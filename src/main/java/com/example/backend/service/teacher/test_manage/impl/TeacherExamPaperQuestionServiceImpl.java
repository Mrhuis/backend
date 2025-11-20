package com.example.backend.service.teacher.test_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.mapper.ExamPaperQuestionMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 教师端试卷题目管理服务实现类
 */
@Service
public class TeacherExamPaperQuestionServiceImpl implements TeacherExamPaperQuestionService {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperQuestionServiceImpl.class);

    @Autowired
    private ExamPaperQuestionMapper examPaperQuestionMapper;

    @Override
    public List<ExamPaperQuestion> getExamPaperQuestionList(TeacherExamPaperQuestionQueryListDto req) {
        try {
            QueryWrapper<ExamPaperQuestion> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 试卷ID精确查询
                if (req.getPaperId() != null && req.getPaperId() > 0) {
                    queryWrapper.eq("paper_id", req.getPaperId());
                }

                // 题目KEY精确查询
                if (StringUtils.hasText(req.getItemKey())) {
                    queryWrapper.eq("item_key", req.getItemKey());
                }
            }

            // 按顺序排列
            queryWrapper.orderByAsc("sort_num");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行试卷题目查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<ExamPaperQuestion> result = examPaperQuestionMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取试卷题目列表失败", e);
            throw new RuntimeException("获取试卷题目列表失败", e);
        }
    }

    @Override
    public Long getExamPaperQuestionsCount(TeacherExamPaperQuestionQueryListDto req) {
        try {
            QueryWrapper<ExamPaperQuestion> queryWrapper = new QueryWrapper<>();

            if (req != null) {
                // 试卷ID精确查询
                if (req.getPaperId() != null && req.getPaperId() > 0) {
                    queryWrapper.eq("paper_id", req.getPaperId());
                }

                // 题目KEY精确查询
                if (StringUtils.hasText(req.getItemKey())) {
                    queryWrapper.eq("item_key", req.getItemKey());
                }
            }

            log.info("执行试卷题目计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = examPaperQuestionMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取试卷题目总数失败", e);
            throw new RuntimeException("获取试卷题目总数失败", e);
        }
    }

    @Override
    public boolean addExamPaperQuestion(TeacherExamPaperQuestionAddDto req) {
        try {
            // 创建试卷题目对象
            ExamPaperQuestion examPaperQuestion = new ExamPaperQuestion();
            examPaperQuestion.setPaperId(req.getPaperId());
            examPaperQuestion.setItemKey(req.getItemKey());
            examPaperQuestion.setSortNum(req.getSortNum());
            examPaperQuestion.setActualScore(req.getActualScore());

            // 插入数据库
            int result = examPaperQuestionMapper.insert(examPaperQuestion);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加试卷题目失败", e);
        }
    }

    @Override
    public boolean updateExamPaperQuestion(TeacherExamPaperQuestionUpdateDto req) {
        try {
            // 先查询试卷题目是否存在
            ExamPaperQuestion examPaperQuestion = examPaperQuestionMapper.selectById(req.getId());
            if (examPaperQuestion == null) {
                throw new RuntimeException("试卷题目关联不存在");
            }

            // 构建更新条件
            UpdateWrapper<ExamPaperQuestion> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空字段
            if (req.getPaperId() != null) {
                updateWrapper.set("paper_id", req.getPaperId());
            }
            if (req.getItemKey() != null) {
                updateWrapper.set("item_key", req.getItemKey());
            }
            if (req.getSortNum() != null) {
                updateWrapper.set("sort_num", req.getSortNum());
            }
            if (req.getActualScore() != null) {
                updateWrapper.set("actual_score", req.getActualScore());
            }

            // 执行更新
            int result = examPaperQuestionMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新试卷题目失败", e);
        }
    }

    @Override
    public boolean deleteExamPaperQuestionById(Long id) {
        try {
            // 先查询要删除的试卷题目
            ExamPaperQuestion examPaperQuestion = examPaperQuestionMapper.selectById(id);
            if (examPaperQuestion == null) {
                throw new RuntimeException("试卷题目关联不存在");
            }

            // 删除目标试卷题目
            int result = examPaperQuestionMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除试卷题目失败", e);
        }
    }

    @Override
    public ExamPaperQuestion getExamPaperQuestionById(Long id) {
        try {
            return examPaperQuestionMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取试卷题目详情失败", e);
        }
    }
}