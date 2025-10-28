package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.*;
import com.example.backend.controller.admin.vo.AdminLAItemQueryListVo;
import com.example.backend.entity.Item;
import com.example.backend.service.admin.la_item_manage.AdminLAItemChapterResourcesService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemKnowledgeResourcesService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemTagResourceService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminLAItemController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/10 14:21
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/laitem")
public class AdminLAItemController {
    @Autowired
    private AdminLAItemService adminLAItemService;

    @Autowired
    private AdminLAItemChapterResourcesService chapterResourcesService;

    @Autowired
    private AdminLAItemKnowledgeResourcesService knowledgeResourcesService;

    @Autowired
    private AdminLAItemTagResourceService resourceTagService;
    @PostMapping("/list")
    public Result<QueryListVo> getItemList(@RequestBody AdminLAItemQueryListDto req) {
        try {
            log.info("收到获取习题列表请求，参数: {}", req);

            // 获取知识点列表
            List<Item> laItems = adminLAItemService.getItemList(req);
            log.info("获取到习题列表，数量: {}", laItems.size());

            // 获取总数
            Long total = adminLAItemService.getItemCount(req);
            log.info("获取到习题总数: {}", total);


            // 将Items列表转换为AdminLAItemQueryListVo列表，并填充额外字段
            List<AdminLAItemQueryListVo> laItemVos = laItems.stream().map(items -> {
                AdminLAItemQueryListVo vo = new AdminLAItemQueryListVo();
                // 拷贝Items的字段到AdminLAItemQueryListVo
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

//            log.info("返回习题列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取习题列表失败", e);
            return Result.error("获取习题列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")
    public Result addItem(@RequestBody AdminLAItemAddDto req) {
        try {
            boolean success = adminLAItemService.addLAItemWithAllRelations(req);
            if (success) {
                return Result.success("习题创建成功");
            } else {
                return Result.error("习题创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建习题时发生错误: " + e.getMessage());
        }
    }



    @PostMapping("update")
    public Result<String> updateItem(@RequestBody AdminLAItemUpdateDto req) {
        try {
            boolean success = adminLAItemService.updateLAItemWithAllRelations(req);
            if (success) {
                return Result.success("习题更新成功");
            } else {
                return Result.error("习题更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新习题时发生错误: " + e.getMessage());
        }
    }

    @PostMapping("/updateStatus")
    public Result<String> updateItemStatus(@RequestBody AdminLAItemUpdateStatusDto req) {
        try {
            log.info("收到更新习题状态请求，id: {}", req.getId());

            // 使用事务方法一次性处理所有表的更新操作
            boolean success = adminLAItemService.updateLAItemStatus(req);

            if (success) {
                log.info("习题更新成功，id: {}", req.getId());
                return Result.success("习题信息更新成功");
            } else {
                log.error("习题更新失败，id: {}", req.getId());
                return Result.error("习题信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新习题信息时发生错误，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            return Result.error("更新习题信息时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteItem(@PathVariable Integer id) {
        try {
            boolean success = adminLAItemService.deleteItemById(id);
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
            
            // 调用服务保存图片并获取访问URL
            String imageUrl = adminLAItemService.storeImage(file);
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                log.info("图片上传成功，访问URL: {}", imageUrl);
                return Result.success(imageUrl);
            } else {
                log.error("图片保存失败");
                return Result.error("图片保存失败");
            }
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}