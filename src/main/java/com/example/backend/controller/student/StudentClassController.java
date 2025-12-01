package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentJoinClassDto;
import com.example.backend.service.student.class_manage.StudentClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: StudentClassController
 * Package: com.example.backend.controller.student
 * Description: 学生端班级管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 10:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/student/class")
public class StudentClassController {

    private static final Logger log = LoggerFactory.getLogger(StudentClassController.class);

    @Autowired
    private StudentClassService studentClassService;

    /**
     * 学生通过邀请码加入班级
     *
     * @param req 加入班级请求（包含邀请码和userKey）
     * @return 加入结果
     */
    @PostMapping("/join")
    public Result<String> joinClass(@RequestBody StudentJoinClassDto req) {
        try {
            log.info("收到学生加入班级请求，邀请码: {}, userKey: {}", req.getInviteCode(), req.getUserKey());

            if (!StringUtils.hasText(req.getInviteCode())) {
                return Result.error("邀请码不能为空");
            }

            if (!StringUtils.hasText(req.getUserKey())) {
                return Result.error("用户标识不能为空");
            }

            boolean success = studentClassService.joinClassByInviteCode(req.getInviteCode(), req.getUserKey());
            if (success) {
                return Result.success("申请加入班级成功，等待教师审核");
            } else {
                return Result.error("加入班级失败");
            }
        } catch (Exception e) {
            log.error("学生加入班级失败", e);
            return Result.error(e.getMessage());
        }
    }
}

