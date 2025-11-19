package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;

public class TeacherKSChapterQueryListDto extends BaseEntity {
    private String key;
    private String name;

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
}