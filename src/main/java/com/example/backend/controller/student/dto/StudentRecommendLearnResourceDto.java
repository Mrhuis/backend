package com.example.backend.controller.student.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: StudentRecommendLearnResourceDto
 * Package: com.example.backend.controller.student.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 14:52
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRecommendLearnResourceDto {

    /**
     * 用户标识
     */
    @JsonAlias({"user_key", "userKey"})  // 原有注解，保留
    private String userKey;
}
