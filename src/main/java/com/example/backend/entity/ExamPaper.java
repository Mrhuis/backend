package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 试卷表实体类
 * 对应数据库表：exam_paper
 */
@Data
@TableName("exam_paper")
public class ExamPaper {

    /**
     * 试卷ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷名称（如：数据结构期中测试卷）
     */
    @TableField("paper_name")
    private String paperName;

    /**
     * 所属科目（默认：数据结构，可扩展为其他科目）
     */
    @TableField("subject")
    private String subject = "数据结构";

    /**
     * 难度等级（1-简单，2-中等，3-困难）
     */
    @TableField("difficulty")
    private Integer difficulty = 2;

    /**
     * 试卷总分（如：100.0）
     */
    @TableField("total_score")
    private Double totalScore;

    /**
     * 考试时长（分钟，如：90）
     */
    @TableField("time_limit")
    private Integer timeLimit;

    /**
     * 创建人ID（关联用户表）
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否启用（1-启用，0-禁用）
     */
    @TableField("is_enabled")
    private Integer isEnabled = 1;
}