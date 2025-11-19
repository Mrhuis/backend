package com.example.backend.controller.teacher.dto;

import com.example.backend.common.dto.BaseEntity;

public class TeacherLAMediaQueryListDto extends BaseEntity {
    private String mediaKey;
    private String fileName;

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
}