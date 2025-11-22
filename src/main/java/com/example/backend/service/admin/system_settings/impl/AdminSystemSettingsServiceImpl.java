package com.example.backend.service.admin.system_settings.impl;

import com.example.backend.service.admin.system_settings.AdminSystemSettingsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统设置服务实现类
 * 调用Python端API管理推荐系统相关配置
 */
@Service
public class AdminSystemSettingsServiceImpl implements AdminSystemSettingsService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminSystemSettingsServiceImpl.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Python服务的地址
    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;
    
    public AdminSystemSettingsServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public Map<String, Object> getConfig() {
        try {
            String url = pythonServiceUrl + "/config";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                if (success) {
                    JsonNode dataNode = root.path("data");
                    Map<String, Object> config = new HashMap<>();
                    
                    if (dataNode.isObject()) {
                        dataNode.fields().forEachRemaining(entry -> {
                            config.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), Object.class));
                        });
                    }
                    
                    return config;
                } else {
                    String message = root.path("message").asText("获取配置失败");
                    throw new RuntimeException(message);
                }
            } else {
                throw new RuntimeException("获取配置失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用Python服务获取配置时出错", e);
            throw new RuntimeException("获取配置失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> updateConfig(Map<String, Object> updateConfig) {
        try {
            String url = pythonServiceUrl + "/config";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(updateConfig, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                if (success) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("message", root.path("message").asText());
                    
                    List<String> updatedConfigs = new ArrayList<>();
                    JsonNode updatedConfigsNode = root.path("updated_configs");
                    if (updatedConfigsNode.isArray()) {
                        for (JsonNode node : updatedConfigsNode) {
                            updatedConfigs.add(node.asText());
                        }
                    }
                    result.put("updated_configs", updatedConfigs);
                    
                    List<String> invalidConfigs = new ArrayList<>();
                    JsonNode invalidConfigsNode = root.path("invalid_configs");
                    if (invalidConfigsNode.isArray()) {
                        for (JsonNode node : invalidConfigsNode) {
                            invalidConfigs.add(node.asText());
                        }
                    }
                    result.put("invalid_configs", invalidConfigs);
                    
                    return result;
                } else {
                    String message = root.path("message").asText("更新配置失败");
                    throw new RuntimeException(message);
                }
            } else {
                throw new RuntimeException("更新配置失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用Python服务更新配置时出错", e);
            throw new RuntimeException("更新配置失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> exportConfig() {
        try {
            String url = pythonServiceUrl + "/config/export";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                if (success) {
                    JsonNode dataNode = root.path("data");
                    Map<String, Object> config = new HashMap<>();
                    
                    if (dataNode.isObject()) {
                        dataNode.fields().forEachRemaining(entry -> {
                            config.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), Object.class));
                        });
                    }
                    
                    return config;
                } else {
                    String message = root.path("message").asText("导出配置失败");
                    throw new RuntimeException(message);
                }
            } else {
                throw new RuntimeException("导出配置失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用Python服务导出配置时出错", e);
            throw new RuntimeException("导出配置失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> importConfig(Map<String, Object> importConfig) {
        try {
            String url = pythonServiceUrl + "/config/import";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(importConfig, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                if (success) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("message", root.path("message").asText());
                    
                    List<String> importedConfigs = new ArrayList<>();
                    JsonNode importedConfigsNode = root.path("imported_configs");
                    if (importedConfigsNode.isArray()) {
                        for (JsonNode node : importedConfigsNode) {
                            importedConfigs.add(node.asText());
                        }
                    }
                    result.put("imported_configs", importedConfigs);
                    
                    return result;
                } else {
                    String message = root.path("message").asText("导入配置失败");
                    throw new RuntimeException(message);
                }
            } else {
                throw new RuntimeException("导入配置失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用Python服务导入配置时出错", e);
            throw new RuntimeException("导入配置失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> listModels() {
        try {
            String url = pythonServiceUrl + "/models";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                if (success) {
                    List<String> modelNames = new ArrayList<>();
                    JsonNode dataNode = root.path("data");
                    if (dataNode.isArray()) {
                        for (JsonNode node : dataNode) {
                            modelNames.add(node.asText());
                        }
                    }
                    
                    return modelNames;
                } else {
                    String message = root.path("message").asText("获取模型列表失败");
                    throw new RuntimeException(message);
                }
            } else {
                throw new RuntimeException("获取模型列表失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("调用Python服务获取模型列表时出错", e);
            throw new RuntimeException("获取模型列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> runPipeline() {
        // 为流水线执行创建专门的RestTemplate，设置长超时时间（40分钟）
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 连接超时30秒
        factory.setReadTimeout(40 * 60 * 1000); // 读取超时40分钟（2400000毫秒）
        RestTemplate pipelineRestTemplate = new RestTemplate(factory);
        
        try {
            String url = pythonServiceUrl + "/run-pipeline";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> requestEntity = new HttpEntity<>("{}", headers);
            ResponseEntity<String> response = pipelineRestTemplate.postForEntity(url, requestEntity, String.class);
            
            // 处理成功响应
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                boolean success = root.path("success").asBoolean(false);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", success);
                result.put("message", root.path("message").asText());
                
                if (root.has("last_run")) {
                    result.put("last_run", root.path("last_run").asText());
                }
                
                return result;
            } else {
                throw new RuntimeException("执行流水线任务失败，HTTP状态码: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // 处理HTTP客户端错误（如409 CONFLICT, 429 TOO_MANY_REQUESTS等）
            int statusCodeValue = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();
            
            log.warn("调用Python服务执行流水线任务时收到HTTP错误，状态码: {}, 响应体: {}", statusCodeValue, responseBody);
            
            // 尝试解析响应体中的错误消息
            try {
                if (responseBody != null && !responseBody.isEmpty()) {
                    JsonNode root = objectMapper.readTree(responseBody);
                    String message = root.path("message").asText();
                    if (message != null && !message.isEmpty()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", message);
                        return result;
                    }
                }
            } catch (Exception parseException) {
                log.warn("解析错误响应体失败", parseException);
            }
            
            // 如果无法解析响应体，返回默认错误消息
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            if (statusCodeValue == HttpStatus.CONFLICT.value()) {
                result.put("message", "流水线任务正在执行中，请稍后再试");
            } else if (statusCodeValue == HttpStatus.TOO_MANY_REQUESTS.value()) {
                result.put("message", "请求过于频繁，请稍后再试");
            } else {
                result.put("message", "执行流水线任务失败: " + e.getMessage());
            }
            return result;
        } catch (Exception e) {
            log.error("调用Python服务执行流水线任务时出错", e);
            throw new RuntimeException("执行流水线任务失败: " + e.getMessage(), e);
        }
    }
}