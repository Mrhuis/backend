package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSTagUpdateDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 10:59
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminKSTagUpdateDto {

    //    @TableId(type = IdType.AUTO)
    private Long id;
//
//    private String pluginKey;

    @JsonAlias({"tag_applicable_type", "tagApplicableType"})
    private String tagApplicableType;

    @JsonAlias({"tag_content", "tagContent"})
    private String tagContent;

    @JsonAlias({"tag_desc", "tagDesc"})
    private String tagDesc;

    private String status;
}
