package com.example.backend.service.student.test_center_result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.PageResult;
import com.example.backend.controller.student.vo.StudentCompletedPaperVO;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.StudentAnswer;
import com.example.backend.mapper.ExamPaperMapper;
import com.example.backend.mapper.StudentAnswerMapper;
import com.example.backend.service.student.test_center_result.StudentAnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 学生测试中心结果-学生答案服务实现类
 */
@Service("testCenterResultStudentAnswerService")
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer> implements StudentAnswerService {

    private static final Logger log = LoggerFactory.getLogger(StudentAnswerServiceImpl.class);

    private final StudentAnswerMapper studentAnswerMapper;
    private final ExamPaperMapper examPaperMapper;

    public StudentAnswerServiceImpl(StudentAnswerMapper studentAnswerMapper,
                                    ExamPaperMapper examPaperMapper) {
        this.studentAnswerMapper = studentAnswerMapper;
        this.examPaperMapper = examPaperMapper;
    }

    @Override
    public StudentAnswer getAnswerByUserKeyPaperIdItemKey(String userKey, Long paperId, String itemKey) {
        try {
            log.info("开始查询学生答案，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey);
            
            QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_key", userKey)
                    .eq("paper_id", paperId)
                    .eq("item_key", itemKey);
            
            StudentAnswer result = studentAnswerMapper.selectOne(queryWrapper);
            log.info("查询学生答案{}成功", result != null ? "成功" : "未找到");
            return result;
        } catch (Exception e) {
            log.error("查询学生答案失败，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey, e);
            throw new RuntimeException("查询学生答案失败", e);
        }
    }
    
    @Override
    public boolean isPaperGraded(String userKey, Long paperId) {
        try {
            log.info("开始检查试卷是否已评分完成，userKey={}, paperId={}", userKey, paperId);
            
            // 查询符合条件的所有答题记录
            QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_key", userKey)
                    .eq("paper_id", paperId);
            
            List<StudentAnswer> answers = studentAnswerMapper.selectList(queryWrapper);
            
            // 检查是否有任何答题记录的score为null
            for (StudentAnswer answer : answers) {
                if (answer.getScore() == null) {
                    log.info("试卷评分未完成，存在score为null的答题记录");
                    return false;
                }
            }
            
            log.info("试卷评分已完成");
            return true;
        } catch (Exception e) {
            log.error("检查试卷评分状态失败，userKey={}, paperId={}", userKey, paperId, e);
            throw new RuntimeException("检查试卷评分状态失败", e);
        }
    }
    
    @Override
    public List<StudentAnswer> getCompletedPapersByUserKey(String userKey) {
        try {
            log.info("开始查询用户已完成的试卷列表，userKey={}", userKey);
            
            QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_key", userKey)
                    .eq("is_complete", 1);
            
            List<StudentAnswer> result = studentAnswerMapper.selectList(queryWrapper);
            log.info("查询到{}个已完成的试卷", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询用户已完成的试卷列表失败，userKey={}", userKey, e);
            throw new RuntimeException("查询用户已完成的试卷列表失败", e);
        }
    }

    @Override
    public PageResult<StudentCompletedPaperVO> getCompletedPapersPage(String userKey, int page, int size) {
        if (userKey == null || userKey.trim().isEmpty()) {
            throw new IllegalArgumentException("userKey不能为空");
        }
        int pageIndex = Math.max(page, 1);
        int pageSize = Math.max(size, 1);

        QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey)
                .eq("is_complete", 1)
                .isNotNull("paper_id");

        List<StudentAnswer> answers = studentAnswerMapper.selectList(queryWrapper);
        List<StudentCompletedPaperVO> summaries = buildCompletedPaperSummaries(answers);

        summaries.sort(Comparator.comparing(
                StudentCompletedPaperVO::getCompleteTime,
                Comparator.nullsLast(LocalDateTime::compareTo)
        ).reversed());

        long total = summaries.size();
        int fromIndex = Math.min((pageIndex - 1) * pageSize, summaries.size());
        int toIndex = Math.min(fromIndex + pageSize, summaries.size());
        List<StudentCompletedPaperVO> pageRecords = summaries.subList(fromIndex, toIndex);

        return new PageResult<>(pageRecords, total, pageIndex, pageSize);
    }

    @Override
    public Optional<StudentCompletedPaperVO> getCompletedPaperSummary(String userKey, Long paperId) {
        if (userKey == null || paperId == null) {
            return Optional.empty();
        }
        QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey)
                .eq("paper_id", paperId)
                .eq("is_complete", 1);

        List<StudentAnswer> answers = studentAnswerMapper.selectList(queryWrapper);
        if (answers.isEmpty()) {
            return Optional.empty();
        }

        List<StudentCompletedPaperVO> summaries = buildCompletedPaperSummaries(answers);
        if (summaries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(summaries.get(0));
    }

    private List<StudentCompletedPaperVO> buildCompletedPaperSummaries(List<StudentAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<StudentAnswer>> groupedByPaper = answers.stream()
                .filter(answer -> answer.getPaperId() != null)
                .collect(Collectors.groupingBy(StudentAnswer::getPaperId));

        if (groupedByPaper.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> paperIds = new ArrayList<>(groupedByPaper.keySet());
        Map<Long, ExamPaper> examPaperMap = examPaperMapper.selectBatchIds(paperIds).stream()
                .collect(Collectors.toMap(ExamPaper::getId, examPaper -> examPaper));

        List<StudentCompletedPaperVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<StudentAnswer>> entry : groupedByPaper.entrySet()) {
            Long paperId = entry.getKey();
            List<StudentAnswer> paperAnswers = entry.getValue();
            ExamPaper examPaper = examPaperMap.get(paperId);

            StudentCompletedPaperVO vo = new StudentCompletedPaperVO();
            vo.setPaperId(paperId);
            vo.setQuestionCount(paperAnswers.size());
            vo.setCompleteTime(paperAnswers.stream()
                    .map(StudentAnswer::getCompleteTime)
                    .filter(ct -> ct != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(null));

            boolean graded = paperAnswers.stream().allMatch(answer -> answer.getScore() != null);
            vo.setGraded(graded);

            BigDecimal totalScore = paperAnswers.stream()
                    .map(StudentAnswer::getScore)
                    .filter(score -> score != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setObtainedScore(totalScore);

            if (examPaper != null) {
                vo.setPaperName(examPaper.getPaperName());
                vo.setSubject(examPaper.getSubject());
                vo.setDifficulty(examPaper.getDifficulty());
                vo.setTotalScore(examPaper.getTotalScore());
                vo.setTimeLimit(examPaper.getTimeLimit());
            }

            result.add(vo);
        }

        return result;
    }
}