package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息表实体类
 * 对应数据库表：message
 */
@Data
@TableName("message")
public class Message {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    @TableField("conv_id")
    private Long convId;

    /**
     * 发送者user_key
     */
    @TableField("sender_key")
    private String senderKey;

    /**
     * 接收者user_key（单聊为用户ID，群聊为群ID）
     */
    @TableField("receiver_key")
    private String receiverKey;

    /**
     * 文本内容（富文本可存JSON）
     */
    @TableField("content")
    private String content;

    /**
     * 附件（图片/文件）URL，存在对象存储
     */
    @TableField("attach_url")
    private String attachUrl;

    /**
     * 消息类型（1-文本，2-图片，3-文件等）
     */
    @TableField("msg_type")
    private Integer msgType;

    /**
     * 发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 是否已读（0-未读，1-已读）
     */
    @TableField("is_read")
    private Integer isRead = 0;

    /**
     * 是否撤回（0-正常，1-撤回）
     */
    @TableField("is_revoked")
    private Integer isRevoked = 0;

    /**
     * 发送者是否删除（0-未删除，1-已删除）
     */
    @TableField("sender_delete")
    private Integer senderDelete = 0;

    /**
     * 接收者是否删除（0-未删除，1-已删除）
     */
    @TableField("receiver_delete")
    private Integer receiverDelete = 0;
}