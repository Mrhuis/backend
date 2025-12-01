package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("classes")
public class Class {
    /**
     * 数据库自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级业务唯一标识（如C2024_01）
     */
    private String classKey;

    /**
     * 班级名称（如"高一（3）班"）
     */
    private String name;

    /**
     * 班级加入邀请码（唯一）
     */
    private String inviteCode;

    /**
     * 创建者标识（创建班级的教师user_key）
     */
    private String creatorKey;

    /**
     * 班级创建时间
     */
    private LocalDateTime createdAt;
}