package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 更新试卷题目DTO
 */
@Data
public class TeacherExamPaperQuestionUpdateDto {
    /**
     * 关联ID
     */
    private Long id;

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