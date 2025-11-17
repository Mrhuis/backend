package com.example.backend.service.teacher.test_manage;

import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaperQuestion;

import java.util.List;

/**
 * 教师端试卷题目管理服务接口
 */
public interface TeacherExamPaperQuestionService {
    /**
     * 获取试卷题目列表
     *
     * @param req 查询条件
     * @return 题目列表
     */
    List<ExamPaperQuestion> getExamPaperQuestionList(TeacherExamPaperQuestionQueryListDto req);

    /**
     * 获取试卷题目总数
     *
     * @param req 查询条件
     * @return 题目总数
     */
    Long getExamPaperQuestionsCount(TeacherExamPaperQuestionQueryListDto req);

    /**
     * 添加试卷题目
     *
     * @param req 题目信息
     * @return 是否添加成功
     */
    boolean addExamPaperQuestion(TeacherExamPaperQuestionAddDto req);

    /**
     * 更新试卷题目
     *
     * @param req 题目信息
     * @return 是否更新成功
     */
    boolean updateExamPaperQuestion(TeacherExamPaperQuestionUpdateDto req);

    /**
     * 删除试卷题目
     *
     * @param id 关联ID
     * @return 是否删除成功
     */
    boolean deleteExamPaperQuestionById(Long id);

    /**
     * 根据ID获取试卷题目详情
     *
     * @param id 关联ID
     * @return 试卷题目详情
     */
    ExamPaperQuestion getExamPaperQuestionById(Long id);
}