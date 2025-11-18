package com.example.backend.service.admin.message_center;

import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: AdminMessageCenterService
 * Package: com.example.backend.service.admin.message_center
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 10:01
 * @Version 1.0
 */
public interface AdminMessageCenterService {
    /**
     * 存储消息图片
     * @param file 上传的图片文件
     * @return 图片访问URL
     */
    String storeMessageImage(MultipartFile file);
}
