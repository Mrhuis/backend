package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaQueryListDto;
import com.example.backend.controller.teacher.vo.TeacherLAMediaQueryListVo;
import com.example.backend.entity.MediaAssets;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaChapterResourcesService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaKnowledgeResourcesService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaTagResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师端媒体资源管理控制器
 * 负责媒体资源的增删改查功能
 */
@Slf4j
@RestController
@RequestMapping("/api/teacher/lamedia")
public class TeacherLAMediaController {

    @Autowired
    private TeacherLAMediaService teacherLAMediaService;

    @Autowired
    private TeacherLAMediaChapterResourcesService chapterResourcesService;
    
    @Autowired
    private TeacherLAMediaKnowledgeResourcesService knowledgeResourcesService;
    
    @Autowired
    private TeacherLAMediaTagResourceService resourceTagService;

    /**
     * 获取媒体资源列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 媒体资源列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getLAMediaList(@RequestBody TeacherLAMediaQueryListDto req) {
        try {
            log.info("收到获取媒体资源列表请求，参数: {}", req);

            // 获取标签列表
            List<MediaAssets> laMedias = teacherLAMediaService.getLAMediaList(req);
            log.info("获取到媒体资源列表，数量: {}", laMedias.size());

            // 获取总数
            Long total = teacherLAMediaService.getLAMediasCount(req);
            log.info("获取到媒体资源总数: {}", total);

            // 将MediaAssets列表转换为TeacherLAMediaQueryListVo列表，并填充额外字段
            List<TeacherLAMediaQueryListVo> laMediaVos = laMedias.stream().map(mediaAssets -> {
                TeacherLAMediaQueryListVo vo = new TeacherLAMediaQueryListVo();
                // 拷贝MediaAssets的字段到TeacherLAMediaQueryListVo
                BeanUtils.copyProperties(mediaAssets, vo);
                
                // 填充chapterKeyName字段
                vo.setChapterKeyName(chapterResourcesService.selectChapterKeyAndChapterNameByResourceKey(mediaAssets.getMediaKey()));

                // 填充knowledgeKeyName字段
                vo.setKnowledgeKeyName(knowledgeResourcesService.selectKnowledgeKeyAndKnowledgeNameByResourceKey(mediaAssets.getMediaKey()));

                // 填充tagIdName字段
                vo.setTagIdName(resourceTagService.selectTagKeyAndTagNameByResourceKey(mediaAssets.getMediaKey()));
                
                return vo;
            }).collect(Collectors.toList());

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(laMediaVos.stream().map(mediaVo -> (Object) mediaVo).collect(Collectors.toList()));
            result.setTotal(total);
            int pageSize = req.getPageSize() != null ? req.getPageSize() : 100;
            result.setSize(pageSize);
            int currentPage = req.getPageIndex() != null ? req.getPageIndex() : 1;
            result.setCurrent(currentPage);
            result.setPages((int) Math.ceil((double) total / pageSize));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取媒体资源列表失败", e);
            return Result.error("获取媒体资源列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<String> addLAMedia(
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam("mediaKey") String mediaKey,
            @RequestParam("fileName") String fileName,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam(value = "chapter_key", required = false) List<String> chapterKey,
            @RequestParam(value = "knowledge_key", required = false) List<String> knowledgeKey,
            @RequestParam(value = "tagId", required = false) List<Long> tagId,
            @RequestParam(value = "videoUrl", required = false) String videoUrl) {
        try {
            log.info("收到添加媒体资源请求，mediaKey: {}, fileName: {}, videoUrl: {}", mediaKey, fileName, videoUrl);
            
            // 验证：必须提供videoFile或videoUrl其中之一
            if ((videoFile == null || videoFile.isEmpty()) && (videoUrl == null || videoUrl.trim().isEmpty())) {
                return Result.error("必须提供视频文件(videoFile)或视频路径(videoUrl)");
            }
            
            // 构建请求对象
            TeacherLAMediaAddDto req = new TeacherLAMediaAddDto();
            req.setVideoFile(videoFile);
            req.setVideoUrl(videoUrl);
            req.setMediaKey(mediaKey);
            req.setFileName(fileName);
            req.setUploadedBy(uploadedBy);
            req.setChapter_key(chapterKey);
            req.setKnowledge_key(knowledgeKey);
            req.setTagId(tagId);

            // 添加媒体资源
            boolean success = teacherLAMediaService.addLAMediaWithAllRelations(req);

            if (success) {
                log.info("媒体资源添加成功，mediaKey: {}", mediaKey);
                return Result.success("媒体资源创建成功");
            } else {
                log.error("媒体资源添加失败，mediaKey: {}", mediaKey);
                return Result.error("媒体资源创建失败");
            }
        } catch (Exception e) {
            log.error("创建媒体资源时发生错误，mediaKey: {}, 错误: {}", mediaKey, e.getMessage(), e);
            return Result.error("创建媒体资源时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result<String> deleteLAMedia(@PathVariable Integer id) {
        try {
            boolean success = teacherLAMediaService.deleteLAMediaById(id);
            if (success) {
                return Result.success("媒体资源删除成功");
            } else {
                return Result.error("媒体资源删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除媒体资源时发生错误: " + e.getMessage());
        }
    }
}