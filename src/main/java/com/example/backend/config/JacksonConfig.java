package com.example.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    /**
     * 自定义日期时间格式
     */
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 自定义日期时间格式化器
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /**
     * 配置ObjectMapper Bean
     * 支持多种日期时间格式的反序列化
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册JSR310模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 注册自定义的LocalDateTime序列化器和反序列化器
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        
        objectMapper.registerModule(javaTimeModule);
        
        return objectMapper;
    }

    /**
     * 自定义LocalDateTime反序列化器
     * 支持多种日期时间格式
     */
    public static class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
        
        public CustomLocalDateTimeDeserializer() {
            super(DATETIME_FORMATTER);
        }

        @Override
        public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p, 
                                      com.fasterxml.jackson.databind.DeserializationContext ctxt) 
                throws java.io.IOException {
            
            String text = p.getText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            
            try {
                // 首先尝试标准ISO格式（包含T的格式）
                if (text.contains("T")) {
                    // 处理带时区的ISO格式（如：2025-10-30T02:16:09.806Z）
                    // 移除时区信息（Z或+08:00等），保留日期时间部分
                    String cleanedText = text;
                    // 移除Z后缀（UTC时区）
                    if (cleanedText.endsWith("Z")) {
                        cleanedText = cleanedText.substring(0, cleanedText.length() - 1);
                    }
                    // 移除时区偏移（如+08:00, -05:00等）
                    if (cleanedText.matches(".*[+-]\\d{2}:?\\d{2}$")) {
                        cleanedText = cleanedText.replaceFirst("[+-]\\d{2}:?\\d{2}$", "");
                    }
                    // 移除毫秒部分（如果存在），因为LocalDateTime.parse可能不支持带毫秒的格式
                    if (cleanedText.contains(".")) {
                        int dotIndex = cleanedText.indexOf(".");
                        cleanedText = cleanedText.substring(0, dotIndex);
                    }
                    return LocalDateTime.parse(cleanedText);
                }
                
                // 尝试自定义格式 "yyyy-MM-dd HH:mm:ss"
                if (text.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    return LocalDateTime.parse(text, DATETIME_FORMATTER);
                }
                
                // 尝试其他常见格式
                if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return LocalDateTime.parse(text + " 00:00:00", DATETIME_FORMATTER);
                }
                
                // 如果都不匹配，抛出异常
                throw new IllegalArgumentException("Unsupported date time format: " + text);
                
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to parse date time: " + text, e);
            }
        }
    }
} 