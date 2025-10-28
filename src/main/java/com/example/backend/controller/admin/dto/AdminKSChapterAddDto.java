package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSChapterAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/2 11:18
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminKSChapterAddDto {
    @JsonAlias({"chapter_key", "chapterKey"})
//    @JsonProperty("chapter_key")
    private String chapterKey;

    private String name;

    private Integer level;

    @JsonAlias({"sort_order", "sortOrder"})
//    @JsonProperty("sort_order")
    private Integer sortOrder;

    @JsonAlias({"parent_chapter_key", "parentChapterKey"})
//    @JsonProperty("parent_chapter_key")
    private String parentChapterKey;

    private String description;

    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

//    private String status;
}
