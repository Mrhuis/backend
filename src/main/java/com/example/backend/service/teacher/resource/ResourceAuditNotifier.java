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

    /**
     * 发送资源审核结果给上传者
     *
     * @param uploaderKey  资源提交教师key
     * @param resourceType 资源类型文案
     * @param resourceName 资源名称/标识
     * @param status       审核结果状态：ENABLED、DISABLED、REJECTED 等
     * @param reviewerKey  审核人key（管理员），可为空；为空时使用system作为发送者
     */
    public void notifyAuditResult(String uploaderKey,
                                  String resourceType,
                                  String resourceName,
                                  String status,
                                  String reviewerKey) {
        if (!StringUtils.hasText(uploaderKey)) {
            log.warn("uploaderKey 为空，跳过发送审核结果通知");
            return;
        }

        // 如果审核人和上传者相同，则不发送系统消息
        if (StringUtils.hasText(reviewerKey) && reviewerKey.equals(uploaderKey)) {
            log.info("审核人与上传者相同({})，跳过发送审核结果通知", uploaderKey);
            return;
        }

        String finalStatus = StringUtils.hasText(status) ? status : "UNKNOWN";
        String finalResourceType = StringUtils.hasText(resourceType) ? resourceType : "资源";
        String finalResourceName = StringUtils.hasText(resourceName) ? resourceName : finalResourceType;
        String senderKey = StringUtils.hasText(reviewerKey) ? reviewerKey : SYSTEM_SENDER_KEY;

        TeacherAddMessageDto messageDto = new TeacherAddMessageDto();
        messageDto.setSenderKey(senderKey);
        messageDto.setReceiverKey(uploaderKey);
        messageDto.setMsgType(1);
        messageDto.setSendTime(LocalDateTime.now());
        messageDto.setContent(buildAuditResultContent(finalResourceType, finalResourceName, finalStatus));

        teacherMessageCenterService.addMessage(messageDto);
        log.info("资源审核结果通知已发送，uploaderKey={}, resourceType={}, resourceName={}, status={}, reviewerKey={}",
                uploaderKey, resourceType, resourceName, status, reviewerKey);
    }

    private String buildAuditResultContent(String resourceType, String resourceName, String status) {
        String statusText;
        switch (status) {
            case "ENABLED":
                statusText = "审核通过，已启用";
                break;
            case "DISABLED":
                statusText = "审核通过，但已被禁用";
                break;
            case "REJECTED":
                statusText = "审核未通过";
                break;
            case "PENDING":
                statusText = "状态已变更为待审核";
                break;
            default:
                statusText = "状态更新为：" + status;
        }
        return String.format("【审核结果】您提交的%s「%s」%s。", resourceType, resourceName, statusText);
    }

    private String buildContent(String uploaderKey, String resourceType, String resourceName) {
        String teacherKey = StringUtils.hasText(uploaderKey) ? uploaderKey : "未知教师";
        String title = StringUtils.hasText(resourceName) ? resourceName : resourceType;
        String typeText = StringUtils.hasText(resourceType) ? resourceType : "资源";
        return String.format("【资源审核】教师（Key %s）新增%s「%s」，请及时审核。",
                teacherKey, typeText, title);
    }
}

