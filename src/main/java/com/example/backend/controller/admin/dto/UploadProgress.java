package com.example.backend.controller.admin.dto;

public class UploadProgress {
    private String fileId;           // 文件唯一标识
    private String fileName;         // 文件名
    private int currentChunk;        // 当前已上传的块数
    private int totalChunks;         // 总块数
    private long uploadedBytes;      // 已上传的字节数
    private long totalBytes;         // 文件总字节数
    private double progress;         // 进度百分比 (0.0 - 100.0)
    private String status;           // 状态: UPLOADING, COMPLETED, ERROR, PAUSED
    private String message;          // 状态消息
    private long timestamp;          // 时间戳
    private String uploaderId;       // 上传者ID
    private long uploadSpeed;        // 上传速度 (bytes/s)
    private long estimatedTimeLeft;  // 预估剩余时间 (秒)
    private java.util.Set<Integer> uploadedChunks; // 已上传的分块编号集合

    // 状态常量
    public static final String STATUS_UPLOADING = "UPLOADING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_PENDING = "PENDING";

    // 构造函数
    public UploadProgress() {
        this.timestamp = System.currentTimeMillis();
    }

    public UploadProgress(String fileId, String fileName, String uploaderId) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.uploaderId = uploaderId;
        this.status = STATUS_PENDING;
        this.timestamp = System.currentTimeMillis();
    }

    // 计算进度百分比
    public void calculateProgress() {
        if (totalChunks > 0) {
            this.progress = (double) currentChunk / totalChunks * 100.0;
        }
        if (totalBytes > 0) {
            this.progress = (double) uploadedBytes / totalBytes * 100.0;
        }
    }

    // 更新进度
    public void updateProgress(int currentChunk, long uploadedBytes) {
        this.currentChunk = currentChunk;
        this.uploadedBytes = uploadedBytes;
        calculateProgress();
        this.timestamp = System.currentTimeMillis();
    }

    // 设置完成状态
    public void setCompleted() {
        this.status = STATUS_COMPLETED;
        this.currentChunk = this.totalChunks;
        this.uploadedBytes = this.totalBytes;
        this.progress = 100.0;
        this.timestamp = System.currentTimeMillis();
    }

    // 设置错误状态
    public void setError(String message) {
        this.status = STATUS_ERROR;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // 设置暂停状态
    public void setPaused() {
        this.status = STATUS_PAUSED;
        this.timestamp = System.currentTimeMillis();
    }

    // 设置上传状态
    public void setUploading() {
        this.status = STATUS_UPLOADING;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter和Setter方法
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getCurrentChunk() { return currentChunk; }
    public void setCurrentChunk(int currentChunk) { this.currentChunk = currentChunk; }

    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }

    public long getUploadedBytes() { return uploadedBytes; }
    public void setUploadedBytes(long uploadedBytes) { this.uploadedBytes = uploadedBytes; }

    public long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }

    public long getUploadSpeed() { return uploadSpeed; }
    public void setUploadSpeed(long uploadSpeed) { this.uploadSpeed = uploadSpeed; }

    public long getEstimatedTimeLeft() { return estimatedTimeLeft; }
    public void setEstimatedTimeLeft(long estimatedTimeLeft) { this.estimatedTimeLeft = estimatedTimeLeft; }
    
    public java.util.Set<Integer> getUploadedChunks() { return uploadedChunks; }
    public void setUploadedChunks(java.util.Set<Integer> uploadedChunks) { this.uploadedChunks = uploadedChunks; }

    @Override
    public String toString() {
        return "UploadProgress{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", currentChunk=" + currentChunk +
                ", totalChunks=" + totalChunks +
                ", uploadedBytes=" + uploadedBytes +
                ", totalBytes=" + totalBytes +
                ", progress=" + String.format("%.2f", progress) + "%" +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", uploadSpeed=" + uploadSpeed + " bytes/s" +
                ", estimatedTimeLeft=" + estimatedTimeLeft + "s" +
                '}';
    }
} 