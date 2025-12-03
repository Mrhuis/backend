package com.example.backend.controller;

import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.LoginResponseDto;
import com.example.backend.entity.User;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = authService.authenticate(loginRequestDto);

        if (!userOptional.isPresent()) {
            // 用户不存在或密码错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOptional.get();
        // 账户被禁用：返回 403，前端可据此区分
        if (user.getStatus() != null && !"enabled".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LoginResponseDto response = new LoginResponseDto(
                user.getId(),
                user.getUserKey(),
                user.getNickname(),
                user.getRole()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 根据 userKey 查询当前用户状态
     * 200 + "enabled"   -> 可用
     * 403               -> 被禁用
     * 404               -> 用户不存在
     */
    @GetMapping("/status/{userKey}")
    public ResponseEntity<String> getUserStatus(@PathVariable String userKey) {
        Optional<User> userOptional = authService.findByUserKey(userKey);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not_found");
        }
        User user = userOptional.get();
        if (user.getStatus() != null && !"enabled".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("disabled");
        }
        return ResponseEntity.ok("enabled");
    }
}
