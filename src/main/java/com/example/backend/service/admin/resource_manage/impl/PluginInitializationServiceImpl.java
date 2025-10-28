package com.example.backend.service.admin.resource_manage.impl;

import com.example.backend.service.admin.resource_manage.PluginInitializationService;
import com.example.backend.entity.Plugin;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 插件初始化服务实现类
 * 参考Python脚本的实现原理，实现Java版本的插件初始化功能
 */
@Slf4j
@Service
public class PluginInitializationServiceImpl implements PluginInitializationService {

    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${upload.resource.path}")
    private String uploadResourcePath;
    
    // 表导入顺序配置（根据外键依赖关系调整）
    private static final List<String> TABLE_IMPORT_ORDER = Arrays.asList(
        "resource_form",      // 资源形式字典表（无外键）
        "knowledge",          // 知识点表（无外键）
        "chapters",           // 章节表（可能依赖自身或无外键）
        "tag",
        "media_assets",       // 媒体资源表（依赖knowledge）
        "items",              // 习题表（依赖knowledge）
        "knowledge_resources", // 知识点-资源关联表（依赖knowledge、media/items）
        "resource_tag"        // 用户偏好表（依赖resource_form）
    );
    
    // 批量插入大小
    private static final int BATCH_SIZE = 100;
    
    @Override
    @Transactional
    public boolean initializePlugin(Plugin plugin) {
        log.info("开始初始化插件: {}", plugin.getName());
        
        try {
            // 1. 构建插件资源目录路径
            String resourcePath = buildResourcePath(plugin);
            
            // 2. 扫描插件资源目录
            if (!scanPluginResourceDirectory(plugin.getPluginKey(), resourcePath)) {
                log.error("插件资源目录扫描失败: {}", plugin.getPluginKey());
                return false;
            }
            
            // 3. 导入JSON数据到数据库
            if (!importJsonDataToDatabase(resourcePath)) {
                log.error("JSON数据导入失败: {}", plugin.getPluginKey());
                return false;
            }
            
            // 4. 更新插件状态为已初始化
            pluginService.updatePluginStatus(plugin.getId(), "disabled");
            
            // 5. 插件初始化完成，无需清理临时文件
            
            log.info("插件初始化成功: {}", plugin.getName());
            return true;
            
        } catch (Exception e) {
            log.error("插件初始化过程中发生错误: {}", plugin.getName(), e);
            return false;
        }
    }
    
    @Override
    public boolean scanPluginResourceDirectory(String pluginName, String resourcePath) {
        log.info("开始扫描插件资源目录: {} -> {}", pluginName, resourcePath);
        
        try {
            // 验证资源目录是否存在
            Path resourceDir = Paths.get(resourcePath);
            if (!Files.exists(resourceDir)) {
                log.error("插件资源目录不存在: {}", resourceDir);
                return false;
            }
            
            // 验证目录是否包含JSON文件
            if (!hasJsonFiles(resourceDir)) {
                log.error("插件资源目录中未找到JSON文件: {}", resourceDir);
                return false;
            }
            
            log.info("插件资源目录扫描成功: {}", resourceDir);
            return true;
            
        } catch (Exception e) {
            log.error("插件资源目录扫描失败: {}", pluginName, e);
            return false;
        }
    }
    
