package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_answer")
public class StudentAnswer {
    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户标识，关联users表的user_key字段
     */
    @TableField("user_key")
    private String userKey;

    /**
     * 试卷ID，关联exam_paper表的id字段，可为空
     */
    @TableField("paper_id")
    private Long paperId;

    /**
     * 习题标识，关联items表的item_key字段
     */
    @TableField("item_key")
    private String itemKey;

    /**
     * 用户提交的答案
     */
    @TableField("answer")
    private String answer;

    /**
     * 用户该题所得分数，可为空（如未评分）
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 记录创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 是否完成答题 0-未完成 1-已完成
     */
    @TableField("is_complete")
    private Integer isComplete;
    
    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;
}