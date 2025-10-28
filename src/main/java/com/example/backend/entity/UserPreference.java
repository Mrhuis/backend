package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_preference")
public class UserPreference {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userKey;
    
    private String preferType;
    
    private Integer formId;
    
    private LocalDateTime updateTime;
} 