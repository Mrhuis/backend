package com.example.backend.service.student.test_center_result;

import com.example.backend.entity.ExamPaperQuestion;
import java.util.List;

/**
 * 学生测试中心结果-试卷题目服务接口
 */
public interface ExamPaperQuestionService {
    /**
     * 根据试卷ID查询所有题目
     * @param paperId 试卷ID
     * @return 题目列表
     */
    List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId);
}