package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 添加试卷题目DTO
 */
@Data
public class TeacherExamPaperQuestionAddDto {
    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 题目ID
     */
    private Long itemId;

    /**
     * 题目顺序
     */
    private Integer sortNum;

    /**
     * 实际分值
     */
    private Double actualScore;
}