package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("plugins")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plugin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pluginKey;
    private String name;
    private String version;
    private String author;
    private String description;
    private String storagePath;
    private String status;
    private String uploadedBy;
    private LocalDateTime createdAt;
}

