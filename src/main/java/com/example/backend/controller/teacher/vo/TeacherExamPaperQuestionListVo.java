package com.example.backend.controller.teacher.vo;

import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.Item;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷题目列表VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeacherExamPaperQuestionListVo extends ExamPaperQuestion {
    /**
     * 对应的习题信息
     */
    private Item item;
}

