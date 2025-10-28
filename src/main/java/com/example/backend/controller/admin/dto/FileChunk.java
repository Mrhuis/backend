package com.example.backend.controller.admin.dto;

import java.util.Arrays;

public class FileChunk {
    private String fileId;           // 文件唯一标识
    private String fileName;         // 文件名
    private int chunkNumber;         // 当前块序号
    private int totalChunks;         // 总块数
    private long chunkSize;          // 当前块大小
    private long totalFileSize;      // 文件总大小
    private byte[] data;             // 文件块数据
    private String uploaderId;       // 上传者ID
    private String checksum;         // 校验和
    private long timestamp;          // 时间戳

    // 构造函数
    public FileChunk() {}

    public FileChunk(String fileId, String fileName, int chunkNumber, int totalChunks, 
                    long chunkSize, long totalFileSize, byte[] data, String uploaderId) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalChunks;
        this.chunkSize = chunkSize;
        this.totalFileSize = totalFileSize;
        this.data = data;
        this.uploaderId = uploaderId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter和Setter方法
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getChunkNumber() { return chunkNumber; }
    public void setChunkNumber(int chunkNumber) { this.chunkNumber = chunkNumber; }

    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }

    public long getChunkSize() { return chunkSize; }
    public void setChunkSize(long chunkSize) { this.chunkSize = chunkSize; }

    public long getTotalFileSize() { return totalFileSize; }
    public void setTotalFileSize(long totalFileSize) { this.totalFileSize = totalFileSize; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "FileChunk{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", chunkNumber=" + chunkNumber +
                ", totalChunks=" + totalChunks +
                ", chunkSize=" + chunkSize +
                ", totalFileSize=" + totalFileSize +
                ", dataLength=" + (data != null ? data.length : 0) +
                ", uploaderId='" + uploaderId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 