package com.example.backend.service.teacher.la_media_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaUpdateDto;
import com.example.backend.entity.KnowledgeResources;
import com.example.backend.entity.MediaAssets;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaKnowledgeResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 教师端媒体资源知识点关联服务实现
 */
@Service
public class TeacherLAMediaKnowledgeResourcesServiceImpl implements TeacherLAMediaKnowledgeResourcesService {

    private static final Logger log = LoggerFactory.getLogger(TeacherLAMediaKnowledgeResourcesServiceImpl.class);

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;
    
    @Override
    public boolean addKnowledgeResources(TeacherLAMediaAddDto req) {
        try {
            // 先删除原有的知识点-资源关联
            knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                    .eq(KnowledgeResources::getResourceType, "media")
                    .eq(KnowledgeResources::getResourceKey, req.getMediaKey()));

            // 添加新的知识点-资源关联
            if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
                for(String kKey : req.getKnowledge_key()){
                    KnowledgeResources knowledgeResources = new KnowledgeResources();
                    knowledgeResources.setKnowledgeKey(kKey);
                    knowledgeResources.setResourceType("media");
                    knowledgeResources.setResourceKey(req.getMediaKey());
                    knowledgeResourcesMapper.insert(knowledgeResources);
                }
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("添加知识点-资源失败", e);
        }
    }

    @Override
    public boolean updateKnowledgeResources(TeacherLAMediaUpdateDto req) {
        try {
            // 先删除原有的知识点-资源关联
            knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                    .eq(KnowledgeResources::getResourceType, "media")
                    .eq(KnowledgeResources::getResourceKey, req.getMediaKey()));

            // 添加新的知识点-资源关联 - 添加空值检查
            if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
                for(String kKey : req.getKnowledge_key()){
                    KnowledgeResources knowledgeResources = new KnowledgeResources();
                    knowledgeResources.setKnowledgeKey(kKey);
                    knowledgeResources.setResourceType("media");
                    knowledgeResources.setResourceKey(req.getMediaKey());
                    knowledgeResourcesMapper.insert(knowledgeResources);
                }
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("更新知识点-资源失败", e);
        }
    }

    @Override
    public boolean deleteKnowledgeResources(Integer id) {
        try {
            MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
            if (mediaAssets != null) {
                // 删除表中所有resource_type是media和resource_key是mediaAssets.getMediaKey()的记录
                int result = knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                        .eq(KnowledgeResources::getResourceType, "media")
                        .eq(KnowledgeResources::getResourceKey, mediaAssets.getMediaKey()));
                log.info("知识点资源关联删除完成，mediaKey: {}, 删除记录数: {}", mediaAssets.getMediaKey(), result);
                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到ID为 {} 的媒体资源，无法删除知识点资源关联", id);
            return true; // 媒体资源不存在也返回true，表示操作完成
        } catch (Exception e) {
            log.error("删除知识点资源关联失败，id: {}, 错误: {}", id, e.getMessage(), e);
            return false; // 发生异常时返回false
        }
    }

    @Override
    public HashMap<String, String> selectKnowledgeKeyAndKnowledgeNameByResourceKey(String resourceKey) {
        //让knowledgeResourcesMapper通过resource_key查询相关的记录，并返回一个HashMap，键为knowledge_key，值为name
        //但要多表联查,因为name字段在knowledge表中
        List<HashMap<String, String>> list = knowledgeResourcesMapper.selectKnowledgeKeyAndNameByResourceKey(resourceKey,"media");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            result.put(map.get("knowledgeKey"), map.get("name"));
        }
        return result;
    }
}