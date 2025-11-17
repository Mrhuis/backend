package com.example.backend.controller.student.vo;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClassName: StudentLCAutonomousLearnItemsListVo
 * Package: com.example.backend.controller.student.vo
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/11/16 22:30
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StudentLCAutonomousLearnItemsListVo extends BaseEntity {
    /**
     * 数据库自增ID
     */
    private Long id;

    /**
     * 习题业务唯一标识（如Q_stack_001）
     */
    @JsonAlias({"item_key", "itemKey"})
    private String itemKey;

    /**
     * 所属插件（关联plugins.plugin_key）
     */
    @JsonAlias({"plugin_key", "pluginKey"})
    private String pluginKey;

    /**
     * 习题类型：选择题(choice)、判断题(judge)、填空题(blank)、代码题(code)、问答(qa)、画图题(drawing)
     */
    @JsonAlias({"form_key", "formKey"})
    private String formKey;

    /**
     * 难度（1-5），默认1
     */
    private Integer difficulty;

    /**
     * 题干内容
     */
    private String content;

    /**
     * 选择题选项（JSON格式）
     */
    private String options;

    /**
     * 参考答案
     */
    private String answer;

    /**
     * 解析
     */
    private String solution;

    /**
     * 上传者标识（关联users.user_key）
     */
    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

    /**
     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
     */
    private String status;

    /**
     * 创建时间，默认当前时间
     */
    @JsonAlias({"created_at", "createdAt"})
    private java.time.LocalDateTime createdAt;
}