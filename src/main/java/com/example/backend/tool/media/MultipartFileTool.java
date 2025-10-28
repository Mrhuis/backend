package com.example.backend.tool.media;

import org.mp4parser.IsoFile;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: MultipartFileTool
 * Package: com.example.backend.tool.media
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 15:47
 * @Version 1.0
 */
public class MultipartFileTool {
    // 基础资源路径（用于计算相对URL）
    private final String baseResourcePath;
    // 允许的文件扩展名
    private final List<String> allowedExtensions;
    // 最大文件大小限制（字节）
    private final long maxFileSize;
    // 内容类型前缀（如"video/"、"image/"，用于校验Content-Type）
    private final String contentTypePrefix;

    /**
     * 构造函数：初始化文件处理参数
     * @param baseResourcePath 基础资源路径（用于计算相对URL）
     * @param allowedExtensions 允许的文件扩展名列表（如["mp4", "avi"]）
     * @param maxFileSize 最大文件大小（字节）
     * @param contentTypePrefix 内容类型前缀（如"video/"、"image/"）
     */
    public MultipartFileTool(String baseResourcePath,
                              List<String> allowedExtensions,
                              long maxFileSize,
                              String contentTypePrefix) {
        // 参数校验
        if (baseResourcePath == null || baseResourcePath.trim().isEmpty()) {
            throw new IllegalArgumentException("基础资源路径不能为空");
        }
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            throw new IllegalArgumentException("允许的文件扩展名不能为空");
        }
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("最大文件大小必须大于0");
        }
        if (contentTypePrefix == null || contentTypePrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("内容类型前缀不能为空");
        }

        this.baseResourcePath = baseResourcePath;
        this.allowedExtensions = allowedExtensions;
        this.maxFileSize = maxFileSize;
        this.contentTypePrefix = contentTypePrefix;
    }

    /**
     * 校验MultipartFile文件的合法性
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > maxFileSize) {
            long maxSizeMB = maxFileSize / 1024 / 1024;
            throw new RuntimeException("文件过大，最大支持" + maxSizeMB + "MB");
        }

        // 校验文件格式
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        if (fileExtension == null || !allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new RuntimeException("不支持的文件格式，允许格式: " + String.join(", ", allowedExtensions));
        }

        // 校验文件类型（Content-Type）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(contentTypePrefix)) {
            throw new RuntimeException("文件类型不合法，请上传" + contentTypePrefix.replace("/", "") + "类型文件");
        }
    }

    /**
     * 生成唯一文件名（避免重复）
     * @param fileExtension 文件扩展名（如"mp4"）
     * @return 唯一文件名
     */
    public String generateUniqueFileName(String fileExtension) {
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            throw new IllegalArgumentException("文件扩展名不能为空");
        }
        // 移除可能存在的点前缀（如".mp4" → "mp4"）
        String cleanedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
        return UUID.randomUUID().toString() + "." + cleanedExtension;
    }

    /**
     * 保存MultipartFile到目标路径
     * @param file 待保存的文件
     * @param targetPath 目标路径
     * @throws IOException 保存失败时抛出
     */
    public void saveFile(MultipartFile file, Path targetPath) throws IOException {
        // 确保父目录存在
        Files.createDirectories(targetPath.getParent());
        // 利用MultipartFile的transferTo方法保存文件（高效处理）
        file.transferTo(targetPath);
    }

    /**
     * 计算相对URL（去掉baseResourcePath部分）
     * @param fullPath 文件完整路径
     * @return 相对URL路径
     */
    public String calculateRelativeUrl(String fullPath) {
        if (fullPath == null || !fullPath.startsWith(baseResourcePath)) {
            throw new RuntimeException("文件路径不在基础资源路径范围内: " + fullPath);
        }
        // 移除基础路径
        String relativePath = fullPath.replace(baseResourcePath, "");
        // 统一路径分隔符为/（适应HTTP URL规范）
        return relativePath.replace(File.separator, "/").replaceAll("^/+", "/");
    }

    /**
     * 获取视频时长（秒）- 视频专用方法
     * @return 时长（秒）
     */
    public static int getVideoDuration(File file) {
        try (IsoFile iso = new IsoFile(file)) {
            double seconds = (double)
                    iso.getMovieBox().getMovieHeaderBox().getDuration() /
                    iso.getMovieBox().getMovieHeaderBox().getTimescale();
            return (int) Math.round(seconds);
        } catch (Exception e) {
            throw new RuntimeException("解析失败", e);
        }
    }
    /**
     * 获取文件拓展名
     * @return 拓展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // Getter方法（如需外部获取配置参数）
    public String getBaseResourcePath() {
        return baseResourcePath;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public String getContentTypePrefix() {
        return contentTypePrefix;
    }
}
