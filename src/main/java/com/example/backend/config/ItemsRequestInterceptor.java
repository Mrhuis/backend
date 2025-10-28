package com.example.backend.config;

import com.example.backend.entity.Plugin;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.tool.DirectoryTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Items请求拦截器
 * 用于调试items资源访问
 */
@Slf4j
@Component
public class ItemsRequestInterceptor implements HandlerInterceptor {

    @Value("${upload.resource.path}")
    private String baseResourcePath;

    @Autowired
    private PluginService pluginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");

        log.info("=== Items资源请求调试开始 ===");
        log.info("请求URI: {}", requestURI);
        log.info("请求方法: {}", method);
        log.info("请求来源: {}", referer);
        log.info("用户代理: {}", userAgent);

        if (requestURI.startsWith("/items/")) {
            // 提取文件路径部分（去除/items/前缀）
            String filePath = requestURI.substring("/items/".length());
            log.info("原始文件路径: {}", filePath);

            String decodedFilePath;
            try {
                decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
                log.info("解码后文件路径: {}", decodedFilePath);
            } catch (Exception e) {
                log.error("URL解码失败: {}", e.getMessage());
                decodedFilePath = filePath; // 如果解码失败，使用原始路径
            }

            try {
                // 构建完整的文件路径
                DirectoryTool items = new DirectoryTool("items");
                // 获取启用的插件，如果不存在则使用默认值
                String pluginKey = "V1"; // 默认值
                try {
                    Plugin enabledPlugin = pluginService.getEnabledPlugin();
                    if (enabledPlugin != null && enabledPlugin.getPluginKey() != null) {
                        pluginKey = enabledPlugin.getPluginKey();
                    }
                } catch (Exception e) {
                    log.warn("获取启用插件失败，使用默认插件键: {}", e.getMessage());
                }
                
                String absolutePathStr = items.findCurrentPluginFolderAbsolutePath(baseResourcePath, pluginKey);
                if (absolutePathStr == null) {
                    log.warn("无法找到绝对路径，使用基础资源路径");
                    absolutePathStr = baseResourcePath;
                }
                
                Path absolutePath = Paths.get(absolutePathStr);
                Path fullFilePath = absolutePath.resolve(decodedFilePath);

                log.info("基础资源路径: {}", baseResourcePath);
                log.info("完整文件路径: {}", fullFilePath);
                log.info("文件是否存在: {}", Files.exists(fullFilePath));

                if (Files.exists(fullFilePath)) {
                    log.info("文件大小: {} bytes", Files.size(fullFilePath));
                } else {
                    log.warn("文件不存在，检查目录结构:");
                    // 检查父目录是否存在
                    Path parentDir = fullFilePath.getParent();
                    if (parentDir != null && Files.exists(parentDir)) {
                        log.info("父目录存在，列出目录内容:");
                        Files.list(parentDir)
                                .limit(10)
                                .forEach(path -> log.info("  - {}", path.getFileName()));
                    } else {
                        log.warn("父目录不存在: {}", parentDir);
                    }
                }
            } catch (Exception e) {
                log.error("构建文件路径时出错: ", e);
            }

            log.info("=== Items资源请求调试结束 ===");
        }

        // 继续处理请求
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/items/")) {
            log.info("Items资源请求完成 - URI: {}, 状态码: {}", requestURI, response.getStatus());
            if (response.getStatus() >= 400) {
                log.warn("请求失败 - 状态码: {}", response.getStatus());
            }
        }
    }
}