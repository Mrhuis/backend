package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaQueryListDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;
import com.example.backend.controller.admin.vo.AdminLAMediaQueryListVo;
import com.example.backend.entity.MediaAssets;
import com.example.backend.service.admin.la_media_manage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin/lamedia")
public class AdminLAMediaController {

    @Autowired
    private AdminLAMediaService adminLAMediaService;

    @Autowired
    private AdminLAMediaChapterResourcesService chapterResourcesService;

    @Autowired
    private AdminLAMediaKnowledgeResourcesService knowledgeResourcesService;

    @Autowired
    private AdminLAMediaTagResourceService resourceTagService;

    /**
     * 获取视频信息列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 视频信息列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getLAMediaList(@RequestBody AdminLAMediaQueryListDto req) {
        try {
            log.info("收到获取视频信息列表请求，参数: {}", req);

            // 获取标签列表
            List<MediaAssets> laMedias = adminLAMediaService.getLAMediaList(req);
            log.info("获取到视频信息列表，数量: {}", laMedias.size());

            // 获取总数
            Long total = adminLAMediaService.getLAMediasCount(req);
            log.info("获取到视频信息总数: {}", total);

            // 将MediaAssets列表转换为AdminLAMediaQueryListVo列表，并填充额外字段
            List<AdminLAMediaQueryListVo> laMediaVos = laMedias.stream().map(mediaAssets -> {
                AdminLAMediaQueryListVo vo = new AdminLAMediaQueryListVo();
                // 拷贝MediaAssets的字段到AdminLAMediaQueryListVo
                BeanUtils.copyProperties(mediaAssets, vo);
                
                // 填充chapterKeyName字段
                vo.setChapterKeyName(chapterResourcesService.selectChapterKeyAndChapterNameByResourceKey(mediaAssets.getMediaKey()));
                
                // 填充knowledgeKeyName字段
                vo.setKnowledgeKeyName(knowledgeResourcesService.selectChapterKeyAndChapterNameByResourceKey(mediaAssets.getMediaKey()));
                
                // 填充tagIdName字段
                vo.setTagIdName(resourceTagService.selectChapterKeyAndChapterNameByResourceKey(mediaAssets.getMediaKey()));
                
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

//            log.info("返回视频信息列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取视频信息列表失败", e);
            return Result.error("获取视频信息列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")

    public Result addLAMedia(
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("mediaKey") String mediaKey,
            @RequestParam("fileName") String fileName,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam(value = "chapter_key", required = false) List<String> chapterKey,
            @RequestParam(value = "knowledge_key", required = false) List<String> knowledgeKey,
            @RequestParam(value = "tagId", required = false) List<Long> tagId) {
        try {
            log.info("收到添加视频请求，mediaKey: {}, fileName: {}", mediaKey, fileName);
            
            // 构建请求对象
            AdminLAMediaAddDto req = new AdminLAMediaAddDto();
            req.setVideoFile(videoFile);
            req.setMediaKey(mediaKey);
            req.setFileName(fileName);
            req.setUploadedBy(uploadedBy);
            req.setChapter_key(chapterKey);
            req.setKnowledge_key(knowledgeKey);
            req.setTagId(tagId);

            
            // 使用事务方法一次性处理所有表的操作
            boolean success = adminLAMediaService.addLAMediaWithAllRelations(req);
            
            if (success) {
                log.info("视频添加成功，mediaKey: {}", mediaKey);
                return Result.success("视频信息创建成功");
            } else {
                log.error("视频添加失败，mediaKey: {}", mediaKey);
                return Result.error("视频信息创建失败");
            }
        } catch (Exception e) {
            log.error("创建视频信息时发生错误，mediaKey: {}, 错误: {}", mediaKey, e.getMessage(), e);
            return Result.error("创建视频信息时发生错误: " + e.getMessage());
        }
    }


    @PostMapping("/update")
    public Result<String> updateLAMedia(
            @RequestParam("id") Long id,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "mediaKey", required = false) String mediaKey,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "chapter_key", required = false) List<String> chapterKey,
            @RequestParam(value = "knowledge_key", required = false) List<String> knowledgeKey,
            @RequestParam(value = "tagId", required = false) List<Long> tagId) {
        try {
            log.info("收到更新视频请求，id: {}", id);
            
            // 构建DTO对象
            AdminLAMediaUpdateDto req = new AdminLAMediaUpdateDto();
            req.setId(id);
            req.setVideoFile(videoFile);
            req.setMediaKey(mediaKey);
            req.setFileName(fileName);
            req.setUrl(url);
            req.setStatus(status);
            req.setChapter_key(chapterKey);
            req.setKnowledge_key(knowledgeKey);
            req.setTagId(tagId);

            // 使用事务方法一次性处理所有表的更新操作
            boolean success = adminLAMediaService.updateLAMediaWithAllRelations(req);

            if (success) {
                log.info("视频更新成功，id: {}", id);
                return Result.success("视频信息更新成功");
            } else {
                log.error("视频更新失败，id: {}", id);
                return Result.error("视频信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新视频信息时发生错误，id: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("更新视频信息时发生错误: " + e.getMessage());
        }
    }


    @PostMapping("/updateStatus")
    public Result<String> updateLAMedia(
            @RequestParam("id") Long id,
            @RequestParam(value = "status", required = false) String status) {
        try {
            log.info("收到更新视频状态请求，id: {}", id);

            // 使用事务方法一次性处理所有表的更新操作
            boolean success = adminLAMediaService.updateLAMediaStatus(id,status);

            if (success) {
                log.info("视频更新成功，id: {}", id);
                return Result.success("视频信息更新成功");
            } else {
                log.error("视频更新失败，id: {}", id);
                return Result.error("视频信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新视频信息时发生错误，id: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("更新视频信息时发生错误: " + e.getMessage());
        }
    }




    @GetMapping("/delete/{id}")
    public Result deleteLAMedia(@PathVariable Integer id) {
        try {
            boolean mainMediaDeleted = false;
            boolean chapterResourcesDeleted = false;
            boolean knowledgeResourcesDeleted = false;
            boolean resourceTagDeleted = false;
            StringBuilder errorMsg = new StringBuilder();
            


            //删除chapterResources对象
            try {
                chapterResourcesDeleted = chapterResourcesService.deleteChapterResources(id);
                if (!chapterResourcesDeleted) {
                    log.warn("章节资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("章节资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除章节资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除章节资源失败，id: {}", id, e);
            }

            //删除knowledgeResources对象
            try {
                knowledgeResourcesDeleted = knowledgeResourcesService.deleteKnowledgeResources(id);
                if (!knowledgeResourcesDeleted) {
                    log.warn("知识点资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("知识点资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除知识点资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除知识点资源失败，id: {}", id, e);
            }

            //删除resourceTag对象
            try {
                resourceTagDeleted = resourceTagService.deleteResourceTag(id);
                if (!resourceTagDeleted) {
                    log.warn("标签资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("标签资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除标签资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除标签资源失败，id: {}", id, e);
            }

            //删除media对象（主资源）
            try {
                mainMediaDeleted = adminLAMediaService.deleteLAMediaById(id);
                if (!mainMediaDeleted) {
                    errorMsg.append("主媒体资源不存在或已删除; ");
                    log.warn("主媒体资源不存在或已删除，id: {}", id);
                } else {
                    log.info("主媒体资源删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除主媒体资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除主媒体资源失败，id: {}", id, e);
            }

            return Result.success("视频信息删除成功");

        } catch (Exception e) {
            log.error("删除视频信息时发生未知错误，id: {}", id, e);
            return Result.error("删除视频信息时发生错误: " + e.getMessage());
        }
    }
}