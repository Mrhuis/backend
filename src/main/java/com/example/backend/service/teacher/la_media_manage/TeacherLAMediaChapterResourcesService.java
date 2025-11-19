package com.example.backend.service.teacher.la_media_manage;

import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaUpdateDto;

import java.util.HashMap;

/**
 * 教师端媒体资源章节关联服务
 */
public interface TeacherLAMediaChapterResourcesService {
    boolean addChapterResources(TeacherLAMediaAddDto req);

    boolean updateChapterResources(TeacherLAMediaUpdateDto req);

    boolean deleteChapterResources(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}