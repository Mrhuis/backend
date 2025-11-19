package com.example.backend.service.teacher.class_manage;

import com.example.backend.controller.teacher.dto.TeacherClassStudentEnrollmentAddDto;
import com.example.backend.controller.teacher.dto.TeacherClassStudentEnrollmentQueryListDto;
import com.example.backend.controller.teacher.dto.TeacherClassStudentEnrollmentUpdateDto;
import com.example.backend.entity.ClassStudentEnrollment;

import java.util.List;

/**
 * 教师端班级学生关系管理服务接口
 */
public interface TeacherClassStudentEnrollmentService {
    /**
     * 获取班级学生关系列表
     *
     * @param req 查询条件
     * @return 班级学生关系列表
     */
    List<ClassStudentEnrollment> getClassStudentEnrollmentList(TeacherClassStudentEnrollmentQueryListDto req);

    /**
     * 获取班级学生关系总数
     *
     * @param req 查询条件
     * @return 班级学生关系总数
     */
    Long getClassStudentEnrollmentsCount(TeacherClassStudentEnrollmentQueryListDto req);

    /**
     * 添加班级学生关系
     *
     * @param req 班级学生关系信息
     * @return 是否添加成功
     */
    boolean addClassStudentEnrollment(TeacherClassStudentEnrollmentAddDto req);

    /**
     * 更新班级学生关系
     *
     * @param req 班级学生关系信息
     * @return 是否更新成功
     */
    boolean updateClassStudentEnrollment(TeacherClassStudentEnrollmentUpdateDto req);

    /**
     * 删除班级学生关系
     *
     * @param id 班级学生关系ID
     * @return 是否删除成功
     */
    boolean deleteClassStudentEnrollmentById(Long id);

    /**
     * 根据ID获取班级学生关系详情
     *
     * @param id 班级学生关系ID
     * @return 班级学生关系详情
     */
    ClassStudentEnrollment getClassStudentEnrollmentById(Long id);
}