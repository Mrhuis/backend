package com.example.backend.controller.teacher.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 添加消息DTO
 */
@Data
public class TeacherAddMessageDto {
    /**
     * 所属会话ID
     */
    private Long convId;

    /**
     * 发送者user_id
     */
    private Long senderId;

    /**
     * 接收者user_id（单聊为用户ID，群聊为群ID）
     */
    private Long receiverId;

    /**
     * 文本内容（富文本可存JSON）
     */
    private String content;

    /**
     * 附件（图片/文件）URL，存在对象存储
     */
    private String attachUrl;

    /**
     * 消息类型（1-文本，2-图片，3-文件等）
     */
    private Integer msgType;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
}