package com.example.backend.service.teacher.class_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ClassStudentEnrollment;
import com.example.backend.mapper.ClassStudentEnrollmentMapper;
import com.example.backend.service.teacher.class_manage.TeacherClassStudentEnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师端班级学生关系管理服务实现类
 */
@Service
public class TeacherClassStudentEnrollmentServiceImpl implements TeacherClassStudentEnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(TeacherClassStudentEnrollmentServiceImpl.class);

    @Autowired
    private ClassStudentEnrollmentMapper classStudentEnrollmentMapper;

    @Override
    public List<ClassStudentEnrollment> getClassStudentEnrollmentList(TeacherClassStudentEnrollmentQueryListDto req) {
        try {
            QueryWrapper<ClassStudentEnrollment> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 班级业务唯一标识精确查询
                if (StringUtils.hasText(req.getClassKey())) {
                    queryWrapper.eq("class_key", req.getClassKey());
                }

                // 学生业务唯一标识精确查询
                if (StringUtils.hasText(req.getUserKey())) {
                    queryWrapper.eq("user_key", req.getUserKey());
                }

                // 加入状态精确查询
                if (req.getStatus() != null) {
                    queryWrapper.eq("status", req.getStatus());
                }
            }

            // 按加入时间倒序排列
            queryWrapper.orderByDesc("enrolled_at");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行班级学生关系查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<ClassStudentEnrollment> result = classStudentEnrollmentMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取班级学生关系列表失败", e);
            throw new RuntimeException("获取班级学生关系列表失败", e);
        }
    }

    @Override
    public Long getClassStudentEnrollmentsCount(TeacherClassStudentEnrollmentQueryListDto req) {
        try {
            QueryWrapper<ClassStudentEnrollment> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 班级业务唯一标识精确查询
                if (StringUtils.hasText(req.getClassKey())) {
                    queryWrapper.eq("class_key", req.getClassKey());
                }

                // 学生业务唯一标识精确查询
                if (StringUtils.hasText(req.getUserKey())) {
                    queryWrapper.eq("user_key", req.getUserKey());
                }

                // 加入状态精确查询
                if (req.getStatus() != null) {
                    queryWrapper.eq("status", req.getStatus());
                }
            }

            log.info("执行班级学生关系计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = classStudentEnrollmentMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取班级学生关系总数失败", e);
            throw new RuntimeException("获取班级学生关系总数失败", e);
        }
    }

    @Override
    public boolean addClassStudentEnrollment(TeacherClassStudentEnrollmentAddDto req) {
        try {
            // 创建班级学生关系对象
            ClassStudentEnrollment classStudentEnrollment = new ClassStudentEnrollment();
            classStudentEnrollment.setClassKey(req.getClassKey());
            classStudentEnrollment.setUserKey(req.getUserKey());
            classStudentEnrollment.setStatus(0);
            classStudentEnrollment.setEnrolledAt(LocalDateTime.now());

            // 插入数据库
            int result = classStudentEnrollmentMapper.insert(classStudentEnrollment);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加班级学生关系失败", e);
        }
    }

    @Override
    public boolean updateClassStudentEnrollment(TeacherClassStudentEnrollmentUpdateDto req) {
        try {
            // 先查询班级学生关系是否存在
            ClassStudentEnrollment classStudentEnrollment = classStudentEnrollmentMapper.selectById(req.getId());
            if (classStudentEnrollment == null) {
                throw new RuntimeException("班级学生关系不存在");
            }

            // 构建更新条件
            UpdateWrapper<ClassStudentEnrollment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空字段
            if (StringUtils.hasText(req.getClassKey())) {
                updateWrapper.set("class_key", req.getClassKey());
            }
            if (StringUtils.hasText(req.getUserKey())) {
                updateWrapper.set("user_key", req.getUserKey());
            }
            if (req.getStatus() != null) {
                updateWrapper.set("status", req.getStatus());
            }

            // 执行更新
            int result = classStudentEnrollmentMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新班级学生关系失败", e);
        }
    }

    @Override
    public boolean deleteClassStudentEnrollmentById(Long id) {
        try {
            // 先查询要删除的班级学生关系
            ClassStudentEnrollment classStudentEnrollment = classStudentEnrollmentMapper.selectById(id);
            if (classStudentEnrollment == null) {
                throw new RuntimeException("班级学生关系不存在");
            }

            // 删除目标班级学生关系
            int result = classStudentEnrollmentMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除班级学生关系失败", e);
        }
    }

    @Override
    public ClassStudentEnrollment getClassStudentEnrollmentById(Long id) {
        try {
            return classStudentEnrollmentMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取班级学生关系详情失败", e);
        }
    }
}