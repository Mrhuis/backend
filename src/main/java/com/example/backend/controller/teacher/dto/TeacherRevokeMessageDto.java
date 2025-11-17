package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 撤回消息DTO
 */
@Data
public class TeacherRevokeMessageDto {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户ID（验证是否是发送者）
     */
    private Long userId;
}