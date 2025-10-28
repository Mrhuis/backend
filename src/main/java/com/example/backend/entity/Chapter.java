package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chapters")
public class Chapter {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String chapterKey;
    
    private String pluginKey;
    
    private String name;
    
    private Integer level;
    
    private Integer sortOrder;
    
    private String parentChapterKey;
    
    private String description;

    private String uploadedBy;

    private String status;
    
    private LocalDateTime createdAt;
} 