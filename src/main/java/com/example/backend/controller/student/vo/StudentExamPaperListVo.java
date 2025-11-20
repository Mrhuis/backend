package com.example.backend.controller.student.vo;

import com.example.backend.entity.ExamPaper;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentExamPaperListVo extends ExamPaper {
    /**
     * 试卷下发时间
     */
    private LocalDateTime distributeTime;

    /**
     * 试卷开始时间
     */
    private LocalDateTime startTime;

    /**
     * 试卷提交截止时间
     */
    private LocalDateTime deadline;

    /**
     * 下发备注
     */
    private String remark;

    /**
     * 下发人ID
     */
    private Long distributorId;
}