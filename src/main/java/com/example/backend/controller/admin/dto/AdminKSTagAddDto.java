package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSTagAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 10:58
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminKSTagAddDto {
    //    @TableId(type = IdType.AUTO)
//    private Long id;
//
//    private String pluginKey;

    @JsonAlias({"tag_applicable_type", "tagApplicableType"})
    private String tagApplicableType;

    @JsonAlias({"tag_content", "tagContent"})
    private String tagContent;

    @JsonAlias({"tag_desc", "tagDesc"})
    private String tagDesc;

    @JsonAlias({"uploaded_by", "uploadedBy"})  // 含大写U/B，补充注解
    private String uploadedBy;
    // 添加标签时不需要status字段，系统自动设置为ENABLED
    // private String status;
}
