package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.AdminUserAddDto;
import com.example.backend.controller.admin.dto.AdminUserQueryListDto;
import com.example.backend.controller.admin.dto.AdminUserUpdateDto;
import com.example.backend.controller.admin.vo.AdminSimpleUserVo;
import com.example.backend.controller.admin.vo.AdminUserBasicInfoVo;
import com.example.backend.controller.admin.vo.AdminUserQueryDetailVo;
import com.example.backend.entity.User;
import com.example.backend.service.admin.message_center.AdminMessageCenterService;
import com.example.backend.service.admin.user_manage.AdminUserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminUserManageController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/message")
public class AdminMessageCenterController {

    @Autowired
    private AdminMessageCenterService adminMessageCenterService;

    
    /**
     * 上传消息图片
     * 
     * @param file 上传的图片文件
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload-image")
    public Result<String> uploadMessageImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("收到消息图片上传请求，文件名: {}, 文件大小: {}", file.getOriginalFilename(), file.getSize());
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                log.warn("上传的文件为空");
                return Result.error("上传的文件不能为空");
            }
            
            // 检查文件类型（只允许图片格式）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("上传的文件不是图片格式，文件类型: {}", contentType);
                return Result.error("只允许上传图片文件");
            }
            
            // 调用服务保存图片并获取访问URL
            String imageUrl = adminMessageCenterService.storeMessageImage(file);
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                log.info("消息图片上传成功，访问URL: {}", imageUrl);
                return Result.success(imageUrl);
            } else {
                log.error("消息图片保存失败");
                return Result.error("图片保存失败");
            }
        } catch (Exception e) {
            log.error("消息图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}