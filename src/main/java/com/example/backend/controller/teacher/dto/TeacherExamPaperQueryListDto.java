package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 试卷查询条件DTO
 */
@Data
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


}