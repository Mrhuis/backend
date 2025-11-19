package com.example.backend.controller.teacher.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教师端习题更新DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherLAItemUpdateDto {

        /**
     * 数据库自增ID
     */
//    @TableId(type = IdType.AUTO)  // 自增主键（注：DTO一般无需@TableId，若需与实体类对齐可保留）
    private Long id;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 习题业务唯一标识（如Q_stack_001）
     */
    @JsonAlias({"item_key", "itemKey"})  // 原有注解，保留
    private String itemKey;

//    /**
//     * 所属插件（关联plugins.plugin_key）
//     */
//    @JsonAlias({"plugin_key", "pluginKey"})  // 含大写K，补充注解
//    private String pluginKey;

    /**
     * 习题类型：选择题(choice)、判断题(judge)、填空题(blank)、代码题(code)、问答(qa)、画图题(drawing)
     */
//    private String type;  // 全小写，无大写字母，无需加@JsonAlias
    @JsonAlias({"form_key", "formKey"})
    private String formKey;


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

//    /**
//     * 上传者标识（关联users.user_key）
//     */
//    @JsonAlias({"uploaded_by", "uploadedBy"})  // 含大写U/B，补充注解
//    private String uploadedBy;

//    /**
//     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
//     */
//    private String status;  // 全小写，无大写字母，无需加@JsonAlias

//    /**
//     * 创建时间，默认当前时间
//     */
//    @JsonAlias({"created_at", "createdAt"})  // 含大写C/A，补充注解
//    private LocalDateTime createdAt;

    @JsonAlias({"chapter_key", "chapterKey"})
    // 存储所属章节
    private List<String> chapter_key;

    @JsonAlias({"knowledge_key", "knowledgeKey"})
    // 存储对应知识点数组
    private List<String> knowledge_key;

    @JsonAlias({"tag_id", "tagId"})
    // 存储对应标签id数组
    private List<Long> tagId;
}