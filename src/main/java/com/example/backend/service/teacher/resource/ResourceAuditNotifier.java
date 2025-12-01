package com.example.backend.service.teacher.resource;

import com.example.backend.controller.teacher.dto.TeacherAddMessageDto;
import com.example.backend.service.teacher.message_center.TeacherMessageCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 统一封装资源审核提醒的系统通知发送逻辑
 */
@Component
public class ResourceAuditNotifier {

    private static final Logger log = LoggerFactory.getLogger(ResourceAuditNotifier.class);

    private static final String SYSTEM_SENDER_KEY = "system";
    private static final String ADMIN_AUDITOR_KEY = "A0001";

    @Autowired
    private TeacherMessageCenterService teacherMessageCenterService;

    /**
     * 发送资源待审核通知给管理员
     *
     * @param uploaderKey 资源提交教师key
     * @param resourceType 资源类型文案
     * @param resourceName 资源名称/标识
     */
    public void notifyPendingAudit(String uploaderKey, String resourceType, String resourceName) {
        TeacherAddMessageDto messageDto = new TeacherAddMessageDto();
        messageDto.setSenderKey(SYSTEM_SENDER_KEY);
        messageDto.setReceiverKey(ADMIN_AUDITOR_KEY);
        messageDto.setMsgType(1);
        messageDto.setSendTime(LocalDateTime.now());
        messageDto.setContent(buildContent(uploaderKey, resourceType, resourceName));

        teacherMessageCenterService.addMessage(messageDto);
        log.info("资源审核提醒已发送，uploaderKey={}, resourceType={}, resourceName={}",
                uploaderKey, resourceType, resourceName);
    }

    private String buildContent(String uploaderKey, String resourceType, String resourceName) {
        String teacherKey = StringUtils.hasText(uploaderKey) ? uploaderKey : "未知教师";
        String title = StringUtils.hasText(resourceName) ? resourceName : resourceType;
        String typeText = StringUtils.hasText(resourceType) ? resourceType : "资源";
        return String.format("【资源审核】教师（Key %s）新增%s「%s」，请及时审核。",
                teacherKey, typeText, title);
    }
}

