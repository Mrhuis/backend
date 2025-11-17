package com.example.backend.service.admin.user_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.admin.dto.AdminUserAddDto;
import com.example.backend.controller.admin.dto.AdminUserQueryListDto;
import com.example.backend.controller.admin.dto.AdminUserUpdateDto;
import com.example.backend.controller.admin.vo.AdminUserQueryDetailVo;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.admin.user_manage.AdminUserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: AdminUserManageServiceImpl
 * Package: com.example.backend.service.admin.user_manage.impl
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Slf4j
@Service
public class AdminUserManageServiceImpl implements AdminUserManageService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<User> getUserList(AdminUserQueryListDto req) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 根据userKey查询
        if (StringUtils.hasText(req.getUserKey())) {
            queryWrapper.like("user_key", req.getUserKey());
        }
        
        // 根据username查询
        if (StringUtils.hasText(req.getUsername())) {
            queryWrapper.like("username", req.getUsername());
        }
        
        // 根据role查询
        if (StringUtils.hasText(req.getRole())) {
            queryWrapper.eq("role", req.getRole());
        }
        
        // 根据nickname查询
        if (StringUtils.hasText(req.getNickname())) {
            queryWrapper.like("nickname", req.getNickname());
        }
        
        // 根据status查询
        if (StringUtils.hasText(req.getStatus())) {
            queryWrapper.eq("status", req.getStatus());
        }
        
        // 分页处理
        if (req.getPageIndex() != null && req.getPageSize() != null) {
            Page<User> page = new Page<>(req.getPageIndex(), req.getPageSize());
            page = userMapper.selectPage(page, queryWrapper);
            return page.getRecords();
        }
        
        return userMapper.selectList(queryWrapper);
    }
    
    @Override
    public Long getUserCount(AdminUserQueryListDto req) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 根据userKey查询
        if (StringUtils.hasText(req.getUserKey())) {
            queryWrapper.like("user_key", req.getUserKey());
        }
        
        // 根据username查询
        if (StringUtils.hasText(req.getUsername())) {
            queryWrapper.like("username", req.getUsername());
        }
        
        // 根据role查询
        if (StringUtils.hasText(req.getRole())) {
            queryWrapper.eq("role", req.getRole());
        }
        
        // 根据nickname查询
        if (StringUtils.hasText(req.getNickname())) {
            queryWrapper.like("nickname", req.getNickname());
        }
        
        // 根据status查询
        if (StringUtils.hasText(req.getStatus())) {
            queryWrapper.eq("status", req.getStatus());
        }
        
        return userMapper.selectCount(queryWrapper);
    }
    
    @Override
    public boolean addUser(AdminUserAddDto req) {
        try {
            User user = new User();
            BeanUtils.copyProperties(req, user);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return userMapper.insert(user) > 0;
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return false;
        }
    }
    
    @Override
    public boolean updateUser(AdminUserUpdateDto req) {
        try {
            User user = new User();
            BeanUtils.copyProperties(req, user);
            user.setUpdatedAt(LocalDateTime.now());
            
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());
            return userMapper.update(user, updateWrapper) > 0;
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return false;
        }
    }
    
    @Override
    public boolean deleteUserById(Long id) {
        try {
            return userMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return false;
        }
    }
    
    @Override
    public AdminUserQueryDetailVo getUserDetailById(Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user != null) {
                AdminUserQueryDetailVo vo = new AdminUserQueryDetailVo();
                BeanUtils.copyProperties(user, vo);
                return vo;
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return null;
        }
    }
}