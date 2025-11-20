package com.example.backend.service.student.test_center_result.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.mapper.ExamPaperQuestionMapper;
import com.example.backend.service.student.test_center_result.ExamPaperQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生测试中心结果-试卷题目服务实现类
 */
@Service("testCenterResultExamPaperQuestionService")
public class ExamPaperQuestionServiceImpl extends ServiceImpl<ExamPaperQuestionMapper, ExamPaperQuestion> implements ExamPaperQuestionService {

    private static final Logger log = LoggerFactory.getLogger(ExamPaperQuestionServiceImpl.class);

    private final ExamPaperQuestionMapper examPaperQuestionMapper;

    public ExamPaperQuestionServiceImpl(ExamPaperQuestionMapper examPaperQuestionMapper) {
        this.examPaperQuestionMapper = examPaperQuestionMapper;
    }

    @Override
    public List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId) {
        try {
            log.info("开始查询试卷题目，paperId={}", paperId);
            List<ExamPaperQuestion> questions = examPaperQuestionMapper.selectByPaperId(paperId);
            log.info("查询到{}个题目", questions.size());
            return questions;
        } catch (Exception e) {
            log.error("查询试卷题目失败，paperId={}", paperId, e);
            throw new RuntimeException("查询试卷题目失败", e);
        }
    }
}