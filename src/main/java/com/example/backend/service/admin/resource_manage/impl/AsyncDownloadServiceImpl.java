package com.example.backend.service.admin.resource_manage.impl;

import com.example.backend.controller.admin.dto.DownloadTaskStatus;
import com.example.backend.service.admin.resource_manage.AsyncDownloadService;
import com.example.backend.service.admin.resource_manage.DataExportService;
import com.example.backend.service.admin.resource_manage.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步下载服务实现类
 */
@Service
public class AsyncDownloadServiceImpl implements AsyncDownloadService {
    
    private static final Logger log = LoggerFactory.getLogger(AsyncDownloadServiceImpl.class);
    
    @Autowired
    private DataExportService dataExportService;
    
    @Autowired
    private ZipService zipService;
    
    @Value("${upload.resource.path}")
    private String resourcePath;
    
    // 存储下载任务状态的内存缓存（生产环境建议使用Redis）
    private final Map<String, DownloadTaskStatus> taskStatusMap = new ConcurrentHashMap<>();
    
    @Override
    @Async("taskExecutor")
    public CompletableFuture<String> prepareDownloadAsync(String pluginKey, String taskId) {
        DownloadTaskStatus taskStatus = taskStatusMap.get(taskId);
        if (taskStatus == null) {
            taskStatus = new DownloadTaskStatus(taskId, pluginKey);
            taskStatusMap.put(taskId, taskStatus);
        }
        
        try {
            log.info("开始异步准备下载文件: pluginKey={}, taskId={}", pluginKey, taskId);
            
            // 更新任务状态
            taskStatus.setStatus("processing");
            taskStatus.setMessage("正在导出插件数据...");
            taskStatus.setProgress(10);
            
            // 1. 构建插件目录路径
            Path pluginDir = Paths.get(resourcePath, pluginKey);
            Path jsonDir = pluginDir.resolve(pluginKey);
            Path zipPath = pluginDir.resolve(pluginKey + ".zip");
            
            // 2. 创建JSON文件目录
            if (!Files.exists(jsonDir)) {
                Files.createDirectories(jsonDir);
                log.info("创建JSON文件目录: {}", jsonDir);
            }
            
            // 3. 导出MySQL表数据为JSON文件
            taskStatus.setMessage("正在导出数据库数据...");
            taskStatus.setProgress(30);
            boolean exportSuccess = dataExportService.exportPluginDataToJson(pluginKey, jsonDir.toString());
            if (!exportSuccess) {
                throw new RuntimeException("导出插件数据失败");
            }
            
            // 4. 压缩JSON文件目录为ZIP
            taskStatus.setMessage("正在压缩文件...");
            taskStatus.setProgress(60);
            boolean compressSuccess = zipService.compressDirectory(jsonDir.toString(), zipPath.toString());
            if (!compressSuccess) {
                throw new RuntimeException("压缩插件目录失败");
            }
            
            // 5. 验证ZIP文件
            Resource resource = new UrlResource(zipPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("ZIP文件不存在或不可读");
            }
            
            // 6. 任务完成
            String downloadUrl = "/api/admin/plugins/download-file?pluginKey=" + pluginKey + "&taskId=" + taskId;
            taskStatus.setStatus("completed");
            taskStatus.setMessage("文件准备完成，可以开始下载");
            taskStatus.setProgress(100);
            taskStatus.setDownloadUrl(downloadUrl);
            
            log.info("下载任务完成，开始下载文件... pluginKey={}, taskId={}, fileSize={} bytes", 
                    pluginKey, taskId, Files.size(zipPath));
            
            // 等待3分钟后再返回结果
            try {
                log.info("等待3分钟后返回下载链接...");
                Thread.sleep(3 * 60 * 1000); // 3分钟 = 3 * 60 * 1000毫秒
                log.info("等待完成，返回下载链接: {}", downloadUrl);
            } catch (InterruptedException e) {
                log.warn("等待被中断: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
            
            return CompletableFuture.completedFuture(downloadUrl);
            
        } catch (Exception e) {
            log.error("异步下载任务失败: pluginKey={}, taskId={}, error={}", 
                    pluginKey, taskId, e.getMessage(), e);
            
            // 更新任务状态为失败
            taskStatus.setStatus("failed");
            taskStatus.setMessage("文件准备失败");
            taskStatus.setErrorMessage(e.getMessage());
            
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Override
    public DownloadTaskStatus getTaskStatus(String taskId) {
        DownloadTaskStatus taskStatus = taskStatusMap.get(taskId);
        if (taskStatus == null) {
            // 返回一个默认的失败状态
            taskStatus = new DownloadTaskStatus(taskId, "unknown");
            taskStatus.setStatus("failed");
            taskStatus.setMessage("任务不存在");
            taskStatus.setErrorMessage("找不到指定的下载任务");
        }
        return taskStatus;
    }
    
    @Override
    public void cleanupExpiredTasks() {
        LocalDateTime now = LocalDateTime.now();
        taskStatusMap.entrySet().removeIf(entry -> {
            DownloadTaskStatus status = entry.getValue();
            // 清理超过1小时的已完成或失败任务
            if (("completed".equals(status.getStatus()) || "failed".equals(status.getStatus())) 
                    && status.getUpdateTime().plusHours(1).isBefore(now)) {
                log.info("清理过期下载任务: taskId={}, status={}", entry.getKey(), status.getStatus());
                return true;
            }
            return false;
        });
    }
} 