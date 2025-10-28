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
@TableName("media_assets")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaAssets {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String mediaKey;
    
    private String pluginKey;
    
    private String formKey; // video, animation, image, script, document
    
    private String fileName;
    
    private String url;
    
    private String entryPoint;
    
    private String implementationScript;
    
    private Integer duration; // 资源时长（秒）

    private String uploadedBy;

    private String status;

    private LocalDateTime createdAt;

} 