package com.example.backend.service.teacher.test_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.ExamPaperDistribution;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.mapper.ExamPaperMapper;
import com.example.backend.mapper.ExamPaperDistributionMapper;
import com.example.backend.mapper.ExamPaperQuestionMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师端试卷信息管理服务实现类
 */
@Service
public class TeacherExamPaperInfoServiceImpl implements TeacherExamPaperInfoService {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperInfoServiceImpl.class);

    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Autowired
    private ExamPaperDistributionMapper examPaperDistributionMapper;

    @Autowired
    private ExamPaperQuestionMapper examPaperQuestionMapper;

    @Override
    public List<ExamPaper> getExamPaperList(TeacherExamPaperQueryListDto req) {
        try {
            QueryWrapper<ExamPaper> queryWrapper = new QueryWrapper<>();

            // 如果只查询已发布的试卷，先获取已发布的试卷ID列表
            if (req != null && Boolean.TRUE.equals(req.getOnlyDistributed())) {
                QueryWrapper<ExamPaperDistribution> distributionQuery = new QueryWrapper<>();
                // 如果指定了创建者，只查询该创建者的试卷发布记录
                if (StringUtils.hasText(req.getCreatorKey())) {
                    QueryWrapper<ExamPaper> paperQueryWrapper = new QueryWrapper<>();
                    paperQueryWrapper.eq("creator_key", req.getCreatorKey());
                    List<ExamPaper> papers = examPaperMapper.selectList(paperQueryWrapper);
                    List<Long> paperIds = papers.stream().map(ExamPaper::getId).collect(Collectors.toList());
                    if (paperIds.isEmpty()) {
                        // 如果没有找到该创建者的试卷，直接返回空列表
                        log.info("创建者{}没有试卷，返回空列表", req.getCreatorKey());
                        return new ArrayList<>();
                    }
                    distributionQuery.in("paper_id", paperIds);
                }
                List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectList(distributionQuery);
                List<Long> distributedPaperIds = distributions.stream()
                        .map(ExamPaperDistribution::getPaperId)
                        .distinct()
                        .collect(Collectors.toList());
                
                if (distributedPaperIds.isEmpty()) {
                    // 如果没有已发布的试卷，直接返回空列表
                    log.info("没有已发布的试卷，返回空列表");
                    return new ArrayList<>();
                }
                // 只查询已发布的试卷ID
                queryWrapper.in("id", distributedPaperIds);
            }

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

                // 创建者标识精确查询
                if (StringUtils.hasText(req.getCreatorKey())) {
                    queryWrapper.eq("creator_key", req.getCreatorKey());
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

            // 如果只查询已发布的试卷，先获取已发布的试卷ID列表
            if (req != null && Boolean.TRUE.equals(req.getOnlyDistributed())) {
                QueryWrapper<ExamPaperDistribution> distributionQuery = new QueryWrapper<>();
                // 如果指定了创建者，只查询该创建者的试卷发布记录
                if (StringUtils.hasText(req.getCreatorKey())) {
                    QueryWrapper<ExamPaper> paperQueryWrapper = new QueryWrapper<>();
                    paperQueryWrapper.eq("creator_key", req.getCreatorKey());
                    List<ExamPaper> papers = examPaperMapper.selectList(paperQueryWrapper);
                    List<Long> paperIds = papers.stream().map(ExamPaper::getId).collect(Collectors.toList());
                    if (paperIds.isEmpty()) {
                        // 如果没有找到该创建者的试卷，直接返回0
                        log.info("创建者{}没有试卷，返回0", req.getCreatorKey());
                        return 0L;
                    }
                    distributionQuery.in("paper_id", paperIds);
                }
                List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectList(distributionQuery);
                List<Long> distributedPaperIds = distributions.stream()
                        .map(ExamPaperDistribution::getPaperId)
                        .distinct()
                        .collect(Collectors.toList());
                
                if (distributedPaperIds.isEmpty()) {
                    // 如果没有已发布的试卷，直接返回0
                    log.info("没有已发布的试卷，返回0");
                    return 0L;
                }
                // 只查询已发布的试卷ID
                queryWrapper.in("id", distributedPaperIds);
            }

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

                // 创建者标识精确查询
                if (StringUtils.hasText(req.getCreatorKey())) {
                    queryWrapper.eq("creator_key", req.getCreatorKey());
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
            examPaper.setCreatorKey(req.getCreatorKey());
            examPaper.setCreateTime(LocalDateTime.now());
            examPaper.setUpdateTime(LocalDateTime.now());
            examPaper.setIsEnabled(0); // 默认启用

            // 插入数据库
            int result = examPaperMapper.insert(examPaper);
            log.info("插入试卷结果: {}, 插入后的ID: {}", result, examPaper.getId());
            return result > 0;
        } catch (Exception e) {
            // 打印详细异常信息，便于排查如唯一约束、非空约束等数据库错误
            log.error("添加试卷失败, 请求参数: {}", req, e);
            throw new RuntimeException("添加试卷失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExamPaper(TeacherExamPaperUpdateDto req) {
        try {
            // 先查询试卷是否存在
            ExamPaper examPaper = examPaperMapper.selectById(req.getId());
            if (examPaper == null) {
                throw new RuntimeException("试卷不存在");
            }

            // 检查是否从启用变为禁用
            Integer oldIsEnabled = examPaper.getIsEnabled();
            Integer newIsEnabled = req.getIsEnabled();
            boolean isDisabling = (oldIsEnabled != null && oldIsEnabled == 1) 
                    && (newIsEnabled != null && newIsEnabled == 0);

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
            
            // 如果试卷被禁用，自动回收该试卷的所有发布记录
            if (result > 0 && isDisabling) {
                UpdateWrapper<ExamPaperDistribution> distributionUpdateWrapper = new UpdateWrapper<>();
                distributionUpdateWrapper.eq("paper_id", req.getId());
                distributionUpdateWrapper.set("is_recycled", 1);
                int recycledCount = examPaperDistributionMapper.update(null, distributionUpdateWrapper);
                log.info("试卷ID为{}被禁用，自动回收了{}条发布记录", req.getId(), recycledCount);
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("更新试卷失败", e);
            throw new RuntimeException("更新试卷失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExamPaperById(Long id) {
        try {
            // 先查询要删除的试卷
            ExamPaper examPaper = examPaperMapper.selectById(id);
            if (examPaper == null) {
                throw new RuntimeException("试卷不存在");
            }

            // 检查试卷是否被发布过
            QueryWrapper<ExamPaperDistribution> distributionQuery = new QueryWrapper<>();
            distributionQuery.eq("paper_id", id);
            List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectList(distributionQuery);
            
            if (distributions != null && !distributions.isEmpty()) {
                throw new RuntimeException("该试卷已被发布过，无法删除。请先删除相关的发布记录");
            }

            // 删除该试卷的所有题目记录
            QueryWrapper<ExamPaperQuestion> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq("paper_id", id);
            int deletedQuestions = examPaperQuestionMapper.delete(questionQueryWrapper);
            log.info("删除试卷ID为{}的题目记录，共删除{}条", id, deletedQuestions);

            // 删除目标试卷
            int result = examPaperMapper.deleteById(id);
            return result > 0;
        } catch (RuntimeException e) {
            // 重新抛出业务异常，保持原有错误信息
            throw e;
        } catch (Exception e) {
            log.error("删除试卷失败", e);
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