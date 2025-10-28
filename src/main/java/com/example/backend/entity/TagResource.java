package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tag_resource")
public class TagResource {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String resourceType;
    
    private String resourceKey;
    
    private Long tagId;  // 修改为Long类型，对应数据库中的BIGINT
    
} 