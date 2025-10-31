package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: UserResourcePreference7d
 * Package: com.example.backend.entity
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/29 16:04
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_resource_preference_7d")
public class UserResourcePreference7d {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userKey;

    private String formKey;

    private Integer clickCount;

    private LocalDateTime recordTime;

}
