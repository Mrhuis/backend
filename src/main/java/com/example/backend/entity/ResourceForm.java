package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resource_form")
public class ResourceForm {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String formKey;
    
    private String pluginKey;
    
    private String formName;
    
    private String formType;
    
    private String description;

    private String uploadedBy;

    private String status;

    private LocalDateTime createdAt;
} 