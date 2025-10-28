package com.example.backend.controller.admin.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: a
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/8/29 15:15
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminKSKnowledgeQueryListDto extends BaseEntity {

    @JsonAlias({"knowledge_key", "knowledgeKey"})
    private String knowledgeKey;

    private String name;

    // JSON字段，存储前置知识点数组
//    private String prerequisite;

    private Integer difficulty; // 1-5

//    private String description;

    private String status;
}
