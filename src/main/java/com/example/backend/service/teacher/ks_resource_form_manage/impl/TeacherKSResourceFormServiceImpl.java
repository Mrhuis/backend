package com.example.backend.service.teacher.ks_resource_form_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherKSResourceFormAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSResourceFormQueryListDto;
import com.example.backend.entity.Plugin;
import com.example.backend.entity.ResourceForm;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.ks_resource_form_manage.TeacherKSResourceFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherKSResourceFormServiceImpl implements TeacherKSResourceFormService {

    @Autowired
    private ResourceFormMapper resourceFormMapper;
    
    @Autowired
    private PluginService pluginService;

    @Override
    public List<ResourceForm> getResourceFormList(TeacherKSResourceFormQueryListDto req) {
        QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<ResourceForm> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return resourceFormMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getResourceFormCount(TeacherKSResourceFormQueryListDto req) {
        QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        return resourceFormMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addResourceForm(TeacherKSResourceFormAddDto req) {
        // 获取当前启用的插件
        Plugin enabledPlugin = pluginService.getEnabledPlugin();
        if (enabledPlugin == null) {
            throw new RuntimeException("没有启用的插件，无法添加资源形式");
        }
        
        ResourceForm resourceForm = new ResourceForm();
        resourceForm.setFormKey(req.getKey());
        resourceForm.setFormName(req.getName());
        resourceForm.setDescription(req.getDescription());
        resourceForm.setFormType(req.getFormType());
        resourceForm.setPluginKey(enabledPlugin.getPluginKey());
        resourceForm.setUploadedBy(req.getUploadedBy());
        resourceForm.setStatus("PENDING"); // 教师端添加的资源形式默认状态为PENDING
        return resourceFormMapper.insert(resourceForm) > 0;
    }

    @Override
    public boolean deleteResourceFormById(Integer id) {
        return resourceFormMapper.deleteById(id) > 0;
    }
}