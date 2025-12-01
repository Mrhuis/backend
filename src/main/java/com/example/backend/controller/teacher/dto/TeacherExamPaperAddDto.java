package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 添加试卷DTO
 */
@Data
public class TeacherExamPaperAddDto {
    /**
     * 试卷名称
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
     * 试卷总分
     */
    private Double totalScore;

    /**
     * 考试时长（分钟）
     */
    private Integer timeLimit;

    /**
     * 创建者标识（创建试卷的教师user_key）
     */
    private String creatorKey;
}