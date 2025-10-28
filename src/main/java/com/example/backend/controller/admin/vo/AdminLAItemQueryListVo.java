package com.example.backend.controller.admin.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * ClassName: AdminLAItemQueryListVo
 * Package: com.example.backend.controller.admin.vo
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/10 15:09
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLAItemQueryListVo {

    /**
     * 数据库自增ID
     */
    private Long id;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 习题业务唯一标识（如Q_stack_001）
     */
    private String itemKey;

    /**
     * 所属插件（关联plugins.plugin_key）
     */
    private String pluginKey;

    /**
     * 习题类型：选择题(choice)、判断题(judge)、填空题(blank)、代码题(code)、问答(qa)、画图题(drawing)
     */
    private String formKey;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 难度（1-5），默认1
     */
    private Integer difficulty;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 题干内容
     */
    private String content;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 选择题选项（JSON格式）
     */
    private String options;  // 全小写，无大写字母，无需加@JsonAlias（注：可改为JSONObject类型）

    /**
     * 参考答案
     */
    private String answer;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 解析
     */
    private String solution;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 上传者标识（关联users.user_key）
     */
    private String uploadedBy;

    /**
     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
     */
    private String status;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 创建时间，默认当前时间
     */
    private LocalDateTime createdAt;

    // 存储所属章节
    private HashMap<String,String> chapterKeyName;

    // 存储对应知识点数组
    private HashMap<String,String> knowledgeKeyName;

    // 存储对应标签id数组
    private HashMap<String,String> tagIdName;
}
