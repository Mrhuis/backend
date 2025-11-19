package com.example.backend.controller.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherKSTagAddDto {
    @JsonAlias({"tag_applicable_type", "tagApplicableType"})
    private String tagApplicableType;
    
    @JsonAlias({"tag_content", "tagContent"})
    private String name;
    
    @JsonAlias({"tag_desc", "tagDesc"})
    private String description;
    
    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

    public String getTagApplicableType() {
        return tagApplicableType;
    }

    public void setTagApplicableType(String tagApplicableType) {
        this.tagApplicableType = tagApplicableType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}