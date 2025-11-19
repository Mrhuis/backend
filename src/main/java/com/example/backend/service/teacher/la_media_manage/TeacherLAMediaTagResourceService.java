package com.example.backend.service.teacher.la_media_manage;

import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaUpdateDto;

import java.util.HashMap;

/**
 * 教师端媒体资源标签关联服务
 */
public interface TeacherLAMediaTagResourceService {
    boolean addResourceTag(TeacherLAMediaAddDto req);

    boolean updateResourceTag(TeacherLAMediaUpdateDto req);

    boolean deleteResourceTag(Integer id);

    HashMap<String,String> selectTagKeyAndTagNameByResourceKey(String resourceKey);
}