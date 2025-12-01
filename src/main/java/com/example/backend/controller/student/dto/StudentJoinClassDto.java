package com.example.backend.controller.student.dto;

import com.example.backend.common.dto.BaseEntity;
import lombok.Data;

/**
 * 学生加入班级DTO
 */
@Data
public class StudentJoinClassDto extends BaseEntity {
    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 学生userKey（从token或session获取，这里作为备用）
     */
    private String userKey;
}

