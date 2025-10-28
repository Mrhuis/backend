package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 习题资源表实体类
 * 对应数据库表：items
 */
@Data
@TableName("items")  // 指定对应的数据表名
public class Item {

    /**
     * 数据库自增ID
     */
    @TableId(type = IdType.AUTO)  // 自增主键
    private Long id;

    /**
     * 习题业务唯一标识（如Q_stack_001）
     */
    @TableField(value = "item_key", exist = true)  // 映射数据库字段item_key
    private String itemKey;

    /**
     * 所属插件（关联plugins.plugin_key）
     */
    @TableField("plugin_key")
    private String pluginKey;

    /**
     * 习题类型：选择题(choice)、判断题(judge)、填空题(blank)、代码题(code)、问答(qa)、画图题(drawing)
     */
    @TableField("form_key")
    private String formKey;

    /**
     * 难度（1-5），默认1
     */
    @TableField(value = "difficulty", fill = FieldFill.INSERT)  // 插入时默认填充
    private Integer difficulty = 1;

    /**
     * 题干内容
     */
    @TableField("content")
    private String content;

    /**
     * 选择题选项（JSON格式）
     */
    @TableField("options")
    private String options;  // 若使用JSON解析框架，可改为JSONObject类型

    /**
     * 参考答案
     */
    @TableField("answer")
    private String answer;

    /**
     * 解析
     */
    @TableField("solution")
    private String solution;

    /**
     * 上传者标识（关联users.user_key）
     */
    @TableField("uploaded_by")
    private String uploadedBy;

    /**
     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
     */
    @TableField(value = "status")  // 插入时默认填充
    private String status ;

    /**
     * 创建时间，默认当前时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)  // 插入时自动填充
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  // 序列化格式
    private LocalDateTime createdAt;


}
