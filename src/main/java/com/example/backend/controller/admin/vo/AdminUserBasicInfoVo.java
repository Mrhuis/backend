package com.example.backend.controller.admin.vo;

import lombok.Data;

/**
 * 用户基本信息VO
 * 用于通过user_key查询用户名和昵称
 */
@Data
public class AdminUserBasicInfoVo {
    /**
     * 用户业务唯一标识
     */
    private String userKey;
    
    /**
     * 登录用户名
     */
    private String username;
    
    /**
     * 用户显示昵称
     */
    private String nickname;
}