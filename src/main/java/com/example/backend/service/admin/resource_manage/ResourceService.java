package com.example.backend.service.admin.resource_manage;

import com.example.backend.entity.Plugin;
import com.example.backend.controller.admin.dto.FileChunk;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ResourceService {
    /**
     * 存储上传的资源文件
     * 
     * @param file 上传的文件
     * @param uploaderId 上传者ID
     * @throws IOException 文件处理异常
     */
    void storeResource(MultipartFile file, String uploaderId) throws IOException;
    
    /**
     * 删除指定插件的所有相关资源
     * 
     * @param pluginKey 插件唯一标识
     * @throws IOException 删除过程中可能发生的IO异常
     */
    // 新增：删除插件相关资源
    void deletePluginResources(String pluginKey) throws IOException;
    
    /**
     * 上传完整的资源ZIP文件并处理
     * 
     * @param file 上传的ZIP文件
     * @param uploaderId 上传者ID
     * @return 处理后的插件信息
     * @throws IOException 文件处理异常
     */
    // 分块上传相关方法
    Plugin uploadResourceZip(MultipartFile file, String uploaderId) throws IOException;
    
    /**
     * 上传文件分块
     * 
     * @param chunkInfo 分块信息
     * @param file 分块文件数据
     * @return 上传是否成功
     * @throws IOException 文件处理异常
     */
    boolean uploadChunk(FileChunk chunkInfo, MultipartFile file) throws IOException;
    
    /**
     * 合并已上传的文件分块为完整文件
     * 
     * @param fileId 文件唯一标识
     * @param fileName 原始文件名
     * @param uploaderId 上传者ID
     * @return 合并后的插件信息
     * @throws IOException 文件处理异常
     */
    Plugin mergeChunks(String fileId, String fileName, String uploaderId) throws IOException;
    

    
    /**
     * 初始化分块上传
     * 
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param totalChunks 总分块数
     * @return 文件唯一标识
     */
    String initChunkUpload(String fileName, long fileSize, int totalChunks);
    
    /**
     * 清理过期的分块文件
     * 
     * @return 清理的文件数量
     */
    int cleanupExpiredChunks();
}