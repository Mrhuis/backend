package com.example.backend.controller.student.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐学习资源DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLCRecommendLearnResourceDto {

    /**
     * 资源类型,实际上其中内容是id
     */
    @JsonAlias({"form_id", "formId"})
    private Integer formId;
    
    /**
     * 资源标识
     */
    @JsonAlias({"resource_key", "resourceKey"})
    private String resourceKey;
}