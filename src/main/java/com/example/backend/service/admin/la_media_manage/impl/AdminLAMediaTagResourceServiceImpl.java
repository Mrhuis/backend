package com.example.backend.service.admin.la_media_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.TagResource;
import com.example.backend.mapper.*;
import com.example.backend.service.admin.la_media_manage.AdminLAMediaTagResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName: ResourceTagServiceImpl
 * Package: com.example.backend.service.common.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/9 14:53
 * @Version 1.0
 */
@Service
public class AdminLAMediaTagResourceServiceImpl implements AdminLAMediaTagResourceService {

    private static final Logger log = LoggerFactory.getLogger(AdminLAMediaTagResourceServiceImpl.class);

    @Autowired
    private TagResourceMapper tagResourceMapper;
    
    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;
    
    @Autowired
    private TagsMapper tagsMapper;
    
    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;
    
    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Override
    public boolean addResourceTag(AdminLAMediaAddDto req) {
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
                           .eq(TagResource::getResourceType, "media")
                           .eq(TagResource::getResourceKey, req.getMediaKey());
                
                TagResource existingRecord = tagResourceMapper.selectOne(queryWrapper);
                if (existingRecord != null) {
                    log.warn("标签-资源关联已存在，跳过插入: tagId={}, resourceType=media, resourceKey={}", 
                            tId, req.getMediaKey());
                    successCount++; // 已存在也算成功
                    continue;
                }
                
                TagResource tagResource = new TagResource();
                tagResource.setTagId(tId);
                tagResource.setResourceType("media");
                tagResource.setResourceKey(req.getMediaKey());

            // 插入数据库
            int result = tagResourceMapper.insert(tagResource);
                if (result > 0) {
                    successCount++;
                    log.info("成功添加标签-资源关联: tagId={}, resourceType=media, resourceKey={}", 
                            tId, req.getMediaKey());
                }
            }
            return successCount == req.getTagId().size();
        } catch (Exception e) {
            log.error("添加标签-资源失败，mediaKey: {}, 错误: {}", req.getMediaKey(), e.getMessage(), e);
            throw new RuntimeException("添加标签-资源失败", e);
        }
    }

    @Override
    public boolean updateResourceTag(AdminLAMediaUpdateDto req) {
        try {
            // 先删除原有的标签-资源关联
            tagResourceMapper.delete(new LambdaQueryWrapper<TagResource>()
                .eq(TagResource::getResourceType, "media")
                .eq(TagResource::getResourceKey, req.getMediaKey()));
            
            // 添加新的标签-资源关联 - 添加空值检查
            if (req.getTagId() != null && !req.getTagId().isEmpty()) {
                for(Long tId : req.getTagId()){
                    // 检查标签ID是否存在
                    com.example.backend.entity.Tag tag = tagsMapper.selectById(tId);
                    if (tag != null) {
                        TagResource tagResource = new TagResource();
                        tagResource.setTagId(tId);
                        tagResource.setResourceType("media");
                        tagResource.setResourceKey(req.getMediaKey());
                        tagResourceMapper.insert(tagResource);
                    } else {
                        log.warn("标签ID {} 不存在，跳过关联", tId);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("更新标签-资源失败，mediaKey: {}, 错误: {}", req.getMediaKey(), e.getMessage(), e);
            throw new RuntimeException("更新标签-资源失败", e);
        }
    }

    @Override
    public boolean deleteResourceTag(Integer id) {
        MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
        try {
            if (mediaAssets != null) {
                // 删除表中所有resource_type是media和resource_key是mediaAssets.getMediaKey()的记录
                int result = tagResourceMapper.delete(new LambdaQueryWrapper<TagResource>()
                        .eq(TagResource::getResourceType, "media")
                        .eq(TagResource::getResourceKey, mediaAssets.getMediaKey()));
                log.info("标签资源关联删除完成，mediaKey: {}, 删除记录数: {}", mediaAssets.getMediaKey(), result);
                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到Key为 {} 的媒体资源，无法删除标签资源关联", mediaAssets.getMediaKey());
            return true; // 媒体资源不存在也返回true，表示操作完成
        } catch (Exception e) {
            log.error("删除标签资源关联失败，Key: {}, 错误: {}",  mediaAssets.getMediaKey(), e.getMessage(), e);
            return false; // 发生异常时返回false
        }
    }

    @Override
    public HashMap<String, String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey) {
        //让resourceTagMapperr通过resource_key查询相关的记录，并返回一个HashMap，键为id，值为tag_content
        //但要多表联查,因为tag_content字段在tag表中
        List<HashMap<String, String>> list = tagResourceMapper.selectTagIdAndTagContentByResourceKey(resourceKey,"media");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            Object idObj = map.get("id");
            String idStr = idObj != null ? idObj.toString() : "";
            result.put(idStr, map.get("tagContent"));
        }
        return result;
    }
}
