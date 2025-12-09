package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;

public class TeacherLAItemQueryListDto extends BaseEntity {
    private String itemKey;
    private String content;
    private String userKey; // 当前用户标识，用于过滤：只显示状态为ENABLED或创建者为当前用户的数据

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}