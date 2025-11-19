package com.example.backend.service.teacher.la_item_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;
import com.example.backend.entity.Item;
import com.example.backend.entity.TagResource;
import com.example.backend.mapper.*;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemTagResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 教师端习题标签资源关联服务实现
 */
@Service
public class TeacherLAItemTagResourceServiceImpl implements TeacherLAItemTagResourceService {

    private static final Logger log = LoggerFactory.getLogger(TeacherLAItemTagResourceServiceImpl.class);

    @Autowired
    private TagResourceMapper tagResourceMapper;
    
    @Autowired
    private ItemsMapper itemMapper;
    
    @Autowired
    private TagsMapper tagsMapper;

    @Override
    public boolean addResourceTag(TeacherLAItemAddDto req) {
        try {
            // 检查标签列表是否为空
            if (req.getTagId() == null || req.getTagId().isEmpty()) {
                return true; // 没有标签关联，直接返回成功
            }
            
            int successCount = 0;
            for(Long tId : req.getTagId()){
                // 检查是否已存在相同的标签-资源关联
                LambdaQueryWrapper<TagResource> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TagResource::getTagId, tId)
                           .eq(TagResource::getResourceType, "item")
                           .eq(TagResource::getResourceKey, req.getItemKey());
                
                TagResource existingRecord = tagResourceMapper.selectOne(queryWrapper);
                if (existingRecord != null) {
                    log.warn("标签-资源关联已存在，跳过插入: tagId={}, resourceType=item, resourceKey={}", 
                            tId, req.getItemKey());
                    successCount++; // 已存在也算成功
                    continue;
                }
                
                TagResource tagResource = new TagResource();
                tagResource.setTagId(tId);
                tagResource.setResourceType("item");
                tagResource.setResourceKey(req.getItemKey());

                // 插入数据库
                int result = tagResourceMapper.insert(tagResource);
                if (result > 0) {
                    successCount++;
                    log.info("成功添加标签-资源关联: tagId={}, resourceType=item, resourceKey={}", 
                            tId, req.getItemKey());
                }
            }
            return successCount == req.getTagId().size();
        } catch (Exception e) {
            log.error("添加标签-资源失败，itemKey: {}, 错误: {}", req.getItemKey(), e.getMessage(), e);
            throw new RuntimeException("添加标签-资源失败", e);
        }
    }

    @Override
    public boolean updateResourceTag(TeacherLAItemUpdateDto req) {
        try {
            // 先删除原有的标签-资源关联
            tagResourceMapper.delete(new LambdaQueryWrapper<TagResource>()
                .eq(TagResource::getResourceType, "item")
                .eq(TagResource::getResourceKey, req.getItemKey()));
            
            // 添加新的标签-资源关联 - 添加空值检查
            if (req.getTagId() != null && !req.getTagId().isEmpty()) {
                for(Long tId : req.getTagId()){
                    // 检查标签ID是否存在
                    com.example.backend.entity.Tag tag = tagsMapper.selectById(tId);
                    if (tag != null) {
                        TagResource tagResource = new TagResource();
                        tagResource.setTagId(tId);
                        tagResource.setResourceType("item");
                        tagResource.setResourceKey(req.getItemKey());
                        tagResourceMapper.insert(tagResource);
                    } else {
                        log.warn("标签ID {} 不存在，跳过关联", tId);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("更新标签-资源失败，itemKey: {}, 错误: {}", req.getItemKey(), e.getMessage(), e);
            throw new RuntimeException("更新标签-资源失败", e);
        }
    }

    @Override
    public boolean deleteResourceTag(Integer id) {
        Item item = itemMapper.selectById(id);
        try {
            if (item != null) {
                // 删除表中所有resource_type是item和resource_key是item.getItemKey()的记录
                int result = tagResourceMapper.delete(new LambdaQueryWrapper<TagResource>()
                        .eq(TagResource::getResourceType, "item")
                        .eq(TagResource::getResourceKey, item.getItemKey()));
                log.info("标签资源关联删除完成，itemKey: {}, 删除记录数: {}", item.getItemKey(), result);
                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到Key为 {} 的习题，无法删除标签资源关联", item.getItemKey());
            return true; // 习题不存在也返回true，表示操作完成
        } catch (Exception e) {
            log.error("删除标签资源关联失败，Key: {}, 错误: {}",  item.getItemKey(), e.getMessage(), e);
            return false; // 发生异常时返回false
        }
    }

    @Override
    public HashMap<String, String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey) {
        //让resourceTagMapperr通过resource_key查询相关的记录，并返回一个HashMap，键为id，值为tag_content
        //但要多表联查,因为tag_content字段在tag表中
        List<HashMap<String, String>> list = tagResourceMapper.selectTagIdAndTagContentByResourceKey(resourceKey,"item");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            Object idObj = map.get("id");
            String idStr = idObj != null ? idObj.toString() : "";
            result.put(idStr, map.get("tagContent"));
        }
        return result;
    }
}