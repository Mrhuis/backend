package com.example.backend.service.admin.la_item_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.admin.dto.AdminLAItemAddDto;
import com.example.backend.controller.admin.dto.AdminLAItemUpdateDto;

import com.example.backend.entity.ChapterResources;
import com.example.backend.entity.Item;

import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.ItemsMapper;

import com.example.backend.service.admin.la_item_manage.AdminLAItemChapterResourcesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName: ChapterResourcesServiceImpl
 * Package: com.example.backend.service.common.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/9 14:52
 * @Version 1.0
 */
@Service
public class AdminLAItemChapterResourcesServiceImpl implements AdminLAItemChapterResourcesService {

    private static final Logger log = LoggerFactory.getLogger(AdminLAItemChapterResourcesServiceImpl.class);

    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private ItemsMapper itemsMapper;
    @Override
    public boolean addChapterResources(AdminLAItemAddDto req) {
        try {
            // 检查章节列表是否为空
            if (req.getChapter_key() == null || req.getChapter_key().isEmpty()) {
                log.info("章节列表为空，跳过添加章节-资源关联");
                return true; // 没有章节关联，直接返回成功
            }
            
            int successCount = 0;
            for(String cKey : req.getChapter_key()){
                // 检查是否已存在相同的章节-资源关联
                LambdaQueryWrapper<ChapterResources> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ChapterResources::getChapterKey, cKey)
                           .eq(ChapterResources::getResourceType, "item")
                           .eq(ChapterResources::getResourceKey, req.getItemKey());
                
                ChapterResources existingRecord = chapterResourcesMapper.selectOne(queryWrapper);
                if (existingRecord != null) {
                    log.warn("章节-资源关联已存在，跳过插入: chapterKey={}, resourceType=item, resourceKey={}",
                            cKey, req.getItemKey());
                    successCount++; // 已存在也算成功
                    continue;
                }
                
                ChapterResources chapterResources = new ChapterResources();
                chapterResources.setChapterKey(cKey);
                chapterResources.setResourceType("item");
                chapterResources.setResourceKey(req.getItemKey());
                
                // 在循环内插入数据库
                int result = chapterResourcesMapper.insert(chapterResources);
                if (result > 0) {
                    successCount++;
                    log.info("成功添加章节-资源关联: chapterKey={}, resourceType=item, resourceKey={}",
                            cKey, req.getItemKey());
                } else {
                    log.warn("添加章节-资源关联失败: chapterKey={}, resourceType=item, resourceKey={}",
                            cKey, req.getItemKey());
                }
            }
            boolean success = successCount == req.getChapter_key().size();
            if (success) {
                log.info("所有章节-资源关联添加成功，共 {} 条", successCount);
            } else {
                log.error("章节-资源关联添加部分失败，成功 {} 条，总共 {} 条", successCount, req.getChapter_key().size());
            }
            return success;
        } catch (Exception e) {
            log.error("添加章节-资源失败，itemKey: {}, 错误: {}", req.getItemKey(), e.getMessage(), e);
            throw new RuntimeException("添加章节-资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateChapterResources(AdminLAItemUpdateDto req) {
        try {
            // 先删除原有的章节-资源关联
            chapterResourcesMapper.delete(new LambdaQueryWrapper<ChapterResources>()
                    .eq(ChapterResources::getResourceType, "item")
                    .eq(ChapterResources::getResourceKey, req.getItemKey()));
            
            // 如果没有新的章节关联，直接返回成功
            if (req.getChapter_key() == null || req.getChapter_key().isEmpty()) {
                return true;
            }
            
            // 添加新的章节-资源关联
            int successCount = 0;
            for(String cKey : req.getChapter_key()){
                ChapterResources chapterResources = new ChapterResources();
                chapterResources.setChapterKey(cKey);
                chapterResources.setResourceType("item");
                chapterResources.setResourceKey(req.getItemKey());
                
                int result = chapterResourcesMapper.insert(chapterResources);
                if (result > 0) {
                    successCount++;
                }
            }
            return successCount == req.getChapter_key().size();
        } catch (Exception e) {
            throw new RuntimeException("更新章节-资源失败", e);
        }
    }

    @Override
    public boolean deleteChapterResources(Integer id) {
        try {
            Item item = itemsMapper.selectById(id);
            if (item != null) {

                int result = chapterResourcesMapper.delete(new LambdaQueryWrapper<ChapterResources>()
                        .eq(ChapterResources::getResourceType, "item")
                        .eq(ChapterResources::getResourceKey, item.getItemKey()));

                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到ID为 {} 的习题资源，无法删除章节资源关联", id);
            return true; // 媒体资源不存在也返回true，表示操作完成
        } catch (Exception e) {
            log.error("删除章节资源关联失败，id: {}, 错误: {}", id, e.getMessage(), e);
            return false; // 发生异常时返回false
        }
    }

    @Override
    public HashMap<String, String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey) {
        //让chapterResourcesMapper通过resource_key查询相关的记录，并返回一个HashMap，键为chapter_key，值为name
        //但要多表联查,因为name字段在chapter表中
        List<HashMap<String, String>> list = chapterResourcesMapper.selectChapterKeyAndChapterNameByResourceKey(resourceKey,"item");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            result.put(map.get("chapterKey"), map.get("name"));
        }
        return result;
    }
}

