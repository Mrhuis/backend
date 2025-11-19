package com.example.backend.service.teacher.la_item_manage;

import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;

import java.util.HashMap;

/**
 * 教师端习题标签资源关联服务
 */
public interface TeacherLAItemTagResourceService {
    boolean addResourceTag(TeacherLAItemAddDto req);

    boolean updateResourceTag(TeacherLAItemUpdateDto req);

    boolean deleteResourceTag(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}