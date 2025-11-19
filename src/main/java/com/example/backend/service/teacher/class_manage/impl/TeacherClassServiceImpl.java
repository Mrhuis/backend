package com.example.backend.service.teacher.class_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.Class;
import com.example.backend.mapper.ClassMapper;
import com.example.backend.service.teacher.class_manage.TeacherClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师端班级管理服务实现类
 */
@Service
public class TeacherClassServiceImpl implements TeacherClassService {

    private static final Logger log = LoggerFactory.getLogger(TeacherClassServiceImpl.class);

    @Autowired
    private ClassMapper classMapper;

    @Override
    public List<Class> getClassList(TeacherClassQueryListDto req) {
        try {
            QueryWrapper<Class> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 班级名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }

                // 班级业务唯一标识精确查询
                if (StringUtils.hasText(req.getClassKey())) {
                    queryWrapper.eq("class_key", req.getClassKey());
                }
            }

            // 按创建时间倒序排列
            queryWrapper.orderByDesc("created_at");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行班级查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<Class> result = classMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取班级列表失败", e);
            throw new RuntimeException("获取班级列表失败", e);
        }
    }

    @Override
    public Long getClassesCount(TeacherClassQueryListDto req) {
        try {
            QueryWrapper<Class> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 班级名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }

                // 班级业务唯一标识精确查询
                if (StringUtils.hasText(req.getClassKey())) {
                    queryWrapper.eq("class_key", req.getClassKey());
                }
            }

            log.info("执行班级计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = classMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取班级总数失败", e);
            throw new RuntimeException("获取班级总数失败", e);
        }
    }

    @Override
    public boolean addClass(TeacherClassAddDto req) {
        try {
            // 创建班级对象
            Class clazz = new Class();
            clazz.setClassKey(req.getClassKey());
            clazz.setName(req.getName());
            clazz.setInviteCode(req.getInviteCode());
            clazz.setCreatedAt(LocalDateTime.now());

            // 插入数据库
            int result = classMapper.insert(clazz);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加班级失败", e);
        }
    }

    @Override
    public boolean updateClass(TeacherClassUpdateDto req) {
        try {
            // 先查询班级是否存在
            Class clazz = classMapper.selectById(req.getId());
            if (clazz == null) {
                throw new RuntimeException("班级不存在");
            }

            // 构建更新条件
            UpdateWrapper<Class> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空字段
            if (StringUtils.hasText(req.getClassKey())) {
                updateWrapper.set("class_key", req.getClassKey());
            }
            if (StringUtils.hasText(req.getName())) {
                updateWrapper.set("name", req.getName());
            }
            if (StringUtils.hasText(req.getInviteCode())) {
                updateWrapper.set("invite_code", req.getInviteCode());
            }

            // 执行更新
            int result = classMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新班级失败", e);
        }
    }

    @Override
    public boolean deleteClassById(Long id) {
        try {
            // 先查询要删除的班级
            Class clazz = classMapper.selectById(id);
            if (clazz == null) {
                throw new RuntimeException("班级不存在");
            }

            // 删除目标班级
            int result = classMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除班级失败", e);
        }
    }

    @Override
    public Class getClassById(Long id) {
        try {
            return classMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取班级详情失败", e);
        }
    }
}