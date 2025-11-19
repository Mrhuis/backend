package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.Message;
import com.example.backend.service.teacher.message_center.TeacherMessageCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherMessageCenterController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端消息中心控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/17 14:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/message")
public class TeacherMessageCenterController {

    private static final Logger log = LoggerFactory.getLogger(TeacherMessageCenterController.class);

    @Autowired
    private TeacherMessageCenterService teacherMessageCenterService;

    /**
     * 添加消息
     *
     * @param req 消息信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addMessage(@RequestBody TeacherAddMessageDto req) {
        try {
            boolean success = teacherMessageCenterService.addMessage(req);
            if (success) {
                return Result.success("消息发送成功");
            } else {
                return Result.error("消息发送失败");
            }
        } catch (Exception e) {
            return Result.error("发送消息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 根据用户key查询消息列表（发送者或接收者为该用户）
     *
     * @param req 查询条件
     * @return 消息列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getMessageList(@RequestBody TeacherQueryMessageListDto req) {
        try {
            log.info("收到获取消息列表请求，参数: {}", req);

            // 获取消息列表
            List<Message> messages = teacherMessageCenterService.getMessageListByUserKey(req);
            log.info("获取到消息列表，数量: {}", messages.size());

            // 获取总数
            Long total = teacherMessageCenterService.getMessagesCount(req);
            log.info("获取到消息总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(messages.stream().map(message -> (Object) message).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            log.info("返回消息列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取消息列表失败", e);
            return Result.error("获取消息列表失败: " + e.getMessage());
        }
    }

    /**
     * 撤回消息
     *
     * @param req 消息ID和用户ID
     * @return 撤回结果
     */
    @PostMapping("/revoke")
    public Result revokeMessage(@RequestBody TeacherRevokeMessageDto req) {
        try {
            boolean success = teacherMessageCenterService.revokeMessage(req);
            if (success) {
                return Result.success("消息撤回成功");
            } else {
                return Result.error("消息撤回失败");
            }
        } catch (Exception e) {
            return Result.error("撤回消息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除消息（逻辑删除）
     *
     * @param req 消息ID和用户ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result deleteMessage(@RequestBody TeacherDeleteMessageDto req) {
        try {
            boolean success = teacherMessageCenterService.deleteMessage(req);
            if (success) {
                return Result.success("消息删除成功");
            } else {
                return Result.error("消息删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除消息时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 标记会话消息为已读
     *
     * @param req 包含会话ID和用户key的请求对象
     * @return 标记结果
     */
    @PostMapping("/mark-as-read")
    public Result markAsRead(@RequestBody TeacherMarkAsReadDto req) {
        try {
            boolean success = teacherMessageCenterService.markAsRead(req);
            if (success) {
                return Result.success("消息标记为已读成功");
            } else {
                return Result.error("消息标记为已读失败");
            }
        } catch (Exception e) {
            return Result.error("标记消息为已读时发生错误: " + e.getMessage());
        }
    }
}