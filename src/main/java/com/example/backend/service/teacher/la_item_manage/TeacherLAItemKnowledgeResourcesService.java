package com.example.backend.service.teacher.la_item_manage;

import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;

import java.util.HashMap;

/**
 * 教师端习题知识点资源关联服务
 */
public interface TeacherLAItemKnowledgeResourcesService {
    boolean addKnowledgeResources(TeacherLAItemAddDto req);

    boolean updateKnowledgeResources(TeacherLAItemUpdateDto req);

    boolean deleteKnowledgeResources(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}