package com.example.backend.service.admin.resource_manage;

import com.example.backend.entity.Plugin;

/**
 * 插件初始化服务接口
 * 负责扫描插件资源目录并将JSON数据导入到MySQL表中
 */
public interface PluginInitializationService {
    
    /**
     * 初始化插件
     * 1. 扫描插件资源目录
     * 2. 按顺序读取JSON文件并导入到对应表中
     * 3. 更新插件状态
     * 
     * @param plugin 插件信息
     * @return 初始化结果
     */
    boolean initializePlugin(Plugin plugin);
    
    /**
     * 扫描插件资源目录
     * 
     * @param pluginName 插件名称
     * @param resourcePath 资源目录路径
     * @return 扫描是否成功
     */
    boolean scanPluginResourceDirectory(String pluginName, String resourcePath);
    
    /**
     * 导入JSON数据到MySQL表
     * 
     * @param resourcePath 资源目录路径
     * @return 导入是否成功
     */
    boolean importJsonDataToDatabase(String resourcePath);
    
    /**
     * 清理临时文件
     * 
     * @param resourcePath 资源目录路径
     */
    void cleanupTempFiles(String resourcePath);
} 