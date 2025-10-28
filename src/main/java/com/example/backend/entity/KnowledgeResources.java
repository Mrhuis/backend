package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_resources")
public class KnowledgeResources {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String knowledgeKey;
    
    private String resourceType;
    
    private String resourceKey;
} 