    /**
     * 检查目录中是否包含JSON文件
     */
    private boolean hasJsonFiles(Path directory) {
        try {
            return Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> path.toString().toLowerCase().endsWith(".json"));
        } catch (IOException e) {
            log.error("检查JSON文件失败: {}", directory, e);
            return false;
            }
    }
    
    @Override
    @Transactional
    public boolean importJsonDataToDatabase(String resourcePath) {
        log.info("开始导入JSON数据到数据库: {}", resourcePath);
        
        try {
            // 1. 扫描JSON文件
            Map<String, Path> jsonFiles = scanJsonFiles(resourcePath);
            if (jsonFiles.isEmpty()) {
                log.warn("未找到任何JSON文件: {}", resourcePath);
                return false;
            }
            
            log.info("发现{}个JSON文件: {}", jsonFiles.size(), jsonFiles.keySet());
            
            // 2. 按顺序导入表数据
            List<String> successTables = new ArrayList<>();
            List<String> failedTables = new ArrayList<>();
            
            for (String tableName : TABLE_IMPORT_ORDER) {
                if (jsonFiles.containsKey(tableName)) {
                    if (importTableData(tableName, jsonFiles.get(tableName))) {
                        successTables.add(tableName);
                    } else {
                        failedTables.add(tableName);
                        // 如果关键表导入失败，终止后续导入
                        if (isCriticalTable(tableName)) {
                            log.error("关键表{}导入失败，终止后续导入", tableName);
                            break;
                        }
                    }
                }
            }
            
            // 3. 处理未在导入顺序中的表
            List<String> unorderedTables = new ArrayList<>();
            for (String tableName : jsonFiles.keySet()) {
                if (!TABLE_IMPORT_ORDER.contains(tableName)) {
                    unorderedTables.add(tableName);
                }
            }
            
            if (!unorderedTables.isEmpty()) {
                log.warn("发现{}个未指定导入顺序的表: {}", unorderedTables.size(), unorderedTables);
                for (String tableName : unorderedTables) {
                    if (importTableData(tableName, jsonFiles.get(tableName))) {
                        successTables.add(tableName);
                    } else {
                        failedTables.add(tableName);
                    }
                }
            }
            
            // 4. 输出导入结果
            log.info("JSON数据导入完成 - 成功: {}, 失败: {}", successTables, failedTables);
            
            return failedTables.isEmpty();
            
        } catch (Exception e) {
            log.error("JSON数据导入过程中发生错误: {}", resourcePath, e);
            return false;
        }
    }
    
    @Override
    public void cleanupTempFiles(String resourcePath) {
        try {
            Path path = Paths.get(resourcePath);
            if (Files.exists(path)) {
                deleteDirectoryRecursively(path);
                log.info("临时文件清理完成: {}", resourcePath);
            }
        } catch (Exception e) {
            log.warn("清理临时文件时发生错误: {}", resourcePath, e);
        }
    }
    
    /**
     * 构建插件资源目录路径
     */
    private String buildResourcePath(Plugin plugin) {
        return uploadResourcePath + plugin.getPluginKey();
    }
    
    /**
     * 扫描JSON文件（递归扫描子目录）
     */
    private Map<String, Path> scanJsonFiles(String resourcePath) throws IOException {
        Map<String, Path> jsonFiles = new HashMap<>();
        Path path = Paths.get(resourcePath);
        
        if (!Files.exists(path)) {
            log.warn("路径不存在: {}", resourcePath);
            return jsonFiles;
        }
        
        log.info("开始扫描目录: {}", path);
        
        Files.walk(path)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().toLowerCase().endsWith(".json"))
            .forEach(p -> {
                String fileName = p.getFileName().toString();
                String tableName = fileName.substring(0, fileName.lastIndexOf('.'));
                log.info("发现JSON文件: {} -> 表名: {}", fileName, tableName);
                jsonFiles.put(tableName, p);
            });
        
        log.info("扫描完成，共发现{}个JSON文件", jsonFiles.size());
        return jsonFiles;
    }
    
    /**
     * 导入单个表的数据
     */
    private boolean importTableData(String tableName, Path jsonFile) {
        log.info("开始导入表{}的数据: {}", tableName, jsonFile.getFileName());
        
        try {
            // 1. 读取JSON文件
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile.toFile());
            
            if (!rootNode.has(tableName) || !rootNode.get(tableName).isArray()) {
                log.warn("表{}的JSON格式不正确", tableName);
                return false;
            }
            
            JsonNode dataArray = rootNode.get(tableName);
            if (dataArray.size() == 0) {
                log.info("表{}无数据可导入", tableName);
                return true;
            }
            
            // 2. 获取表字段信息
            List<String> tableColumns = getTableColumns(tableName);
            if (tableColumns.isEmpty()) {
                log.error("无法获取表{}的字段信息", tableName);
                return false;
            }
            
            // 3. 准备数据
            List<Map<String, Object>> records = prepareTableRecords(dataArray, tableColumns);
            
            // 4. 批量插入数据
            return batchInsertData(tableName, records, tableColumns);
            
        } catch (Exception e) {
            log.error("导入表{}数据失败: {}", tableName, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取表的字段列表
     */
    private List<String> getTableColumns(String tableName) {
        try {
            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                        "ORDER BY ORDINAL_POSITION";
            
            return jdbcTemplate.queryForList(sql, String.class, tableName);
        } catch (Exception e) {
            log.error("获取表{}字段信息失败: {}", tableName, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 准备表记录数据
     */
    private List<Map<String, Object>> prepareTableRecords(JsonNode dataArray, List<String> tableColumns) {
        List<Map<String, Object>> records = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        
        for (JsonNode record : dataArray) {
            Map<String, Object> filteredRecord = new HashMap<>();
            
            for (String column : tableColumns) {
                if (record.has(column)) {
                    JsonNode value = record.get(column);
                    filteredRecord.put(column, parseJsonValue(value));
                }
            }
            
            if (!filteredRecord.isEmpty()) {
                records.add(filteredRecord);
            }
        }
        
        return records;
    }
    
    /**
     * 解析JSON值类型
     */
    private Object parseJsonValue(JsonNode value) {
        if (value.isNull()) {
            return null;
        } else if (value.isTextual()) {
            String text = value.asText();
            // 尝试解析日期时间
            if (text.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                try {
                    return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    try {
                        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (Exception e2) {
                        return text;
                    }
                }
            }
            return text;
        } else if (value.isBoolean()) {
            return value.asBoolean() ? 1 : 0;
        } else if (value.isNumber()) {
            if (value.isInt()) {
                return value.asInt();
            } else if (value.isLong()) {
                return value.asLong();
            } else {
                return value.asDouble();
            }
        }
        
        return value.asText();
    }
    
    /**
     * 批量插入数据
     */
    private boolean batchInsertData(String tableName, List<Map<String, Object>> records, List<String> columns) {
        if (records.isEmpty()) {
            return true;
        }
        
        try {
            // 构建SQL语句
            String columnsStr = String.join(", ", columns);
            String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));
            
            String sql = String.format("INSERT INTO `%s` (%s) VALUES (%s) " +
                                     "ON DUPLICATE KEY UPDATE %s",
                                     tableName, columnsStr, placeholders,
                                     buildUpdateClause(columns));
            
            // 分批插入
            for (int i = 0; i < records.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, records.size());
                List<Map<String, Object>> batch = records.subList(i, endIndex);
                
                List<Object[]> batchParams = new ArrayList<>();
                for (Map<String, Object> record : batch) {
                    Object[] params = new Object[columns.size()];
                    for (int j = 0; j < columns.size(); j++) {
                        params[j] = record.get(columns.get(j));
                    }
                    batchParams.add(params);
                }
                
                jdbcTemplate.batchUpdate(sql, batchParams);
                log.debug("表{}批量插入完成: {}/{}", tableName, endIndex, records.size());
            }
            
            log.info("表{}数据导入成功，共{}条记录", tableName, records.size());
            return true;
            
        } catch (Exception e) {
            log.error("表{}批量插入失败: {}", tableName, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 构建更新子句
     */
    private String buildUpdateClause(List<String> columns) {
        return columns.stream()
            .filter(col -> !"id".equals(col)) // 排除自增ID
            .map(col -> String.format("`%s` = VALUES(`%s`)", col, col))
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
    
    /**
     * 判断是否为关键表
     */
    private boolean isCriticalTable(String tableName) {
        return Arrays.asList("knowledge", "resource_form").contains(tableName);
    }
    
    /**
     * 递归删除目录
     */
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(child -> {
                try {
                    deleteDirectoryRecursively(child);
                } catch (IOException e) {
                    log.warn("删除子目录失败: {}", child, e);
                }
            });
        }
        Files.delete(path);
    }
} 