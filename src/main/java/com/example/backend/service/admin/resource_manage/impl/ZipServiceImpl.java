package com.example.backend.service.admin.resource_manage.impl;

import com.example.backend.service.admin.resource_manage.ZipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP压缩服务实现类
 */
@Slf4j
@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public boolean compressDirectory(Path sourceDir, Path targetZip) throws IOException {
        return compressDirectory(sourceDir.toString(), targetZip.toString());
    }

    @Override
    public boolean compressDirectory(String sourceDirPath, String targetZipPath) throws IOException {
        Path sourceDir = Paths.get(sourceDirPath);
        Path targetZip = Paths.get(targetZipPath);
        
        if (!Files.exists(sourceDir)) {
            log.error("源目录不存在: {}", sourceDirPath);
            return false;
        }
        
        // 确保目标ZIP文件的父目录存在
        Path parentDir = targetZip.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        log.info("开始压缩目录: {} -> {}", sourceDirPath, targetZipPath);
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetZip))) {
            // 设置压缩级别
            zos.setLevel(6); // 平衡压缩率和速度
            
            // 遍历源目录
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 计算相对路径
                    Path relativePath = sourceDir.relativize(file);
                    String entryName = relativePath.toString().replace('\\', '/');
                    
                    // 创建ZIP条目
                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zos.putNextEntry(zipEntry);
                    
                    // 复制文件内容
                    Files.copy(file, zos);
                    zos.closeEntry();
                    
                    log.debug("添加文件到ZIP: {}", entryName);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // 创建目录条目（ZIP中需要显式创建目录）
                    if (!dir.equals(sourceDir)) {
                        Path relativePath = sourceDir.relativize(dir);
                        String entryName = relativePath.toString().replace('\\', '/') + "/";
                        
                        ZipEntry zipEntry = new ZipEntry(entryName);
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                        
                        log.debug("添加目录到ZIP: {}", entryName);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    log.warn("访问文件失败: {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
            
            log.info("目录压缩完成: {} -> {}", sourceDirPath, targetZipPath);
            return true;
            
        } catch (Exception e) {
            log.error("压缩目录失败: {} -> {}", sourceDirPath, targetZipPath, e);
            // 删除可能创建的不完整ZIP文件
            try {
                Files.deleteIfExists(targetZip);
            } catch (IOException deleteEx) {
                log.warn("删除不完整的ZIP文件失败: {}", targetZip, deleteEx);
            }
            throw e;
        }
    }
} 