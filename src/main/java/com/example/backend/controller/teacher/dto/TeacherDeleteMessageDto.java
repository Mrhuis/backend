package com.example.backend.controller.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 删除消息DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherDeleteMessageDto {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户key（验证是发送者还是接收者）
     */
    private String userKey;
}