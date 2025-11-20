package com.example.backend.service.teacher.test_grading;

import com.example.backend.controller.teacher.vo.TeacherTestGradingUserVo;
import com.example.backend.entity.Class;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.StudentAnswer;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教师端试卷批改服务接口
 */
public interface TeacherTestGradingService {
    /**
     * 根据paper_id查询对应的班级列表
     *
     * @param paperId 试卷ID
     * @return 班级列表
     */
    List<Class> getClassesByPaperId(Long paperId);

    /**
     * 根据class_key查询班级学生列表
     *
     * @param classKey 班级key
     * @return 学生列表
     */
    List<TeacherTestGradingUserVo> getStudentsByClassKey(String classKey);

    /**
     * 根据试卷ID查询试卷中的所有题目
     *
     * @param paperId 试卷ID
     * @return 题目列表
     */
    List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId);

    /**
     * 根据user_key、paper_id、item_key查询对应答案
     *
     * @param userKey  用户标识
     * @param paperId  试卷ID
     * @param itemKey  习题标识
     * @return 学生答案对象
     */
    StudentAnswer getAnswerByUserKeyPaperIdItemKey(String userKey, Long paperId, String itemKey);

    /**
     * 设置学生答案得分
     *
     * @param userKey  用户标识
     * @param paperId  试卷ID
     * @param itemKey  习题标识
     * @param score    得分
     * @return 是否设置成功
     */
    boolean setAnswerScore(String userKey, Long paperId, String itemKey, BigDecimal score);
}