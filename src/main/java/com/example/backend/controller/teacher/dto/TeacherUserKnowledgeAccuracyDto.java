package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 教师端查看学生知识点正确率 DTO
 */
@Data
public class TeacherUserKnowledgeAccuracyDto {

    /**
     * 学生标识（users.user_key）
     */
    private String userKey;

    /**
     * 知识点业务标识（knowledges.knowledge_key）
     */
    private String knowledgeKey;

    /**
     * 知识点名称
     */
    private String knowledgeName;

    /**
     * 在统计周期内答对次数
     */
    private Integer correctCount;

    /**
     * 在统计周期内作答总次数
     */
    private Integer totalCount;

    /**
     * 正确率（0-100，保留两位小数）
     */
    private Double accuracy;
}


