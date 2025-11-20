package com.example.backend.service.student.test_center.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.mapper.ExamPaperQuestionMapper;
import com.example.backend.service.student.test_center.ExamPaperQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamPaperQuestionServiceImpl extends ServiceImpl<ExamPaperQuestionMapper, ExamPaperQuestion> implements ExamPaperQuestionService {

    private final ExamPaperQuestionMapper examPaperQuestionMapper;

    public ExamPaperQuestionServiceImpl(ExamPaperQuestionMapper examPaperQuestionMapper) {
        this.examPaperQuestionMapper = examPaperQuestionMapper;
    }

    @Override
    public List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId) {
        return examPaperQuestionMapper.selectByPaperId(paperId);
    }
}