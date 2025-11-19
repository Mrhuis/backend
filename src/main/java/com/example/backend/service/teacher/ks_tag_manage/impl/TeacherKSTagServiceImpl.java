package com.example.backend.service.teacher.ks_tag_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherKSTagAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSTagQueryListDto;
import com.example.backend.entity.Plugin;
import com.example.backend.entity.Tag;
import com.example.backend.mapper.TagsMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.ks_tag_manage.TeacherKSTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherKSTagServiceImpl implements TeacherKSTagService {

    @Autowired
    private TagsMapper tagsMapper;
    
    @Autowired
    private PluginService pluginService;

    @Override
    public List<Tag> getTagList(TeacherKSTagQueryListDto req) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<Tag> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return tagsMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getTagsCount(TeacherKSTagQueryListDto req) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        return tagsMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addTag(TeacherKSTagAddDto req) {
        // 获取当前启用的插件
        Plugin enabledPlugin = pluginService.getEnabledPlugin();
        if (enabledPlugin == null) {
            throw new RuntimeException("没有启用的插件，无法添加标签");
        }
        
        Tag tag = new Tag();
        tag.setPluginKey(enabledPlugin.getPluginKey());
        tag.setTagApplicableType(req.getTagApplicableType());
        // DTO 中的 name 字段对应实体类的 tagContent 字段
        tag.setTagContent(req.getName());
        tag.setTagDesc(req.getDescription());
        tag.setUploadedBy(req.getUploadedBy());
        tag.setStatus("PENDING"); // 教师端添加的标签默认状态为PENDING
        
        return tagsMapper.insert(tag) > 0;
    }

    @Override
    public boolean deleteTagById(Integer id) {
        return tagsMapper.deleteById(id) > 0;
    }
}