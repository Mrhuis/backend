package com.example.backend.controller.student.dto;

import lombok.Data;

@Data
public class StudentAnswerQueryDto {
    /**
     * 用户标识，关联users表的user_key字段
     */
    private String userKey;

    /**
     * 试卷ID，关联exam_paper表的id字段，可为空
     */
    private Long paperId;

    /**
     * 习题标识，关联items表的item_key字段
     */
    private String itemKey;
}