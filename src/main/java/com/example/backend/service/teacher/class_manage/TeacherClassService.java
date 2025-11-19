package com.example.backend.service.teacher.class_manage;

import com.example.backend.controller.teacher.dto.TeacherClassAddDto;
import com.example.backend.controller.teacher.dto.TeacherClassQueryListDto;
import com.example.backend.controller.teacher.dto.TeacherClassUpdateDto;
import com.example.backend.entity.Class;

import java.util.List;

/**
 * 教师端班级管理服务接口
 */
public interface TeacherClassService {
    /**
     * 获取班级列表
     *
     * @param req 查询条件
     * @return 班级列表
     */
    List<Class> getClassList(TeacherClassQueryListDto req);

    /**
     * 获取班级总数
     *
     * @param req 查询条件
     * @return 班级总数
     */
    Long getClassesCount(TeacherClassQueryListDto req);

    /**
     * 添加班级
     *
     * @param req 班级信息
     * @return 是否添加成功
     */
    boolean addClass(TeacherClassAddDto req);

    /**
     * 更新班级
     *
     * @param req 班级信息
     * @return 是否更新成功
     */
    boolean updateClass(TeacherClassUpdateDto req);

    /**
     * 删除班级
     *
     * @param id 班级ID
     * @return 是否删除成功
     */
    boolean deleteClassById(Long id);

    /**
     * 根据ID获取班级详情
     *
     * @param id 班级ID
     * @return 班级详情
     */
    Class getClassById(Long id);
}