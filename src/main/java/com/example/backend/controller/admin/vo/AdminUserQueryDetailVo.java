package com.example.backend.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: AdminUserQueryDetailVo
 * Package: com.example.backend.controller.admin.vo
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserQueryDetailVo {

    /**
     * 数据库自增ID（内部唯一标识）
     */
    private Long id;

    /**
     * 用户业务唯一标识（推荐系统特征关联主键，如U100）
     */
    private String userKey;

    /**
     * 登录账号（唯一）
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
    private Integer totalActiveDays;

    /**
     * 连续活跃天数（辅助特征）
     */
    private Integer continuousActiveDays;

    /**
     * 最后活跃时间（用于计算时效性）
     */
    private LocalDateTime lastActiveTime;

    /**
     * 注册时间（用于计算"用户注册时长"特征）
     */
    private LocalDateTime createdAt;

    /**
     * 信息最后更新时间
     */
    private LocalDateTime updatedAt;
}