package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<User> authenticate(LoginRequestDto loginRequestDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginRequestDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user != null && user.getPassword().equals(loginRequestDto.getPassword())) {
            // 在生产环境中，这里应该是加密密码的匹配
            // e.g., passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
