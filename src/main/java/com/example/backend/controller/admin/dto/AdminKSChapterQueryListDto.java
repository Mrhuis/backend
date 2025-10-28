package com.example.backend.controller.admin.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: AdminKSChapterQueryListDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/2 11:00
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminKSChapterQueryListDto extends BaseEntity {


    @JsonAlias({"chapter_key", "chapterKey"})
    private String chapterKey;

    private String name;

    private Integer level;

//    @JsonProperty("sort_order")
//    private Integer sortOrder;

//    @JsonProperty("parent_chapter_key")
//    private String parentChapterKey;

//    private String description;

//    @JsonProperty("uploaded_by")
//    private String uploadedBy;

    private String status;

}
