package com.example.backend.service.admin.resource_manage;

import com.example.backend.controller.admin.dto.DownloadTaskStatus;
import java.util.concurrent.CompletableFuture;

/**
 * 异步下载服务接口
 * 用于处理大文件下载的异步压缩和传输
 */
public interface AsyncDownloadService {
    
    /**
     * 异步准备下载文件
     * @param pluginKey 插件标识
     * @param taskId 任务ID
     * @return 异步任务结果
     */
    CompletableFuture<String> prepareDownloadAsync(String pluginKey, String taskId);
    
    /**
     * 获取下载任务状态
     * @param taskId 任务ID
     * @return 任务状态信息
     */
    DownloadTaskStatus getTaskStatus(String taskId);
    
    /**
     * 清理过期的下载任务
     */
    void cleanupExpiredTasks();
} 