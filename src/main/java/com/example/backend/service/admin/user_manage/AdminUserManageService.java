package com.example.backend.service.admin.user_manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.admin.dto.AdminUserAddDto;
import com.example.backend.controller.admin.dto.AdminUserQueryListDto;
import com.example.backend.controller.admin.dto.AdminUserUpdateDto;
import com.example.backend.controller.admin.vo.AdminSimpleUserVo;
import com.example.backend.controller.admin.vo.AdminUserBasicInfoVo;
import com.example.backend.controller.admin.vo.AdminUserQueryDetailVo;
import com.example.backend.entity.User;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: AdminUserManageService
 * Package: com.example.backend.service.admin.user_manage.AdminUserManageService
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
public interface AdminUserManageService {
    
    /**
     * 查询用户列表
     * @param req 查询条件
     * @return 用户列表
     */
    List<User> getUserList(AdminUserQueryListDto req);
    
    /**
     * 查询用户数量
     * @param req 查询条件
     * @return 用户总数
     */
    Long getUserCount(AdminUserQueryListDto req);
    
    /**
     * 添加用户
     * @param req 用户信息
     * @return 是否成功
     */
    boolean addUser(AdminUserAddDto req);
    
    /**
     * 更新用户
     * @param req 用户信息
     * @return 是否成功
     */
    boolean updateUser(AdminUserUpdateDto req);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUserById(Long id);
    
    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    AdminUserQueryDetailVo getUserDetailById(Long id);
    
    /**
     * 获取简化版用户列表(status为enabled的用户)
     * @return 简化版用户列表
     */
    List<AdminSimpleUserVo> getSimpleUserList();
    
    /**
     * 通过user_key获取用户基本信息（用户名和昵称）
     * @param userKey 用户业务唯一标识
     * @return 用户基本信息
     */
    AdminUserBasicInfoVo getUserBasicInfoByUserKey(String userKey);
    

}