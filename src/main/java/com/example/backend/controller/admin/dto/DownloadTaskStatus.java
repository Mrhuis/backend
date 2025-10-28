package com.example.backend.controller.admin.dto;

import java.time.LocalDateTime;

/**
 * 下载任务状态DTO
 */
public class DownloadTaskStatus {
    
    private String taskId;
    private String pluginKey;
    private String status; // processing, completed, failed
    private String message;
    private String downloadUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String errorMessage;
    private int progress; // 进度百分比 (0-100)
    
    public DownloadTaskStatus() {}
    
    public DownloadTaskStatus(String taskId, String pluginKey) {
        this.taskId = taskId;
        this.pluginKey = pluginKey;
        this.status = "processing";
        this.message = "正在准备下载文件...";
        this.progress = 0;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getPluginKey() {
        return pluginKey;
    }
    
    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updateTime = LocalDateTime.now();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public void setProgress(int progress) {
        this.progress = Math.min(100, Math.max(0, progress)); // 确保进度在0-100之间
    }
} 