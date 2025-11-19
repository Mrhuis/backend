package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 试卷下发查询条件DTO
 */
@Data
public class TeacherExamPaperDistributionQueryListDto extends BaseEntity {
    /**
     * 关联班级表的班级Key（哪个班级被下发试卷）
     */
    private String classKey;

    /**
     * 关联试卷表的试卷ID（下发的是哪份试卷）
     */
    private Long paperId;

    /**
     * 试卷开始时间（允许学生开始作答的时间，可等于下发时间）
     */
    private LocalDateTime startTime;

    /**
     * 下发人ID（关联用户表，通常是老师/管理员）
     */
    private Long distributorId;

    /**
     * 是否已回收（0-未回收，1-已回收，回收后学生无法再提交）
     */
    private Integer isRecycled;
}