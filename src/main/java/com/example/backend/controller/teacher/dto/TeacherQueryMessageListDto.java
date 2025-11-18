package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 查询消息列表DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherQueryMessageListDto extends BaseEntity {
    /**
     * 用户key（用于查询发送者或接收者为该用户的消息）
     */
    private String userKey;
}