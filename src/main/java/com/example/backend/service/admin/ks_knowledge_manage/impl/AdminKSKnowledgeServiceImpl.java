package com.example.backend.service.admin.ks_knowledge_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeAddDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeUpdateDto;
import com.example.backend.entity.Knowledge;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.KnowledgesMapper;
import com.example.backend.service.admin.ks_knowledge_manage.AdminKSKnowledgeService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: KnowledgeServiceImpl
 * Package: com.example.backend.service.admin.ks_knowledge_manage.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/8/29 15:40
 * @Version 1.0
 */
@Service
public class AdminKSKnowledgeServiceImpl implements AdminKSKnowledgeService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminKSKnowledgeServiceImpl.class);

    @Autowired
    private KnowledgesMapper knowledgesMapper;
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;

    @Override
    public List<Knowledge> getKnowledgeList(AdminKSKnowledgeQueryListDto req) {
        //分页查询，获取对应查找条件数据，req中并不是每个字段都有值，所以要判断
        //先调用PluginService.getEnabledPlugin(), 获取插件对象，获取插件对象中的plugin_key
        //当前方法返回的knowledge数据的plugin_key必须是插件对象中的plugin_key
        
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取知识点列表 - 当前启用的插件: {}", enabledPlugin);
            
            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }
            
            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());
            
            QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
            
            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());
            
            // 添加查询条件（与getKnowledgesCount保持一致）
            if (req != null) {
                // 知识点标识模糊查询
                if (StringUtils.hasText(req.getKnowledgeKey())) {
                    queryWrapper.like("knowledge_key", req.getKnowledgeKey());
                }
                
                // 知识点名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }
                
                // 难度精确查询
                if (req.getDifficulty() != null) {
                    queryWrapper.eq("difficulty", req.getDifficulty());
                }
                
                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }
                
                // 描述模糊查询
//                if (StringUtils.hasText(req.getDescription())) {
//                    queryWrapper.like("description", req.getDescription());
//                }
//
                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("knowledge_key", searchValue)
                            .or()
                            .like("name", searchValue)
                            .or()
                            .like("description", searchValue)
                            .or()
                            .like("prerequisite", searchValue)
                            .or()
                            .like("uploaded_by", searchValue)
                    );
                }
            }
            
            // 按创建时间倒序排列（如果有created_at字段）
            queryWrapper.orderByDesc("id");
            
            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }
            
            log.info("执行查询，SQL条件: {}", queryWrapper.getTargetSql());
            
            List<Knowledge> result = knowledgesMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());
            
            return result;
        } catch (Exception e) {
            log.error("获取知识点列表失败", e);
            throw new RuntimeException("获取知识点列表失败", e);
        }
    }

    @Override
    public Long getKnowledgesCount(AdminKSKnowledgeQueryListDto req) {
        //分页查询，获取对应查找条件数据，req中并不是每个字段都有值，所以要判断
        //先调用PluginService.getEnabledPlugin(), 获取插件对象，获取插件对象中的plugin_key
        //当前方法返回的knowledge数据的plugin_key必须是插件对象中的plugin_key
        
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取知识点总数 - 当前启用的插件: {}", enabledPlugin);
            
            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回0");
                return 0L; // 如果没有启用的插件，返回0
            }
            
            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());
            
            QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
            
            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());
            
            // 添加查询条件（与getKnowledgeList保持一致，但不包含分页和排序）
            if (req != null) {
                // 知识点标识模糊查询
                if (StringUtils.hasText(req.getKnowledgeKey())) {
                    queryWrapper.like("knowledge_key", req.getKnowledgeKey());
                }
                
                // 知识点名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }
                
                // 难度精确查询
                if (req.getDifficulty() != null) {
                    queryWrapper.eq("difficulty", req.getDifficulty());
                }
                
                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }
                
                // 描述模糊查询
