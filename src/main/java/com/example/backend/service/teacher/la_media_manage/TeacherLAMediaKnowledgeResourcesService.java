package com.example.backend.service.teacher.la_media_manage;

import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaUpdateDto;

import java.util.HashMap;

/**
 * 教师端媒体资源知识点关联服务
 */
public interface TeacherLAMediaKnowledgeResourcesService {
    boolean addKnowledgeResources(TeacherLAMediaAddDto req);

    boolean updateKnowledgeResources(TeacherLAMediaUpdateDto req);

    boolean deleteKnowledgeResources(Integer id);

    HashMap<String,String> selectKnowledgeKeyAndKnowledgeNameByResourceKey(String resourceKey);
}