package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 添加班级学生关系DTO
 */
@Data
public class TeacherClassStudentEnrollmentAddDto extends BaseEntity {
    /**
     * 关联班级（classes.class_key）
     */
    private String classKey;

    /**
     * 关联学生（users.user_key）
     */
    private String userKey;

    /**
     * 加入状态：已加入/待审核
     */
    private Integer status;
}