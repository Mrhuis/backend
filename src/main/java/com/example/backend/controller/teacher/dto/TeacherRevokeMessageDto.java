package com.example.backend.controller.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 撤回消息DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherRevokeMessageDto {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户key（验证是否是发送者）
     */
    private String userKey;
}