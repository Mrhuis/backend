package com.example.backend.controller.teacher.vo;

import com.example.backend.entity.ExamPaper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师端试卷列表视图对象
 * 扩展ExamPaper，添加是否可以批阅的标识
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherExamPaperListVo extends ExamPaper {
    /**
     * 是否可以批阅（true-可以批阅，false-不能批阅）
     * 只有当前时间超过了试卷发布的截止时间，才能进行批阅
     */
    private Boolean canGrade;
}

