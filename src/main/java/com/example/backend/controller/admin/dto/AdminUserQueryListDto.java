package com.example.backend.controller.admin.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminUserQueryListDto
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
public class AdminUserQueryListDto extends BaseEntity {

    /**
     * 用户业务唯一标识（推荐系统特征关联主键，如U100）
     */
    @JsonAlias({"user_key", "userKey"})
    private String userKey;

    /**
     * 登录账号（唯一）
     */
    private String username;

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
}