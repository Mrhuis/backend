package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.controller.teacher.vo.TeacherLAItemQueryListVo;
import com.example.backend.entity.Item;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemChapterResourcesService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemKnowledgeResourcesService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemTagResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师端习题管理控制器
 * 负责习题的增删改查功能
 */
@Slf4j
@RestController
@RequestMapping("/api/teacher/laitem")
public class TeacherLAItemController {

    @Autowired
    private TeacherLAItemService teacherLAItemService;

    @Autowired
    private TeacherLAItemChapterResourcesService chapterResourcesService;
    
    @Autowired
    private TeacherLAItemKnowledgeResourcesService knowledgeResourcesService;
    
    @Autowired
    private TeacherLAItemTagResourceService resourceTagService;

    @PostMapping("/list")
    public Result<QueryListVo> getItemList(@RequestBody TeacherLAItemQueryListDto req) {
        try {
            log.info("收到获取习题列表请求，参数: {}", req);

            // 获取知识点列表
            List<Item> laItems = teacherLAItemService.getItemList(req);
            log.info("获取到习题列表，数量: {}", laItems.size());

            // 获取总数
            Long total = teacherLAItemService.getItemCount(req);
            log.info("获取到习题总数: {}", total);

            // 将Items列表转换为TeacherLAItemQueryListVo列表，并填充额外字段
            List<TeacherLAItemQueryListVo> laItemVos = laItems.stream().map(items -> {
                TeacherLAItemQueryListVo vo = new TeacherLAItemQueryListVo();
                // 拷贝Items的字段到TeacherLAItemQueryListVo
                BeanUtils.copyProperties(items, vo);

                // 填充chapterKeyName字段
                vo.setChapterKeyName(chapterResourcesService.selectChapterKeyAndChapterNameByResourceKey(items.getItemKey()));

                // 填充knowledgeKeyName字段
                vo.setKnowledgeKeyName(knowledgeResourcesService.selectChapterKeyAndChapterNameByResourceKey(items.getItemKey()));

                // 填充tagIdName字段
                vo.setTagIdName(resourceTagService.selectChapterKeyAndChapterNameByResourceKey(items.getItemKey()));

                return vo;
            }).collect(Collectors.toList());

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(laItemVos.stream().map(itemVo -> (Object) itemVo).collect(Collectors.toList()));
            result.setTotal(total);
            int pageSize = req.getPageSize() != null ? req.getPageSize() : 100;
            result.setSize(pageSize);
            int currentPage = req.getPageIndex() != null ? req.getPageIndex() : 1;
            result.setCurrent(currentPage);
            result.setPages((int) Math.ceil((double) total / pageSize));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取习题列表失败", e);
            return Result.error("获取习题列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addItem(@RequestBody TeacherLAItemAddDto req) {
        try {
            boolean success = teacherLAItemService.addItemWithAllRelations(req);
            if (success) {
                return Result.success("习题创建成功");
            } else {
                return Result.error("习题创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建习题时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteItem(@PathVariable Integer id) {
        try {
            boolean success = teacherLAItemService.deleteItemById(id);
            if (success) {
                return Result.success("习题删除成功");
            } else {
                return Result.error("习题删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除习题时发生错误: " + e.getMessage());
        }
    }

    /**
     * 上传图片文件
     * 
     * @param file 上传的图片文件
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload-image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("收到图片上传请求，文件名: {}, 文件大小: {}", file.getOriginalFilename(), file.getSize());
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                log.warn("上传的文件为空");
                return Result.error("上传的文件不能为空");
            }
            
            // 检查文件类型（只允许图片格式）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("上传的文件不是图片格式，文件类型: {}", contentType);
                return Result.error("只允许上传图片文件");
            }
            
            // 教师端暂时不支持图片存储
            log.warn("教师端暂时不支持图片存储");
            return Result.error("教师端暂时不支持图片存储");
            
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}