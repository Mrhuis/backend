package com.example.backend.service.teacher.message_center;

import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.Message;

import java.util.List;

/**
 * ClassName: TeacherMessageCenterService
 * Package: com.example.backend.service.teacher.message_center
 * Description: 教师端消息中心服务接口
 *
 * @Author 陈昊锡
 * @Create 2025/11/17 14:50
 * @Version 1.0
 */
public interface TeacherMessageCenterService {
    /**
     * 添加消息
     *
     * @param req 消息信息
     * @return 是否添加成功
     */
    boolean addMessage(TeacherAddMessageDto req);

    /**
     * 根据用户ID查询消息列表（发送者或接收者为该用户）
     *
     * @param req 查询条件
     * @return 消息列表
     */
    List<Message> getMessageListByUserId(TeacherQueryMessageListDto req);

    /**
     * 获取消息总数
     *
     * @param req 查询条件
     * @return 消息总数
     */
    Long getMessagesCount(TeacherQueryMessageListDto req);

    /**
     * 撤回消息
     *
     * @param req 消息ID和用户ID
     * @return 是否撤回成功
     */
    boolean revokeMessage(TeacherRevokeMessageDto req);

    /**
     * 删除消息（逻辑删除）
     *
     * @param req 消息ID和用户ID
     * @return 是否删除成功
     */
    boolean deleteMessage(TeacherDeleteMessageDto req);
}