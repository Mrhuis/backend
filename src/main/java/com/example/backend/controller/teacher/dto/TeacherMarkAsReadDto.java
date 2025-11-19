package com.example.backend.controller.teacher.dto;

import lombok.Data;

/**
 * ClassName: TeacherMarkAsReadDto
 * Package: com.example.backend.controller.teacher.dto
 * Description: 教师端标记消息已读DTO
 *
 * @Author lingma
 * @Create 2025/11/19
 * @Version 1.0
 */
@Data
public class TeacherMarkAsReadDto {
    /**
     * 会话ID，前端以字符串形式传输以避免精度丢失
     */
    private String convId;
    
    /**
     * 用户key
     */
    private String userKey;
}