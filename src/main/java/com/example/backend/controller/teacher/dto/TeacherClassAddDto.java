package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 添加班级DTO
 */
@Data
public class TeacherClassAddDto extends BaseEntity {
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
}