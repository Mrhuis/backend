package com.example.backend.service.teacher.test_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.ExamPaperDistribution;
import com.example.backend.mapper.ExamPaperDistributionMapper;
import com.example.backend.mapper.ExamPaperMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师端试卷下发管理服务实现类
 */
@Service
public class TeacherExamPaperDistributionServiceImpl implements TeacherExamPaperDistributionService {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperDistributionServiceImpl.class);

    @Autowired
    private ExamPaperDistributionMapper examPaperDistributionMapper;

    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Override
    public List<ExamPaperDistribution> getExamPaperDistributionList(TeacherExamPaperDistributionQueryListDto req) {
        try {
            if (req == null) {
                req = new TeacherExamPaperDistributionQueryListDto();
            }
            QueryWrapper<ExamPaperDistribution> queryWrapper = new QueryWrapper<>();

            // 如果指定了创建者标识，需要先查询该创建者的试卷ID列表
            if (StringUtils.hasText(req.getCreatorKey())) {
                QueryWrapper<ExamPaper> paperQueryWrapper = new QueryWrapper<>();
                paperQueryWrapper.eq("creator_key", req.getCreatorKey());
                List<ExamPaper> papers = examPaperMapper.selectList(paperQueryWrapper);
                List<Long> paperIds = papers.stream().map(ExamPaper::getId).collect(Collectors.toList());
                
                if (paperIds.isEmpty()) {
                    // 如果没有找到该创建者的试卷，直接返回空列表
                    log.info("创建者{}没有试卷，返回空列表", req.getCreatorKey());
                    return new java.util.ArrayList<>();
                }
                
                // 只查询这些试卷的发布记录
                queryWrapper.in("paper_id", paperIds);
            }

            // 班级Key精确查询
            if (StringUtils.hasText(req.getClassKey())) {
                queryWrapper.eq("class_key", req.getClassKey());
            }

            // 试卷ID精确查询
            if (req.getPaperId() != null && req.getPaperId() > 0) {
                queryWrapper.eq("paper_id", req.getPaperId());
            }

            // 开始时间查询
            if (req.getStartTime() != null) {
                queryWrapper.ge("start_time", req.getStartTime());
            }

            // 下发人ID精确查询
            if (req.getDistributorId() != null && req.getDistributorId() > 0) {
                queryWrapper.eq("distributor_id", req.getDistributorId());
            }

            // 回收状态查询
            if (req.getIsRecycled() != null) {
                queryWrapper.eq("is_recycled", req.getIsRecycled());
            }

            // 按下发时间倒序排列
            queryWrapper.orderByDesc("distribute_time");

            // 分页查询
            if (req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行试卷下发查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<ExamPaperDistribution> result = examPaperDistributionMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取试卷下发列表失败", e);
            throw new RuntimeException("获取试卷下发列表失败", e);
        }
    }

    @Override
    public Long getExamPaperDistributionsCount(TeacherExamPaperDistributionQueryListDto req) {
        try {
            if (req == null) {
                req = new TeacherExamPaperDistributionQueryListDto();
            }
            QueryWrapper<ExamPaperDistribution> queryWrapper = new QueryWrapper<>();

            // 如果指定了创建者标识，需要先查询该创建者的试卷ID列表
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
                
                // 只查询这些试卷的发布记录
                queryWrapper.in("paper_id", paperIds);
            }

            // 班级Key精确查询
            if (StringUtils.hasText(req.getClassKey())) {
                queryWrapper.eq("class_key", req.getClassKey());
            }

            // 试卷ID精确查询
            if (req.getPaperId() != null && req.getPaperId() > 0) {
                queryWrapper.eq("paper_id", req.getPaperId());
            }

            // 开始时间查询
            if (req.getStartTime() != null) {
                queryWrapper.ge("start_time", req.getStartTime());
            }

            // 下发人ID精确查询
            if (req.getDistributorId() != null && req.getDistributorId() > 0) {
                queryWrapper.eq("distributor_id", req.getDistributorId());
            }

            // 回收状态查询
            if (req.getIsRecycled() != null) {
                queryWrapper.eq("is_recycled", req.getIsRecycled());
            }

            log.info("执行试卷下发计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = examPaperDistributionMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取试卷下发总数失败", e);
            throw new RuntimeException("获取试卷下发总数失败", e);
        }
    }

    @Override
    public boolean addExamPaperDistribution(TeacherExamPaperDistributionAddDto req) {
        try {
            // 创建试卷下发对象
            ExamPaperDistribution examPaperDistribution = new ExamPaperDistribution();
            examPaperDistribution.setClassKey(req.getClassKey());
            examPaperDistribution.setPaperId(req.getPaperId());
            examPaperDistribution.setDistributeTime(LocalDateTime.now());
            examPaperDistribution.setStartTime(req.getStartTime());
            examPaperDistribution.setDeadline(req.getDeadline());
            examPaperDistribution.setDistributorId(req.getDistributorId());
            examPaperDistribution.setIsRecycled(0); // 默认未回收
            examPaperDistribution.setRemark(req.getRemark());
            examPaperDistribution.setCreateTime(LocalDateTime.now());

            // 插入数据库
            int result = examPaperDistributionMapper.insert(examPaperDistribution);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加试卷下发失败", e);
        }
    }

    @Override
    public boolean updateExamPaperDistribution(TeacherExamPaperDistributionUpdateDto req) {
        try {
            // 先查询试卷下发是否存在
            ExamPaperDistribution examPaperDistribution = examPaperDistributionMapper.selectById(req.getId());
            if (examPaperDistribution == null) {
                throw new RuntimeException("试卷下发记录不存在");
            }

            // 构建更新条件
            UpdateWrapper<ExamPaperDistribution> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空字段
            if (StringUtils.hasText(req.getClassKey())) {
                updateWrapper.set("class_key", req.getClassKey());
            }
            if (req.getPaperId() != null) {
                updateWrapper.set("paper_id", req.getPaperId());
            }
            if (req.getStartTime() != null) {
                updateWrapper.set("start_time", req.getStartTime());
            }
            if (req.getDeadline() != null) {
                updateWrapper.set("deadline", req.getDeadline());
            }
            if (req.getDistributorId() != null) {
                updateWrapper.set("distributor_id", req.getDistributorId());
            }
            if (req.getIsRecycled() != null) {
                updateWrapper.set("is_recycled", req.getIsRecycled());
            }
            if (StringUtils.hasText(req.getRemark())) {
                updateWrapper.set("remark", req.getRemark());
            }

            // 执行更新
            int result = examPaperDistributionMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新试卷下发失败", e);
        }
    }

    @Override
    public boolean deleteExamPaperDistributionById(Long id) {
        try {
            // 先查询要删除的试卷下发
            ExamPaperDistribution examPaperDistribution = examPaperDistributionMapper.selectById(id);
            if (examPaperDistribution == null) {
                throw new RuntimeException("试卷下发记录不存在");
            }

            // 删除目标试卷下发
            int result = examPaperDistributionMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除试卷下发失败", e);
        }
    }

    @Override
    public ExamPaperDistribution getExamPaperDistributionById(Long id) {
        try {
            return examPaperDistributionMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取试卷下发详情失败", e);
        }
    }

    @Override
    public boolean recycleExamPaperDistribution(TeacherExamPaperDistributionRecycleDto req) {
        try {
            // 先查询试卷下发是否存在
            ExamPaperDistribution examPaperDistribution = examPaperDistributionMapper.selectById(req.getId());
            if (examPaperDistribution == null) {
                throw new RuntimeException("试卷下发记录不存在");
            }

            // 构建更新条件
            UpdateWrapper<ExamPaperDistribution> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 更新回收状态
            updateWrapper.set("is_recycled", req.getIsRecycled());

            // 执行更新
            int result = examPaperDistributionMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("回收试卷下发失败", e);
        }
    }
}