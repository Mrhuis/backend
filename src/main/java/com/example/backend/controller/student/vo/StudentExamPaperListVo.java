package com.example.backend.controller.student.vo;

import com.example.backend.entity.ExamPaper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
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

    /**
     * 是否已回收（0-未回收，1-已回收，回收后学生无法再提交）
     */
    private Integer isRecycled;
}