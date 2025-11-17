package com.example.backend.service.teacher.test_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaper;
import com.example.backend.mapper.ExamPaperMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师端试卷信息管理服务实现类
 */
@Service
public class TeacherExamPaperInfoServiceImpl implements TeacherExamPaperInfoService {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperInfoServiceImpl.class);

    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Override
    public List<ExamPaper> getExamPaperList(TeacherExamPaperQueryListDto req) {
        try {
            QueryWrapper<ExamPaper> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 试卷名称模糊查询
                if (StringUtils.hasText(req.getPaperName())) {
                    queryWrapper.like("paper_name", req.getPaperName());
                }

                // 科目精确查询
                if (StringUtils.hasText(req.getSubject())) {
                    queryWrapper.eq("subject", req.getSubject());
                }

                // 难度等级精确查询
                if (req.getDifficulty() != null && req.getDifficulty() > 0) {
                    queryWrapper.eq("difficulty", req.getDifficulty());
                }

                // 启用状态精确查询
                if (req.getIsEnabled() != null) {
                    queryWrapper.eq("is_enabled", req.getIsEnabled());
                }
            }

            // 按创建时间倒序排列
            queryWrapper.orderByDesc("create_time");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行试卷查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<ExamPaper> result = examPaperMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取试卷列表失败", e);
            throw new RuntimeException("获取试卷列表失败", e);
        }
    }

    @Override
    public Long getExamPapersCount(TeacherExamPaperQueryListDto req) {
        try {
            QueryWrapper<ExamPaper> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 试卷名称模糊查询
                if (StringUtils.hasText(req.getPaperName())) {
                    queryWrapper.like("paper_name", req.getPaperName());
                }

                // 科目精确查询
                if (StringUtils.hasText(req.getSubject())) {
                    queryWrapper.eq("subject", req.getSubject());
                }

                // 难度等级精确查询
                if (req.getDifficulty() != null && req.getDifficulty() > 0) {
                    queryWrapper.eq("difficulty", req.getDifficulty());
                }

                // 启用状态精确查询
                if (req.getIsEnabled() != null) {
                    queryWrapper.eq("is_enabled", req.getIsEnabled());
                }
            }

            log.info("执行试卷计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = examPaperMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取试卷总数失败", e);
            throw new RuntimeException("获取试卷总数失败", e);
        }
    }

    @Override
    public boolean addExamPaper(TeacherExamPaperAddDto req) {
        try {
            // 创建试卷对象
            ExamPaper examPaper = new ExamPaper();
            examPaper.setPaperName(req.getPaperName());
            examPaper.setSubject(req.getSubject());
            examPaper.setDifficulty(req.getDifficulty());
            examPaper.setTotalScore(req.getTotalScore());
            examPaper.setTimeLimit(req.getTimeLimit());
            examPaper.setCreateUserId(req.getCreateUserId());
            examPaper.setCreateTime(LocalDateTime.now());
            examPaper.setUpdateTime(LocalDateTime.now());
            examPaper.setIsEnabled(0); // 默认启用

            // 插入数据库
            int result = examPaperMapper.insert(examPaper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加试卷失败", e);
        }
    }

    @Override
    public boolean updateExamPaper(TeacherExamPaperUpdateDto req) {
        try {
            // 先查询试卷是否存在
            ExamPaper examPaper = examPaperMapper.selectById(req.getId());
            if (examPaper == null) {
                throw new RuntimeException("试卷不存在");
            }

            // 构建更新条件
            UpdateWrapper<ExamPaper> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空字段
            if (StringUtils.hasText(req.getPaperName())) {
                updateWrapper.set("paper_name", req.getPaperName());
            }
            if (StringUtils.hasText(req.getSubject())) {
                updateWrapper.set("subject", req.getSubject());
            }
            if (req.getDifficulty() != null) {
                updateWrapper.set("difficulty", req.getDifficulty());
            }
            if (req.getTotalScore() != null) {
                updateWrapper.set("total_score", req.getTotalScore());
            }
            if (req.getTimeLimit() != null) {
                updateWrapper.set("time_limit", req.getTimeLimit());
            }
            if (req.getIsEnabled() != null) {
                updateWrapper.set("is_enabled", req.getIsEnabled());
            }
            
            updateWrapper.set("update_time", LocalDateTime.now());

            // 执行更新
            int result = examPaperMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新试卷失败", e);
        }
    }

    @Override
    public boolean deleteExamPaperById(Long id) {
        try {
            // 先查询要删除的试卷
            ExamPaper examPaper = examPaperMapper.selectById(id);
            if (examPaper == null) {
                throw new RuntimeException("试卷不存在");
            }

            // 删除目标试卷
            int result = examPaperMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除试卷失败", e);
        }
    }

    @Override
    public ExamPaper getExamPaperById(Long id) {
        try {
            return examPaperMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取试卷详情失败", e);
        }
    }
}