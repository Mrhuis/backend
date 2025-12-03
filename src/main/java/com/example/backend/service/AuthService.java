package com.example.backend.service;

import com.example.backend.dto.LoginRequestDto;
import com.example.backend.entity.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> authenticate(LoginRequestDto loginRequestDto);

    /**
     * 根据 userKey 查询用户
     */
    Optional<User> findByUserKey(String userKey);
}
