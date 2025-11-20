package com.example.backend.service.student.test_center_do;

import com.example.backend.entity.ExamPaperQuestion;
import java.util.List;

public interface ExamPaperQuestionService {
    /**
     * 根据试卷ID查询所有题目
     * @param paperId 试卷ID
     * @return 题目列表
     */
    List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId);
}