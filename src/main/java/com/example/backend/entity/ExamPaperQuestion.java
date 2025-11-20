package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 试卷题目关联表实体类
 * 对应数据库表：exam_paper_question
 */
@Data
@TableName("exam_paper_question")
public class ExamPaperQuestion {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷ID（关联exam_paper表）
     */
    @TableField("paper_id")
    private Long paperId;

    /**
     * 题目KEY（关联question表）
     */
    @TableField("item_key")
    private String itemKey;

    /**
     * 题目在试卷中的顺序（越小越靠前）
     */
    @TableField("sort_num")
    private Integer sortNum;

    /**
     * 该题目在本试卷中的实际分值（可能不同于默认分值）
     */
    @TableField("actual_score")
    private Double actualScore;
}