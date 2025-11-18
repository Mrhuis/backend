package com.example.backend.config;

import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.tool.DirectoryTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静态资源配置类
 * 配置媒体文件的访问路径映射
 */
@Slf4j
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${upload.resource.path}")
    private String uploadResourcePath;
    
    @Autowired
    private MediaRequestInterceptor mediaRequestInterceptor;
    
    @Autowired
    private ItemsRequestInterceptor itemsRequestInterceptor;
    
    @Autowired
    private PluginService pluginService;


    //处理前端的静态资源请求，将请求映射到真实的文件路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理相对路径和绝对路径
        String resourceLocation;

        DirectoryTool media = new DirectoryTool("media");
        // 获取启用的插件key，如果获取失败则使用默认值"V1"
        String pluginKey = getPluginKeyOrDefault();
        String absolutePathStr = media.findCurrentPluginFolderAbsolutePath(uploadResourcePath, pluginKey);

        if (absolutePathStr == null) {
            // 如果找不到绝对路径，使用基础资源路径
            absolutePathStr = uploadResourcePath;
        }

        Path absolutePath = Paths.get(absolutePathStr);

        if (uploadResourcePath.startsWith("..")) {
            // 相对路径：相对于当前工作目录
            Path currentPath = Paths.get("").toAbsolutePath();
            // 添加V1/V1/DataStructure/media路径，因为实际文件在这个子目录下

            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用相对路径配置静态资源: {} -> {}", uploadResourcePath, resourcePath);
        } else if (uploadResourcePath.startsWith("/") || uploadResourcePath.contains(":")) {
            // 绝对路径
            resourceLocation = "file:" + absolutePath;
            if (!resourceLocation.endsWith("/") && !resourceLocation.endsWith("\\")) {
                resourceLocation += File.separator;
            }
            log.info("使用绝对路径配置静态资源: {}", resourceLocation);
        } else {
            // 相对路径：相对于项目根目录
            Path currentPath = Paths.get("").toAbsolutePath();
            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用项目相对路径配置静态资源: {} -> {}", uploadResourcePath, resourcePath);
        }
        
        // 映射媒体文件访问路径
        // 访问路径: http://localhost:8080/media/**
        registry.addResourceHandler("/media/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600) // 设置缓存时间为1小时
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // URL解码资源路径
                        try {
                            String decodedPath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
                            log.debug("解码资源路径: {} -> {}", resourcePath, decodedPath);
                            return super.getResource(decodedPath, location);
                        } catch (Exception e) {
                            log.error("URL解码失败，使用原始路径: {}", resourcePath);
                            return super.getResource(resourcePath, location);
                        }
                    }
                });
        
        log.info("静态资源映射配置完成: /media/** -> {}", resourceLocation);
        
        // 添加调试信息：检查目录是否存在
        try {
            Path checkPath = Paths.get(resourceLocation.replace("file:", ""));
            log.info("静态资源目录存在检查: {} -> 存在: {}", checkPath, Files.exists(checkPath));
            if (Files.exists(checkPath)) {
                log.info("目录内容: {}", Files.list(checkPath).limit(5).map(Path::getFileName).toList());
            }
        } catch (Exception e) {
            log.error("检查静态资源目录时出错", e);
        }
        
        // 配置items资源路径映射
        configureItemsResourceHandlers(registry);
        
        // 配置message资源路径映射
        configureMessageResourceHandlers(registry);
        
        // 保留默认的静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
    
    /**
     * 配置items资源路径映射
     * @param registry 资源处理器注册器
     */
    private void configureItemsResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理相对路径和绝对路径
        String resourceLocation;

        DirectoryTool items = new DirectoryTool("items");
        // 获取启用的插件key，如果获取失败则使用默认值"V1"
        String pluginKey = getPluginKeyOrDefault();
        String absolutePathStr = items.findCurrentPluginFolderAbsolutePath(uploadResourcePath, pluginKey);

        if (absolutePathStr == null) {
            // 如果找不到绝对路径，使用基础资源路径
            absolutePathStr = uploadResourcePath;
        }

        Path absolutePath = Paths.get(absolutePathStr);

        if (uploadResourcePath.startsWith("..")) {
            // 相对路径：相对于当前工作目录
            Path currentPath = Paths.get("").toAbsolutePath();
            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用相对路径配置items静态资源: {} -> {}", uploadResourcePath, resourcePath);
        } else if (uploadResourcePath.startsWith("/") || uploadResourcePath.contains(":")) {
            // 绝对路径
            resourceLocation = "file:" + absolutePath;
            if (!resourceLocation.endsWith("/") && !resourceLocation.endsWith("\\")) {
                resourceLocation += File.separator;
            }
            log.info("使用绝对路径配置items静态资源: {}", resourceLocation);
        } else {
            // 相对路径：相对于项目根目录
            Path currentPath = Paths.get("").toAbsolutePath();
            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用项目相对路径配置items静态资源: {} -> {}", uploadResourcePath, resourcePath);
        }
        
        // 映射items文件访问路径
        // 访问路径: http://localhost:8080/items/**
        registry.addResourceHandler("/items/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600) // 设置缓存时间为1小时
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // URL解码资源路径
                        try {
                            String decodedPath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
                            log.debug("解码items资源路径: {} -> {}", resourcePath, decodedPath);
                            return super.getResource(decodedPath, location);
                        } catch (Exception e) {
                            log.error("URL解码失败，使用原始路径: {}", resourcePath);
                            return super.getResource(resourcePath, location);
                        }
                    }
                });
        
        log.info("Items静态资源映射配置完成: /items/** -> {}", resourceLocation);
        
        // 添加调试信息：检查目录是否存在
        try {
            Path checkPath = Paths.get(resourceLocation.replace("file:", ""));
            log.info("Items静态资源目录存在检查: {} -> 存在: {}", checkPath, Files.exists(checkPath));
            if (Files.exists(checkPath)) {
                log.info("Items目录内容: {}", Files.list(checkPath).limit(5).map(Path::getFileName).toList());
            }
        } catch (Exception e) {
            log.error("检查Items静态资源目录时出错", e);
        }
    }
    
    /**
     * 配置message资源路径映射
     * @param registry 资源处理器注册器
     */
    private void configureMessageResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理相对路径和绝对路径
        String resourceLocation;

        DirectoryTool message = new DirectoryTool("message");
        // 获取启用的插件key，如果获取失败则使用默认值"V1"
        String pluginKey = getPluginKeyOrDefault();
        String absolutePathStr = message.findCurrentPluginFolderAbsolutePath(uploadResourcePath, pluginKey);

        if (absolutePathStr == null) {
            // 如果找不到绝对路径，使用基础资源路径
            absolutePathStr = uploadResourcePath;
        }

        Path absolutePath = Paths.get(absolutePathStr);

        if (uploadResourcePath.startsWith("..")) {
            // 相对路径：相对于当前工作目录
            Path currentPath = Paths.get("").toAbsolutePath();
            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用相对路径配置message静态资源: {} -> {}", uploadResourcePath, resourcePath);
        } else if (uploadResourcePath.startsWith("/") || uploadResourcePath.contains(":")) {
            // 绝对路径
            resourceLocation = "file:" + absolutePath;
            if (!resourceLocation.endsWith("/") && !resourceLocation.endsWith("\\")) {
                resourceLocation += File.separator;
            }
            log.info("使用绝对路径配置message静态资源: {}", resourceLocation);
        } else {
            // 相对路径：相对于项目根目录
            Path currentPath = Paths.get("").toAbsolutePath();
            Path resourcePath = absolutePath;
            resourceLocation = "file:" + resourcePath.toString() + File.separator;
            log.info("使用项目相对路径配置message静态资源: {} -> {}", uploadResourcePath, resourcePath);
        }
        
        // 映射message文件访问路径
        // 访问路径: http://localhost:8080/message/**
        registry.addResourceHandler("/message/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600) // 设置缓存时间为1小时
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // URL解码资源路径
                        try {
                            String decodedPath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
                            log.debug("解码message资源路径: {} -> {}", resourcePath, decodedPath);
                            return super.getResource(decodedPath, location);
                        } catch (Exception e) {
                            log.error("URL解码失败，使用原始路径: {}", resourcePath);
                            return super.getResource(resourcePath, location);
                        }
                    }
                });
        
        log.info("Message静态资源映射配置完成: /message/** -> {}", resourceLocation);
        
        // 添加调试信息：检查目录是否存在
        try {
            Path checkPath = Paths.get(resourceLocation.replace("file:", ""));
            log.info("Message静态资源目录存在检查: {} -> 存在: {}", checkPath, Files.exists(checkPath));
            if (Files.exists(checkPath)) {
                log.info("Message目录内容: {}", Files.list(checkPath).limit(5).map(Path::getFileName).toList());
            }
        } catch (Exception e) {
            log.error("检查Message静态资源目录时出错", e);
        }
    }
    
    /**
     * 获取当前启用的插件key，如果获取失败则返回默认值"V1"
     * @return 插件key
     */
    private String getPluginKeyOrDefault() {
        try {
            String pluginKey = pluginService.getEnabledPluginKey();
            if (pluginKey != null && !pluginKey.isEmpty()) {
                return pluginKey;
            }
        } catch (Exception e) {
            log.warn("获取启用插件key失败: {}", e.getMessage());
        }
        log.info("使用默认插件key: V1");
        return "V1";
    }

    //注册拦截器
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册媒体请求拦截器
        registry.addInterceptor(mediaRequestInterceptor)
                .addPathPatterns("/media/**");
        log.info("媒体请求拦截器已注册");
        
        // 注册items请求拦截器
        registry.addInterceptor(itemsRequestInterceptor)
                .addPathPatterns("/items/**");
        log.info("Items请求拦截器已注册");
    }
}