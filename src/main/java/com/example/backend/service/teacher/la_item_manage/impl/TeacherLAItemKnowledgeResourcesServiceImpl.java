package com.example.backend.service.teacher.la_item_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;
import com.example.backend.entity.Item;
import com.example.backend.entity.KnowledgeResources;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemKnowledgeResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 教师端习题知识点资源关联服务实现
 */
@Service
public class TeacherLAItemKnowledgeResourcesServiceImpl implements TeacherLAItemKnowledgeResourcesService {

    private static final Logger log = LoggerFactory.getLogger(TeacherLAItemKnowledgeResourcesServiceImpl.class);

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private ItemsMapper itemMapper;
    
    @Override
    public boolean addKnowledgeResources(TeacherLAItemAddDto req) {
        try {
            // 先删除原有的知识点-资源关联
            knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                    .eq(KnowledgeResources::getResourceType, "item")
                    .eq(KnowledgeResources::getResourceKey, req.getItemKey()));

            // 添加新的知识点-资源关联
            if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
                for(String kKey : req.getKnowledge_key()){
                    KnowledgeResources knowledgeResources = new KnowledgeResources();
                    knowledgeResources.setKnowledgeKey(kKey);
                    knowledgeResources.setResourceType("item");
                    knowledgeResources.setResourceKey(req.getItemKey());
                    knowledgeResourcesMapper.insert(knowledgeResources);
                }
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("添加知识点-资源失败", e);
        }
    }

    @Override
    public boolean updateKnowledgeResources(TeacherLAItemUpdateDto req) {
        try {
            // 先删除原有的知识点-资源关联
            knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                    .eq(KnowledgeResources::getResourceType, "item")
                    .eq(KnowledgeResources::getResourceKey, req.getItemKey()));

            // 添加新的知识点-资源关联 - 添加空值检查
            if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
                for(String kKey : req.getKnowledge_key()){
                    KnowledgeResources knowledgeResources = new KnowledgeResources();
                    knowledgeResources.setKnowledgeKey(kKey);
                    knowledgeResources.setResourceType("item");
                    knowledgeResources.setResourceKey(req.getItemKey());
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
            Item item = itemMapper.selectById(id);
            if (item != null) {
                // 删除表中所有resource_type是item和resource_key是item.getItemKey()的记录
                int result = knowledgeResourcesMapper.delete(new LambdaQueryWrapper<KnowledgeResources>()
                        .eq(KnowledgeResources::getResourceType, "item")
                        .eq(KnowledgeResources::getResourceKey, item.getItemKey()));
                log.info("知识点资源关联删除完成，itemKey: {}, 删除记录数: {}", item.getItemKey(), result);
                return true; // 总是返回true，表示操作完成
            }
            log.warn("未找到ID为 {} 的习题，无法删除知识点资源关联", id);
            return true; // 习题不存在也返回true，表示操作完成
        } catch (Exception e) {
            log.error("删除知识点资源关联失败，id: {}, 错误: {}", id, e.getMessage(), e);
            return false; // 发生异常时返回false
        }
    }

    @Override
    public HashMap<String, String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey) {
        //让knowledgeResourcesMapper通过resource_key查询相关的记录，并返回一个HashMap，键为knowledge_key，值为name
        //但要多表联查,因为name字段在knowledge表中
        List<HashMap<String, String>> list = knowledgeResourcesMapper.selectKnowledgeKeyAndNameByResourceKey(resourceKey,"item");
        HashMap<String, String> result = new HashMap<>();
        for (HashMap<String, String> map : list) {
            result.put(map.get("knowledgeKey"), map.get("name"));
        }
        return result;
    }
}