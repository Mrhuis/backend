package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * 删除消息DTO
 */
@Data
public class TeacherDeleteMessageDto {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户ID（验证是发送者还是接收者）
     */
    private Long userId;
}