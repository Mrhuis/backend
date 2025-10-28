package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("knowledges")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Knowledge {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String knowledgeKey;
    
    private String pluginKey;
    
    private String name;
    
    // JSON字段，存储前置知识点数组
    private String prerequisite;
    
    private Integer difficulty; // 1-5
    
    private String description;

    private String uploadedBy;

    private String status;

    private LocalDateTime createdAt;
} 