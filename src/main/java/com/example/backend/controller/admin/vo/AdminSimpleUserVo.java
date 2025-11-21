package com.example.backend.controller.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简化版用户信息VO类
 * 用于返回用户的部分基本信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSimpleUserVo {
    /**
     * 数据库自增ID（内部唯一标识）
     */
    private Long id;
    
    /**
     * 登录账号（唯一）
     */
    private String username;
    
    /**
     * 用户业务唯一标识（推荐系统特征关联主键，如U100）
     */
    private String userKey;
    
    /**
     * 用户显示昵称
     */
    private String nickname;
    
    /**
     * 用户角色：仅 student 需推荐学习资源
     */
    private String role;
    
    /**
     * 账户状态：禁用用户不参与推荐
     */
    private String status;
}