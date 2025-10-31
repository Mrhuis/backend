package com.example.backend.service.student.lc_recommend_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.ResourceForm;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.service.student.lc_recommend_learn.StudentLCResourceFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * ClassName: StudentLCResourceFormServiceImpl
 * Package: com.example.backend.service.student.lc_recommend_learn.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/31 9:17
 * @Version 1.0
 */
@Service
public class StudentLCResourceFormServiceImpl implements StudentLCResourceFormService {
    
    @Autowired
    private ResourceFormMapper resourceFormMapper;
    
    @Override
    public String getResourceType(String formKey) {
        QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("form_key", formKey);
        ResourceForm resourceForm = resourceFormMapper.selectOne(queryWrapper);
        return resourceForm != null ? resourceForm.getFormType() : null;
    }
}