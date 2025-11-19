package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 班级查询条件DTO
 */
@Data
public class TeacherClassQueryListDto extends BaseEntity {
    /**
     * 班级业务唯一标识（如C2024_01）
     */
    private String classKey;

    /**
     * 班级名称（如"高一（3）班"）
     */
    private String name;
}