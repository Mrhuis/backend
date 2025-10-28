package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSResourceFormAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/3 8:50
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminKSResourceFormAddDto {
    @JsonAlias({"form_key", "formKey"})
    private String formKey;
    @JsonAlias({"form_name", "formName"})
    private String formName;
    @JsonAlias({"form_type", "formType"})
    private String formType;
    private String description;
    @JsonAlias({"uploaded_by", "uploadedBy"})  // 含大写U/B，补充注解
    private String uploadedBy;
}
