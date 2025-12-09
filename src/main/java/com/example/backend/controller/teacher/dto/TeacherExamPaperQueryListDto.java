package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeacherExamPaperQueryListDto extends BaseEntity {
    /**
     * 试卷名称（模糊查询）
     */
    private String paperName;

    /**
     * 科目
     */
    private String subject;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 是否启用
     */
    private Integer isEnabled;

    /**
     * 创建者标识（创建试卷的教师user_key）
     */
    private String creatorKey;

    /**
     * 是否只查询已发布的试卷（true-只查询在发布列表中的试卷，false/null-查询所有试卷）
     */
    private Boolean onlyDistributed;
}