//                if (StringUtils.hasText(req.getDescription())) {
//                    queryWrapper.like("description", req.getDescription());
//                }
                
                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                        .like("knowledge_key", searchValue)
                        .or()
                        .like("name", searchValue)
                        .or()
                        .like("description", searchValue)
                        .or()
                        .like("prerequisite", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                    );
                }
            }
            
            log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
            
            Long count = knowledgesMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);
            
            return count;
        } catch (Exception e) {
            log.error("获取知识点总数失败", e);
            throw new RuntimeException("获取知识点总数失败", e);
        }
    }

    @Override
    public boolean addKnowledge(AdminKSKnowledgeAddDto req) {
        //添加的时候，默认status是DISABLED，plugin_key必须是PluginService.getEnabledPlugin(), 获取插件对象，获取插件对象中的plugin_key
        
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加知识点");
            }
            
            // 创建知识点对象
            Knowledge knowledge = new Knowledge();
            knowledge.setKnowledgeKey(req.getKnowledgeKey());
            knowledge.setPluginKey(enabledPlugin.getPluginKey());
            knowledge.setName(req.getName());
            knowledge.setPrerequisite(req.getPrerequisite());
            knowledge.setDifficulty(req.getDifficulty());
            knowledge.setDescription(req.getDescription());
            knowledge.setUploadedBy(req.getUploadedBy());
            knowledge.setStatus("DISABLED"); // 默认状态
            
            // 插入数据库
            int result = knowledgesMapper.insert(knowledge);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加知识点失败", e);
        }
    }

    @Override
    public boolean updateKnowledge(AdminKSKnowledgeUpdateDto req) {
        //更新时，先根据id查询出对应的knowledge对象，再根据req更新knowledge对象，req中有非空/0的字段，进行对应字段的更新。
        
        try {
            // 先查询知识点是否存在
            Knowledge existingKnowledge = knowledgesMapper.selectById(req.getId());
            if (existingKnowledge == null) {
                throw new RuntimeException("知识点不存在");
            }
            
            // 构建更新条件
            UpdateWrapper<Knowledge> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());
            
            // 只更新非空/非0的字段

            if (StringUtils.hasText(req.getName())) {
                updateWrapper.set("name", req.getName());
            }
            if (StringUtils.hasText(req.getPrerequisite())) {
                updateWrapper.set("prerequisite", req.getPrerequisite());
            }
            if (req.getDifficulty() != null && req.getDifficulty() > 0) {
                updateWrapper.set("difficulty", req.getDifficulty());
            }
            if (StringUtils.hasText(req.getDescription())) {
                updateWrapper.set("description", req.getDescription());
            }
            if (StringUtils.hasText(req.getStatus())) {
                updateWrapper.set("status", req.getStatus());
            }
            
            // 执行更新
            int result = knowledgesMapper.update(null, updateWrapper);

            // 审核结果通知上传者（知识点资源），仅在状态发生变更且存在上传者时发送
            if (result > 0
                    && StringUtils.hasText(existingKnowledge.getUploadedBy())
                    && StringUtils.hasText(req.getStatus())
                    && !req.getStatus().equals(existingKnowledge.getStatus())) {
                resourceAuditNotifier.notifyAuditResult(
                        existingKnowledge.getUploadedBy(),
                        "知识点资源",
                        existingKnowledge.getName(),
                        req.getStatus(),
                        null  // 暂无管理员user_key，使用system作为发送者
                );
            }

            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新知识点失败", e);
        }
    }

    @Override
    public boolean deleteKnowledgeById(Integer id) {
        //根据id删除对应数据，但是被删除数据的knowledge_key可能是该表别的数据中prerequisite的组成部分。故，也要删除别的数据中的prerequisite中该knowledge_key。（prerequisite其中数据类似["K_007_stack_seq", "K_008_stack_link"]）
        
        try {
            // 先查询要删除的知识点
            Knowledge knowledgeToDelete = knowledgesMapper.selectById(id);
            if (knowledgeToDelete == null) {
                throw new RuntimeException("知识点不存在");
            }
            
            String knowledgeKeyToDelete = knowledgeToDelete.getKnowledgeKey();
            
            // 查询所有包含该知识点作为前置条件的知识点
            List<Knowledge> relatedKnowledges = knowledgesMapper.selectList(
                new QueryWrapper<Knowledge>().like("prerequisite", knowledgeKeyToDelete)
            );
            
            // 更新相关知识点，从prerequisite中移除被删除的知识点
            for (Knowledge related : relatedKnowledges) {
                try {
                    if (StringUtils.hasText(related.getPrerequisite())) {
                        // 解析JSON数组
                        List<String> prerequisites = objectMapper.readValue(
                            related.getPrerequisite(), 
                            new TypeReference<List<String>>() {}
                        );
                        
                        // 移除被删除的知识点
                        prerequisites.remove(knowledgeKeyToDelete);
                        
                        // 更新prerequisite字段
                        UpdateWrapper<Knowledge> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", related.getId());
                        updateWrapper.set("prerequisite", objectMapper.writeValueAsString(prerequisites));
                        knowledgesMapper.update(null, updateWrapper);
                    }
                } catch (JsonProcessingException e) {
                    // JSON解析失败，跳过这个知识点
                    continue;
                }
            }
            
            // 删除目标知识点
            int result = knowledgesMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除知识点失败", e);
        }
    }
}
