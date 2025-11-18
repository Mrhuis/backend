package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminUserAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserAddDto {

    /**
     * 用户业务唯一标识（推荐系统特征关联主键，如U100）
     */
    @JsonAlias({"user_key", "userKey"})
    private String userKey;

    /**
     * 登录用户名（唯一）
     */
    private String username;

    /**
     * 加密密码（如bcrypt哈希存储）
     */
    private String password;

    /**
     * 用户角色：仅 student 需推荐学习资源
     */
    private String role;

    /**
     * 用户显示昵称
     */
    private String nickname;

    /**
     * 账户状态：禁用用户不参与推荐
     */
    private String status;

    /**
     * 用户活跃天数（直接作为特征）
     */
    @JsonAlias({"total_active_days", "totalActiveDays"})
    private Integer totalActiveDays;

    /**
     * 连续活跃天数（辅助特征）
     */
    @JsonAlias({"continuous_active_days", "continuousActiveDays"})
    private Integer continuousActiveDays;
}