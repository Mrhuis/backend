package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;

public class TeacherLAMediaQueryListDto extends BaseEntity {
    private String mediaKey;
    private String fileName;
    private String userKey; // 当前用户标识，用于过滤：只显示状态为ENABLED或创建者为当前用户的数据

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}