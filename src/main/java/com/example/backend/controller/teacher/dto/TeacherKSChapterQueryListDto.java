package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;

public class TeacherKSChapterQueryListDto extends BaseEntity {
    private String key;
    private String name;
    private String userKey; // 当前用户标识，用于过滤：只显示状态为ENABLED或创建者为当前用户的数据

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}