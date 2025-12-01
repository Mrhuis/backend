package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<User> authenticate(LoginRequestDto loginRequestDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", loginRequestDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user != null && user.getPassword().equals(loginRequestDto.getPassword())) {
            // 在生产环境中，这里应该是加密密码的匹配
            // e.g., passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())
            updateUserActiveStats(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * 更新用户活跃状态相关字段：
     * - last_active_time：最后活跃时间
     * - total_active_days：累计活跃天数
     * - continuous_active_days：连续活跃天数
     */
    private void updateUserActiveStats(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate lastActiveDate = user.getLastActiveTime() != null ? user.getLastActiveTime().toLocalDate() : null;

        int totalActiveDays = user.getTotalActiveDays() != null ? user.getTotalActiveDays() : 0;
        int continuousActiveDays = user.getContinuousActiveDays() != null ? user.getContinuousActiveDays() : 0;

        if (lastActiveDate == null) {
            // 第一次活跃
            totalActiveDays = 1;
            continuousActiveDays = 1;
        } else if (lastActiveDate.isEqual(today)) {
            // 同一天重复登录，不重复计算
        } else if (lastActiveDate.plusDays(1).isEqual(today)) {
            // 连续活跃
            totalActiveDays += 1;
            continuousActiveDays += 1;
        } else if (lastActiveDate.isBefore(today)) {
            // 非连续活跃，累计 +1，连续重置
            totalActiveDays += 1;
            continuousActiveDays = 1;
        }

        // 防御性：活跃天数至少为1
        if (totalActiveDays <= 0) {
            totalActiveDays = 1;
        }
        if (continuousActiveDays <= 0) {
            continuousActiveDays = 1;
        }

        user.setTotalActiveDays(totalActiveDays);
        user.setContinuousActiveDays(continuousActiveDays);
        user.setLastActiveTime(now);
        userMapper.updateById(user);
    }
}
