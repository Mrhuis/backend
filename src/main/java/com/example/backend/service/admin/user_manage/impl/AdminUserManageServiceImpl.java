package com.example.backend.service.admin.user_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.admin.dto.AdminUserAddDto;
import com.example.backend.controller.admin.dto.AdminUserQueryListDto;
import com.example.backend.controller.admin.dto.AdminUserUpdateDto;
import com.example.backend.controller.admin.vo.AdminSimpleUserVo;
import com.example.backend.controller.admin.vo.AdminUserBasicInfoVo;
import com.example.backend.controller.admin.vo.AdminUserQueryDetailVo;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.admin.user_manage.AdminUserManageService;
import com.example.backend.tool.DirectoryTool;
import com.example.backend.tool.media.MultipartFileTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @Value("${upload.resource.path}")
    private String baseResourcePath;
    
    @Override
    public List<User> getUserList(AdminUserQueryListDto req) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 根据userKey查询
        if (StringUtils.hasText(req.getUserKey())) {
            queryWrapper.like("user_key", req.getUserKey());
        }
        
        // 根据account查询
        if (StringUtils.hasText(req.getUsername())) {
            queryWrapper.like("account", req.getUsername());
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
        
        // 根据account查询
        if (StringUtils.hasText(req.getUsername())) {
            queryWrapper.like("account", req.getUsername());
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
            // 手动映射username到account字段
            user.setAccount(req.getUsername());
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
            // 手动映射username到account字段
            user.setAccount(req.getUsername());
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
                // 手动映射account到username字段
                log.debug("用户ID: {}, account字段值: {}", id, user.getAccount());
                vo.setUsername(user.getAccount());
                log.debug("设置username后的值: {}", vo.getUsername());
                return vo;
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return null;
        }
    }
    
    @Override
    public List<AdminSimpleUserVo> getSimpleUserList() {
        // 创建查询条件，只查询status为enabled的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "enabled");
        
        // 查询用户列表
        List<User> users = userMapper.selectList(queryWrapper);
        
        // 转换为简化版VO对象
        return users.stream().map(user -> {
            AdminSimpleUserVo vo = new AdminSimpleUserVo();
            vo.setId(user.getId());
            vo.setUsername(user.getAccount());
            vo.setUserKey(user.getUserKey());
            vo.setNickname(user.getNickname());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public AdminUserBasicInfoVo getUserBasicInfoByUserKey(String userKey) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey);
        
        // 查询用户
        User user = userMapper.selectOne(queryWrapper);
        
        // 如果用户存在，转换为VO对象
        if (user != null) {
            AdminUserBasicInfoVo vo = new AdminUserBasicInfoVo();
            vo.setUserKey(user.getUserKey());
            vo.setUsername(user.getAccount());
            vo.setNickname(user.getNickname());
            return vo;
        }
        
        return null;
    }
    
    @Override
    public AdminUserQueryDetailVo getUserDetailByKey(String userKey) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey);
        
        // 查询用户
        User user = userMapper.selectOne(queryWrapper);
        
        // 如果用户存在，转换为VO对象
        if (user != null) {
            AdminUserQueryDetailVo vo = new AdminUserQueryDetailVo();
            BeanUtils.copyProperties(user, vo);
            // 手动映射account到username字段
            log.debug("用户userKey: {}, account字段值: {}", userKey, user.getAccount());
            vo.setUsername(user.getAccount());
            log.debug("设置username后的值: {}", vo.getUsername());
            return vo;
        }
        
        return null;
    }

}