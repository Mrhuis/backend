package com.example.backend.service.student.lc_autonomous_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.KnowledgeResources;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.service.student.lc_autonomous_learn.StudentLCALKnowledgeResourcesService;
import com.example.backend.service.student.lc_recommend_learn.StudentLCRLKnowledgeResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: StudentLCKnowledgeResourcesServiceImpl
 * Package: com.example.backend.service.student.lc_recommend_learn.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/31 9:19
 * @Version 1.0
 */
@Service
public class StudentLCALKnowledgeResourcesServiceImpl implements StudentLCALKnowledgeResourcesService {
    
    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;
    
    @Override
    public List<String> getKnowledgeKeys(String resourceType, String resourceKey) {
        //创建查询条件
        QueryWrapper<KnowledgeResources> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_type", resourceType).eq("resource_key", resourceKey);
        //载入查询列表的查询条件空位
        List<KnowledgeResources> knowledgeResourcesList = knowledgeResourcesMapper.selectList(queryWrapper);
        //获取知识点列表
        return knowledgeResourcesList.stream()
                .map(KnowledgeResources::getKnowledgeKey)
                .collect(Collectors.toList());
    }
}