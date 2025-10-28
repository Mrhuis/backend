package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSKnowledgeAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/8/29 16:05
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminKSKnowledgeAddDto {

    @JsonAlias({"knowledge_key", "knowledgeKey"})
    private String knowledgeKey;

    private String name;

    // JSON字段，存储前置知识点数组
    private String prerequisite;

    private String uploadedBy;

    private Integer difficulty;

    private String description;

}
