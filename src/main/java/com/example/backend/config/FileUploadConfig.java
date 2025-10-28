package com.example.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传配置类
 * 负责创建必要的目录结构
 */
@Slf4j
@Configuration
public class FileUploadConfig {

    @Value("${upload.resource.path}")
    private String uploadResourcePath;

    /**
     * 初始化时创建必要的目录
     */
    @PostConstruct
    public void init() {
        try {
            // 创建资源上传目录
            createDirectoryIfNotExists(uploadResourcePath);
            
            // 创建临时解压目录
            createDirectoryIfNotExists(uploadResourcePath + "temp/");
            
            log.info("文件上传目录初始化完成: {}", uploadResourcePath);
            
        } catch (Exception e) {
            log.error("文件上传目录初始化失败: {}", uploadResourcePath, e);
        }
    }

    /**
     * 创建目录（如果不存在）
     */
    private void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("创建目录: {}", dirPath);
        }
    }
} 