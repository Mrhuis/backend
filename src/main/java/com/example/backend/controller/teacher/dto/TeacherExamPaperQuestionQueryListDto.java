package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 试卷题目查询条件DTO
 */
@Data
public class TeacherExamPaperQuestionQueryListDto extends BaseEntity {
    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 题目KEY
     */
    private String itemKey;

    /**
     * 创建者标识（创建试卷的教师user_key）
     */
    private String creatorKey;
}