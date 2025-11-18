package com.example.backend.service.teacher.message_center.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.Message;
import com.example.backend.mapper.MessageMapper;
import com.example.backend.service.teacher.message_center.TeacherMessageCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TeacherMessageCenterServiceImpl
 * Package: com.example.backend.service.teacher.message_center.impl
 * Description: 教师端消息中心服务实现类
 *
 * @Author 陈昊锡
 * @Create 2025/11/17 14:50
 * @Version 1.0
 */
@Service
public class TeacherMessageCenterServiceImpl implements TeacherMessageCenterService {

    private static final Logger log = LoggerFactory.getLogger(TeacherMessageCenterServiceImpl.class);

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public boolean addMessage(TeacherAddMessageDto req) {
        try {
            Long convId = resolveConvId(req);
            // 创建消息对象
            Message message = new Message();
            message.setConvId(convId);
            message.setSenderKey(req.getSenderKey());
            message.setReceiverKey(req.getReceiverKey());
            message.setContent(req.getContent());
            message.setAttachUrl(req.getAttachUrl());
            message.setMsgType(req.getMsgType());
            message.setSendTime(req.getSendTime());

            // 插入数据库
            int result = messageMapper.insert(message);
            return result > 0;
        } catch (Exception e) {
            log.error("添加消息失败", e);
            throw new RuntimeException("添加消息失败: " + e.getMessage());
        }
    }

    @Override
    public List<Message> getMessageListByUserKey(TeacherQueryMessageListDto req) {
        try {
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();

            if (req != null && req.getUserKey() != null) {
                // 查询条件：用户是发送者且未删除，或用户是接收者且未删除
                queryWrapper.and(wrapper ->
                    wrapper.eq("sender_key", req.getUserKey()).eq("sender_delete", 0)
                           .or()
                           .eq("receiver_key", req.getUserKey()).eq("receiver_delete", 0)
                );
            }

            // 按发送时间升序排列
            queryWrapper.orderByAsc("send_time");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行消息查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<Message> result = messageMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取消息列表失败", e);
            throw new RuntimeException("获取消息列表失败: " + e.getMessage());
        }
    }

    @Override
    public Long getMessagesCount(TeacherQueryMessageListDto req) {
        try {
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();

            if (req != null && req.getUserKey() != null) {
                // 查询条件：用户是发送者且未删除，或用户是接收者且未删除
                queryWrapper.and(wrapper ->
                    wrapper.eq("sender_key", req.getUserKey()).eq("sender_delete", 0)
                           .or()
                           .eq("receiver_key", req.getUserKey()).eq("receiver_delete", 0)
                );
            }

            log.info("执行消息计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = messageMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取消息总数失败", e);
            throw new RuntimeException("获取消息总数失败: " + e.getMessage());
        }
    }

    @Override
    public boolean revokeMessage(TeacherRevokeMessageDto req) {
        try {
            // 先查询消息是否存在
            Message message = messageMapper.selectById(req.getId());
            if (message == null) {
                throw new RuntimeException("消息不存在");
            }

            // 验证是否是发送者操作
            if (!message.getSenderKey().equals(req.getUserKey())) {
                throw new RuntimeException("只能撤回自己发送的消息");
            }

            // 检查消息发送时间是否超过撤回限制（假设为2分钟）
            long timeDiff = System.currentTimeMillis() - message.getSendTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (timeDiff > 2 * 60 * 1000) {
                throw new RuntimeException("消息发送超过2分钟，无法撤回");
            }

            // 构建更新条件
            UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());
            updateWrapper.set("is_revoked", 1);

            // 执行更新
            int result = messageMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            log.error("撤回消息失败", e);
            throw new RuntimeException("撤回消息失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteMessage(TeacherDeleteMessageDto req) {
        try {
            // 先查询消息是否存在
            Message message = messageMapper.selectById(req.getId());
            if (message == null) {
                throw new RuntimeException("消息不存在");
            }

            // 构建更新条件
            UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 判断是发送者还是接收者操作
            if (message.getSenderKey().equals(req.getUserKey())) {
                // 发送者删除
                updateWrapper.set("sender_delete", 1);
            } else if (message.getReceiverKey().equals(req.getUserKey())) {
                // 接收者删除
                updateWrapper.set("receiver_delete", 1);
            } else {
                throw new RuntimeException("只能删除自己发送或接收的消息");
            }

            // 执行更新
            int result = messageMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            log.error("删除消息失败", e);
            throw new RuntimeException("删除消息失败: " + e.getMessage());
        }
    }

    /**
     * 解析并生成会话ID
     *
     * @param req 消息请求
     * @return 会话ID
     */
    private Long resolveConvId(TeacherAddMessageDto req) {
        if (req == null) {
            throw new IllegalArgumentException("消息参数不能为空");
        }
        if (req.getSenderKey() == null || req.getReceiverKey() == null) {
            throw new IllegalArgumentException("发送者和接收者key不能为空");
        }
        if (req.getConvId() != null) {
            return req.getConvId();
        }

        Long existingConvId = findExistingConvId(req.getSenderKey(), req.getReceiverKey());
        if (existingConvId != null) {
            return existingConvId;
        }

        return IdWorker.getId();
    }

    /**
     * 根据双方历史消息查找会话ID
     *
     * @param senderKey   发送者key
     * @param receiverKey 接收者key
     * @return 历史会话ID或null
     */
    private Long findExistingConvId(String senderKey, String receiverKey) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.select("conv_id")
                .and(w -> w.eq("sender_key", senderKey).eq("receiver_key", receiverKey)
                        .or()
                        .eq("sender_key", receiverKey).eq("receiver_key", senderKey))
                .isNotNull("conv_id")
                .orderByDesc("send_time")
                .last("LIMIT 1");

        Message existing = messageMapper.selectOne(wrapper);
        return existing != null ? existing.getConvId() : null;
    }
}