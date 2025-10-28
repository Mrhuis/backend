package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chapter_resources")
public class ChapterResources {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String chapterKey;
    
    private String resourceType;
    
    private String resourceKey;
} 