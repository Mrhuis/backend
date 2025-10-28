package com.example.backend.service.admin.resource_manage.impl;

import com.example.backend.service.admin.resource_manage.DataExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据导出服务实现类
 */
@Slf4j
@Service
public class DataExportServiceImpl implements DataExportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 插件相关的表名列表
    private static final String[] PLUGIN_RELATED_TABLES = {
        "plugins",
        "chapters", 
        "knowledge",
        "items",
        "media_assets",
        "chapter_resources",
        "knowledge_resources",
        "resource_form",
        "resource_tag",
        "user_preference",
        "user_knowledge",
        "user_resource_interact",
        "interact_label"
    };

    @Override
    public boolean exportPluginDataToJson(String pluginKey, String exportDir) throws IOException {
        log.info("开始导出插件 {} 的数据为JSON文件", pluginKey);
        
        // 创建导出目录
        Path exportPath = Paths.get(exportDir);
        if (!Files.exists(exportPath)) {
            Files.createDirectories(exportPath);
            log.info("创建导出目录: {}", exportPath);
        }
        
        boolean allSuccess = true;
        int successCount = 0;
        int failedCount = 0;
        
        // 导出所有相关表
        for (String tableName : PLUGIN_RELATED_TABLES) {
            try {
                if (exportTableToJson(tableName, exportDir)) {
                    successCount++;
                    log.info("表 {} 导出成功", tableName);
                } else {
                    failedCount++;
                    allSuccess = false;
                    log.warn("表 {} 导出失败", tableName);
                }
            } catch (Exception e) {
                failedCount++;
                allSuccess = false;
                log.error("导出表 {} 时发生异常", tableName, e);
            }
        }
        
        log.info("插件数据导出完成 - 成功: {}, 失败: {}", successCount, failedCount);
        return allSuccess;
    }

    @Override
    public boolean exportTableToJson(String tableName, String exportDir) throws IOException {
        try {
            log.info("开始导出表: {}", tableName);
            
            // 查询表数据
            String sql = "SELECT * FROM `" + tableName + "`";
            List<Map<String, Object>> tableData = jdbcTemplate.queryForList(sql);
            
            log.info("表 {} 共有 {} 条数据", tableName, tableData.size());
            
            // 处理数据，转换特殊类型
            List<Map<String, Object>> processedData = processTableData(tableData);
            
            // 创建JSON数据结构
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("table_name", tableName);
            jsonData.put("export_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            jsonData.put("data_count", processedData.size());
            jsonData.put("data", processedData);
            
            // 写入JSON文件
            Path jsonFile = Paths.get(exportDir, tableName + ".json");
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(jsonFile.toFile(), jsonData);
            
            log.info("表 {} 导出成功: {}", tableName, jsonFile);
            return true;
            
        } catch (Exception e) {
            log.error("导出表 {} 失败", tableName, e);
            return false;
        }
    }
    
    /**
     * 处理表数据，转换特殊类型为可序列化的格式
     */
    private List<Map<String, Object>> processTableData(List<Map<String, Object>> rawData) {
        return rawData.stream().map(row -> {
            Map<String, Object> processedRow = new HashMap<>();
            
            row.forEach((key, value) -> {
                if (value instanceof Timestamp) {
                    // 转换时间戳为字符串
                    processedRow.put(key, ((Timestamp) value).toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else if (value instanceof java.sql.Date) {
                    // 转换SQL日期为字符串
                    processedRow.put(key, ((java.sql.Date) value).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } else if (value instanceof java.sql.Time) {
                    // 转换SQL时间为字符串
                    processedRow.put(key, ((java.sql.Time) value).toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } else if (value instanceof byte[]) {
                    // 转换字节数组为Base64字符串
                    processedRow.put(key, java.util.Base64.getEncoder().encodeToString((byte[]) value));
                } else {
                    // 其他类型直接复制
                    processedRow.put(key, value);
                }
            });
            
            return processedRow;
        }).toList();
    }
} 