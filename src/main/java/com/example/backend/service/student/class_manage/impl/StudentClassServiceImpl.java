package com.example.backend.service.student.class_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.entity.Class;
import com.example.backend.entity.ClassStudentEnrollment;
import com.example.backend.mapper.ClassMapper;
import com.example.backend.mapper.ClassStudentEnrollmentMapper;
import com.example.backend.service.student.class_manage.StudentClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 学生端班级管理服务实现类
 */
@Service
public class StudentClassServiceImpl implements StudentClassService {

    private static final Logger log = LoggerFactory.getLogger(StudentClassServiceImpl.class);

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ClassStudentEnrollmentMapper classStudentEnrollmentMapper;

    @Override
    public boolean joinClassByInviteCode(String inviteCode, String userKey) {
        try {
            // 1. 验证邀请码，查找对应的班级
            QueryWrapper<Class> classQueryWrapper = new QueryWrapper<>();
            classQueryWrapper.eq("invite_code", inviteCode);
            Class clazz = classMapper.selectOne(classQueryWrapper);

            if (clazz == null) {
                throw new RuntimeException("邀请码无效，未找到对应的班级");
            }

            // 2. 检查学生是否已经加入该班级
            QueryWrapper<ClassStudentEnrollment> enrollmentQueryWrapper = new QueryWrapper<>();
            enrollmentQueryWrapper.eq("class_key", clazz.getClassKey());
            enrollmentQueryWrapper.eq("user_key", userKey);
            ClassStudentEnrollment existingEnrollment = classStudentEnrollmentMapper.selectOne(enrollmentQueryWrapper);

            if (existingEnrollment != null) {
                throw new RuntimeException("您已经加入该班级");
            }

            // 3. 创建班级学生关系，状态为待审核（0）
            ClassStudentEnrollment enrollment = new ClassStudentEnrollment();
            enrollment.setClassKey(clazz.getClassKey());
            enrollment.setUserKey(userKey);
            enrollment.setStatus(0); // 待审核
            enrollment.setEnrolledAt(LocalDateTime.now());

            // 4. 插入数据库
            int result = classStudentEnrollmentMapper.insert(enrollment);
            return result > 0;
        } catch (Exception e) {
            log.error("学生加入班级失败: inviteCode={}, userKey={}", inviteCode, userKey, e);
            throw new RuntimeException("加入班级失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean leaveClass(String classKey, String userKey) {
        try {
            // 查询学生是否在该班级中
            QueryWrapper<ClassStudentEnrollment> enrollmentQueryWrapper = new QueryWrapper<>();
            enrollmentQueryWrapper.eq("class_key", classKey);
            enrollmentQueryWrapper.eq("user_key", userKey);
            ClassStudentEnrollment enrollment = classStudentEnrollmentMapper.selectOne(enrollmentQueryWrapper);

            if (enrollment == null) {
                throw new RuntimeException("您尚未加入该班级");
            }

            // 直接删除该关系
            int result = classStudentEnrollmentMapper.delete(enrollmentQueryWrapper);
            return result > 0;
        } catch (Exception e) {
            log.error("学生退出班级失败: classKey={}, userKey={}", classKey, userKey, e);
            throw new RuntimeException("退出班级失败: " + e.getMessage(), e);
        }
    }
}

