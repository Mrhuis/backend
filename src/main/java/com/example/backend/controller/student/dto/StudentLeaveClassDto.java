package com.example.backend.controller.student.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生退出班级DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentLeaveClassDto extends BaseEntity {
    /**
     * 班级标识
     */
    private String classKey;

    /**
     * 学生userKey
     */
    private String userKey;
}


