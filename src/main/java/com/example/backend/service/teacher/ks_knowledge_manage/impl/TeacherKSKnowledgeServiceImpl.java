package com.example.backend.service.teacher.ks_knowledge_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeQueryListDto;
import com.example.backend.entity.Knowledge;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.KnowledgesMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.ks_knowledge_manage.TeacherKSKnowledgeService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherKSKnowledgeServiceImpl implements TeacherKSKnowledgeService {

    @Autowired
    private KnowledgesMapper knowledgesMapper;
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;

    @Override
    public List<Knowledge> getKnowledgeList(TeacherKSKnowledgeQueryListDto req) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("knowledge_key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        // 过滤条件：只显示状态为ENABLED或创建者为当前用户的数据
        if (req.getUserKey() != null && !req.getUserKey().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .eq("status", "ENABLED")
                .or()
                .eq("uploaded_by", req.getUserKey())
            );
        } else {
            // 如果没有提供userKey，只显示ENABLED状态的数据
            queryWrapper.eq("status", "ENABLED");
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<Knowledge> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return knowledgesMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getKnowledgesCount(TeacherKSKnowledgeQueryListDto req) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("knowledge_key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        // 过滤条件：只显示状态为ENABLED或创建者为当前用户的数据
        if (req.getUserKey() != null && !req.getUserKey().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .eq("status", "ENABLED")
                .or()
                .eq("uploaded_by", req.getUserKey())
            );
        } else {
            // 如果没有提供userKey，只显示ENABLED状态的数据
            queryWrapper.eq("status", "ENABLED");
        }
        
        return knowledgesMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addKnowledge(TeacherKSKnowledgeAddDto req) {
        // 获取当前启用的插件
        Plugin enabledPlugin = pluginService.getEnabledPlugin();
        
        Knowledge knowledge = new Knowledge();
        BeanUtils.copyProperties(req, knowledge);
        knowledge.setKnowledgeKey(req.getKey());
        knowledge.setPluginKey(enabledPlugin != null ? enabledPlugin.getPluginKey() : "");
        knowledge.setStatus("PENDING"); // 教师端添加的知识点默认状态为PENDING
        boolean inserted = knowledgesMapper.insert(knowledge) > 0;
        if (inserted) {
            resourceAuditNotifier.notifyPendingAudit(
                    req.getUploadedBy(),
                    "知识点资源",
                    knowledge.getName()
            );
        }
        return inserted;
    }

    @Override
    public boolean deleteKnowledgeById(Integer id) {
        return knowledgesMapper.deleteById(id) > 0;
    }
}