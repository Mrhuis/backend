package com.example.backend.service.admin.resource_manage;

import java.io.IOException;

/**
 * 数据导出服务接口
 * 负责将MySQL表数据导出为JSON文件
 */
public interface DataExportService {
    
    /**
     * 导出插件相关的所有表数据为JSON文件
     * 
     * @param pluginKey 插件唯一标识
     * @param exportDir 导出目录路径
     * @return 是否导出成功
     * @throws IOException 导出过程中的IO异常
     */
    boolean exportPluginDataToJson(String pluginKey, String exportDir) throws IOException;
    
    /**
     * 导出指定表的数据为JSON文件
     * 
     * @param tableName 表名
     * @param exportDir 导出目录路径
     * @return 是否导出成功
     * @throws IOException 导出过程中的IO异常
     */
    boolean exportTableToJson(String tableName, String exportDir) throws IOException;
} 