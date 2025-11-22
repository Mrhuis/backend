package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.service.admin.system_settings.AdminSystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统设置控制器
 * 管理推荐系统相关配置
 */
@RestController
@RequestMapping("/api/admin/system/config")
public class AdminSystemSettingsController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminSystemSettingsController.class);
    
    @Autowired
    private AdminSystemSettingsService adminSystemSettingsService;
    
    /**
     * 获取配置
     * @return 配置项列表
     */
    @GetMapping
    public Result<Map<String, Object>> getConfig() {
        try {
            log.info("收到获取配置请求");
            
            Map<String, Object> config = adminSystemSettingsService.getConfig();
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return Result.error("获取配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新配置
     * @param updateConfig 待更新的配置项
     * @return 更新结果
     */
    @PutMapping
    public Result<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> updateConfig) {
        try {
            log.info("收到更新配置请求: {}", updateConfig);
            
            Map<String, Object> result = adminSystemSettingsService.updateConfig(updateConfig);
            
            @SuppressWarnings("unchecked")
            List<String> invalidConfigs = (List<String>) result.get("invalid_configs");
            boolean success = invalidConfigs == null || invalidConfigs.isEmpty();
            
            return success ? Result.success(result) : Result.error("部分配置项更新失败", result);
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return Result.error("更新配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出配置
     * @return 导出的配置项
     */
    @GetMapping("/export")
    public Result<Map<String, Object>> exportConfig() {
        try {
            log.info("收到导出配置请求");
            
            Map<String, Object> config = adminSystemSettingsService.exportConfig();
            return Result.success(config);
        } catch (Exception e) {
            log.error("导出配置失败", e);
            return Result.error("导出配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 导入配置
     * @param importConfig 待导入的配置项
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<Map<String, Object>> importConfig(@RequestBody Map<String, Object> importConfig) {
        try {
            log.info("收到导入配置请求: {}", importConfig);
            
            Map<String, Object> result = adminSystemSettingsService.importConfig(importConfig);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("导入配置参数校验失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("导入配置失败", e);
            return Result.error("导入配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取模型文件列表
     * @return 模型文件名列表
     */
    @GetMapping("/models")
    public Result<Map<String, Object>> listModels() {
        try {
            log.info("收到获取模型文件列表请求");
            
            List<String> modelNames = adminSystemSettingsService.listModels();
            
            Map<String, Object> result = new HashMap<>();
            result.put("data", modelNames);
            result.put("count", modelNames.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取模型文件列表失败", e);
            return Result.error("获取模型文件列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 立即执行推荐系统流水线任务
     * @return 执行结果
     */
    @PostMapping("/run-pipeline")
    public Result<Map<String, Object>> runPipeline() {
        try {
            log.info("收到执行推荐系统流水线任务请求");
            
            Map<String, Object> result = adminSystemSettingsService.runPipeline();
            
            Boolean success = (Boolean) result.get("success");
            if (success != null && success) {
                return Result.success(result);
            } else {
                return Result.error(result.get("message").toString(), result);
            }
        } catch (Exception e) {
            log.error("执行推荐系统流水线任务失败", e);
            return Result.error("执行推荐系统流水线任务失败: " + e.getMessage());
        }
    }
}