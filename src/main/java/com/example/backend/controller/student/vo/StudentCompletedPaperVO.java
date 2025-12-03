package com.example.backend.controller.student.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生已完成试卷汇总视图对象
 */
@Data
public class StudentCompletedPaperVO {
    private Long paperId;
    private String paperName;
    private String subject;
    private Integer difficulty;
    private Double totalScore;
    private Integer timeLimit;
    private LocalDateTime completeTime;
    private Integer questionCount;
    private BigDecimal obtainedScore;
    private Boolean graded;
    /**
     * 是否可以查看详情（如果存在is_complete=0的记录，则不能查看详情）
     */
    private Boolean canViewDetail;
}

