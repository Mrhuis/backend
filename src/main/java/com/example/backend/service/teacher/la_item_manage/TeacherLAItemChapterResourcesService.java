package com.example.backend.service.teacher.la_item_manage;

import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;

import java.util.HashMap;

/**
 * 教师端习题章节资源关联服务
 */
public interface TeacherLAItemChapterResourcesService {
    boolean addChapterResources(TeacherLAItemAddDto req);

    boolean updateChapterResources(TeacherLAItemUpdateDto req);

    boolean deleteChapterResources(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}