package com.example.backend.controller.teacher;


import com.example.backend.service.admin.resource_manage.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/course-management")
public class TeacherCourseManagementController {

    private static final Logger log = LoggerFactory.getLogger(TeacherCourseManagementController.class);

    @Autowired
    private ResourceService resourceService;

    /**
     * 上传教学资源ZIP包
     * @param file ZIP文件
     * @param uploaderId 上传者ID
     * @return 成功或失败的响应
     */
    @PostMapping("/upload-zip")
    public ResponseEntity<Map<String, Object>> uploadResourceZip(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploaderId") String uploaderId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            response.put("error", "文件为空");
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查文件大小
        long fileSize = file.getSize();
        long maxSize = 3L * 1024 * 1024 * 1024; // 3GB
        if (fileSize > maxSize) {
            response.put("error", "文件过大");
            response.put("message", "文件大小不能超过3GB");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 检查文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
            response.put("error", "文件格式错误");
            response.put("message", "只能上传ZIP格式的文件");
            return ResponseEntity.badRequest().body(response);
        }
        
        log.info("开始处理文件上传，文件名: {}, 大小: {} bytes, 上传者ID: {}", 
                fileName, fileSize, uploaderId);
        
        try {
            resourceService.storeResource(file, uploaderId);
            
            response.put("success", true);
            response.put("message", "资源包上传成功，等待管理员审核");
            log.info("文件上传成功: {}", fileName);
            
            return ResponseEntity.ok(response);
            
        } catch (EOFException e) {
            log.error("文件上传过程中连接中断: {}", e.getMessage(), e);
            response.put("error", "上传中断");
            response.put("message", "上传过程中连接中断，请检查网络连接后重试");
            return ResponseEntity.status(408).body(response);
            
        } catch (MaxUploadSizeExceededException e) {
            log.error("文件大小超过限制: {}", e.getMessage(), e);
            response.put("error", "文件过大");
            response.put("message", "文件大小超过了系统限制（3GB）");
            return ResponseEntity.status(413).body(response);
            
        } catch (MultipartException e) {
            log.error("文件上传处理失败: {}", e.getMessage(), e);
            response.put("error", "上传失败");
            response.put("message", "文件上传处理失败，请检查文件格式后重试");
            return ResponseEntity.status(400).body(response);
            
        } catch (IOException e) {
            log.error("文件存储失败: {}", e.getMessage(), e);
            response.put("error", "存储失败");
            response.put("message", "文件存储过程中发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
            
        } catch (Exception e) {
            log.error("文件上传过程中发生未知错误: {}", e.getMessage(), e);
            response.put("error", "上传失败");
            response.put("message", "文件上传过程中发生未知错误，请稍后重试");
            return ResponseEntity.status(500).body(response);
        }
    }
}
