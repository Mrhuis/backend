package com.example.backend.service.admin.resource_manage;

import java.io.IOException;
import java.nio.file.Path;

/**
 * ZIP压缩服务接口
 * 负责将目录压缩为ZIP文件
 */
public interface ZipService {
    
    /**
     * 压缩目录为ZIP文件
     * 
     * @param sourceDir 源目录路径
     * @param targetZip 目标ZIP文件路径
     * @return 是否压缩成功
     * @throws IOException 压缩过程中的IO异常
     */
    boolean compressDirectory(Path sourceDir, Path targetZip) throws IOException;
    
    /**
     * 压缩目录为ZIP文件（使用字符串路径）
     * 
     * @param sourceDirPath 源目录路径字符串
     * @param targetZipPath 目标ZIP文件路径字符串
     * @return 是否压缩成功
     * @throws IOException 压缩过程中的IO异常
     */
    boolean compressDirectory(String sourceDirPath, String targetZipPath) throws IOException;
} 