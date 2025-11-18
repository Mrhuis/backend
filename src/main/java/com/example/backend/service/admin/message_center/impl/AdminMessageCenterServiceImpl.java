package com.example.backend.service.admin.message_center.impl;

import com.example.backend.service.admin.message_center.AdminMessageCenterService;
import com.example.backend.tool.DirectoryTool;
import com.example.backend.tool.media.MultipartFileTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: AdminMessageCenterServiceImpl
 * Package: com.example.backend.service.admin.message_center.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 10:02
 * @Version 1.0
 */
@Slf4j
@Service
public class AdminMessageCenterServiceImpl implements AdminMessageCenterService {




    @Value("${upload.resource.path}")
    private String baseResourcePath;

    @Override
    public String storeMessageImage(MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        Path absolutePath = Paths.get(baseResourcePath).toAbsolutePath().normalize();
        MultipartFileTool imageTool = new MultipartFileTool(
                absolutePath.toString(), // 基础资源路径
                allowedExtensions,       // 允许的文件扩展名
                10 * 1024 * 1024,        // 最大文件大小10MB
                "image/"                 // 内容类型前缀
        );

        try {
            log.info("\n--- 验证图片文件 ---");
            log.info("待验证文件: {}", file.getOriginalFilename());
            log.info("文件大小: {} 字节", file.getSize());
            log.info("内容类型: {}", file.getContentType());

            imageTool.validateFile(file);
            log.info("文件验证通过");

            log.info("\n--- 生成唯一图片文件名 ---");
            String originalFileName = file.getOriginalFilename();
            String fileExtension = MultipartFileTool.getFileExtension(originalFileName);
            String uniqueFileName = imageTool.generateUniqueFileName(fileExtension);
            log.info("原始文件名: {}", originalFileName);
            log.info("文件扩展名: {}", fileExtension);
            log.info("生成的唯一文件名: {}", uniqueFileName);

            log.info("\n--- 构建目标路径 ---");
            DirectoryTool message = new DirectoryTool("message");
            String targetDirectory = message.findOrCreateTargetDirectory(baseResourcePath);
            Path targetPath = Paths.get(targetDirectory, uniqueFileName);
            log.info("目标目录: {}", targetDirectory);
            log.info("完整目标路径: {}", targetPath.toString());

            log.info("\n--- 保存文件 ---");
            imageTool.saveFile(file, targetPath);
            log.info("文件已保存到: {}", targetPath.toString());

            log.info("\n--- 计算相对URL ---");
            String relativeUrl = "/message/" + uniqueFileName;
            log.info("相对URL: {}", relativeUrl);

            return relativeUrl;

        } catch (Exception e) {
            log.error("图片上传处理失败: ", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }
    }
}
