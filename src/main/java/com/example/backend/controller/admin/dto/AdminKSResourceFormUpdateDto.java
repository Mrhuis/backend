package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminKSResourceFormUpdateDto
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
public class AdminKSResourceFormUpdateDto {
    private Long id;

//    @JsonAlias({"form_key", "formKey"})
//    private String formKey;

    @JsonAlias({"form_name", "formName"})
    private String formName;

    @JsonAlias({"form_type", "formType"})
    private String formType;

    private String description;

    private String status;
}
