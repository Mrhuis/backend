package com.example.backend.service.admin.system_settings;

import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口
 * 管理推荐系统相关配置
 */
public interface AdminSystemSettingsService {
    
    /**
     * 获取配置
     * @return 配置项列表
     */
    Map<String, Object> getConfig();
    
    /**
     * 更新配置
     * @param updateConfig 待更新的配置项
     * @return 更新结果，包含更新成功和失败的配置项列表
     */
    Map<String, Object> updateConfig(Map<String, Object> updateConfig);
    
    /**
     * 导出配置
     * @return 导出的配置项
     */
    Map<String, Object> exportConfig();
    
    /**
     * 导入配置
     * @param importConfig 待导入的配置项
     * @return 导入结果
     */
    Map<String, Object> importConfig(Map<String, Object> importConfig);
    
    /**
     * 获取模型文件列表
     * @return 模型文件名列表
     */
    List<String> listModels();
    
    /**
     * 立即执行推荐系统流水线任务
     * @return 执行结果
     */
    Map<String, Object> runPipeline();
}