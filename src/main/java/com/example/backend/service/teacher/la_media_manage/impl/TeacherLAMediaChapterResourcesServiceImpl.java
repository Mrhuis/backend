package com.example.backend.service.teacher.la_media_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaUpdateDto;
import com.example.backend.entity.ChapterResources;
import com.example.backend.entity.MediaAssets;
import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaChapterResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 教师端媒体资源章节关联服务实现
 */
@Service
public class TeacherLAMediaChapterResourcesServiceImpl implements TeacherLAMediaChapterResourcesService {

    private static final Logger log = LoggerFactory.getLogger(TeacherLAMediaChapterResourcesServiceImpl.class);

    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;
    
    @Override
    public boolean addChapterResources(TeacherLAMediaAddDto req) {
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
                           .eq(ChapterResources::getResourceType, "media")
                           .eq(ChapterResources::getResourceKey, req.getMediaKey());
                
                ChapterResources existingRecord = chapterResourcesMapper.selectOne(queryWrapper);
                if (existingRecord != null) {
                    log.warn("章节-资源关联已存在，跳过插入: chapterKey={}, resourceType=media, resourceKey={}", 
                            cKey, req.getMediaKey());
                    successCount++; // 已存在也算成功
                    continue;
                }
                
                ChapterResources chapterResources = new ChapterResources();
                chapterResources.setChapterKey(cKey);
                chapterResources.setResourceType("media");
                chapterResources.setResourceKey(req.getMediaKey());
                
                // 在循环内插入数据库
                int result = chapterResourcesMapper.insert(chapterResources);
                if (result > 0) {
                    successCount++;
                    log.info("成功添加章节-资源关联: chapterKey={}, resourceType=media, resourceKey={}", 
                            cKey, req.getMediaKey());
                } else {
                    log.warn("添加章节-资源关联失败: chapterKey={}, resourceType=media, resourceKey={}", 
                            cKey, req.getMediaKey());
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
            log.error("添加章节-资源失败，mediaKey: {}, 错误: {}", req.getMediaKey(), e.getMessage(), e);
            throw new RuntimeException("添加章节-资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateChapterResources(TeacherLAMediaUpdateDto req) {
        try {
            // 先删除原有的章节-资源关联
            chapterResourcesMapper.delete(new LambdaQueryWrapper<ChapterResources>()
                    .eq(ChapterResources::getResourceType, "media")
                    .eq(ChapterResources::getResourceKey, req.getMediaKey()));
            
            // 如果没有新的章节关联，直接返回成功
            if (req.getChapter_key() == null || req.getChapter_key().isEmpty()) {
                return true;
            }
            
            // 添加新的章节-资源关联
            int successCount = 0;
            for(String cKey : req.getChapter_key()){
                ChapterResources chapterResources = new ChapterResources();
                chapterResources.setChapterKey(cKey);
                chapterResources.setResourceType("media");
                chapterResources.setResourceKey(req.getMediaKey());
                
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
            MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
            if (mediaAssets != null) {
                //删除表中所有resource_type是media和resource_key是mediaAssets.getMediaKey()的记录
                int result = chapterResourcesMapper.delete(new LambdaQueryWrapper<ChapterResources>()
                        .eq(ChapterResources::getResourceType, "media")
                        .eq(ChapterResources::getResourceKey, mediaAssets.getMediaKey()));
                log.info("章节资源关联删除完成，mediaKey: {}, 删除记录数: {}", mediaAssets.getMediaKey(), result);
                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到ID为 {} 的媒体资源，无法删除章节资源关联", id);
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
        List<HashMap<String, String>> list = chapterResourcesMapper.selectChapterKeyAndChapterNameByResourceKey(resourceKey,"media");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            result.put(map.get("chapterKey"), map.get("name"));
        }
        return result;
    }
}