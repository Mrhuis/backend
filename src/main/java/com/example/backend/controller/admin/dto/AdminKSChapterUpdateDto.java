package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSChapterUpdateDto
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
public class AdminKSChapterUpdateDto {

    private Long id;

//    @JsonAlias({"chapter_key", "chapterKey"})
//    private String chapterKey;

    private String name;

    private Integer level;

    @JsonAlias({"sort_order", "sortOrder"})
    private Integer sortOrder;

    @JsonAlias({"parent_chapter_key", "parentChapterKey"})
    private String parentChapterKey;

    private String description;

    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

    private String status;
}
