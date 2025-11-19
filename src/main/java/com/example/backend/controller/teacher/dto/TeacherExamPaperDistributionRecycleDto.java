package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 试卷回收DTO
 */
@Data
public class TeacherExamPaperDistributionRecycleDto extends BaseEntity {
    /**
     * 数据库自增ID
     */
    private Long id;

    /**
     * 是否已回收（0-未回收，1-已回收，回收后学生无法再提交）
     */
    private Integer isRecycled;
}