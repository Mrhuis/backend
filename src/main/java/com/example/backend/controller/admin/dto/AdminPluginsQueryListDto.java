package com.example.backend.controller.admin.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: AdminPluginsQueryListDto
 * Package: com.example.backend.controller.admin.dto
 * Description: 管理员插件查询列表DTO
 *
 * @Author 陈昊锡
 * @Create 2025/8/29 14:25
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminPluginsQueryListDto extends BaseEntity {
    @JsonProperty("plugin_key")
    private String pluginKey;
    
    private String name;
    private String version;
    private String author;
    private String description;
    
    @JsonProperty("storage_path")
    private String storagePath;
    
    private String status;
    
    @JsonProperty("uploaded_by")
    private String uploadedBy;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
