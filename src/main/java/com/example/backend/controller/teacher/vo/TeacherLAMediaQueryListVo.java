package com.example.backend.controller.teacher.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * 教师端媒体资源查询列表VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherLAMediaQueryListVo {
    private Long id;

    private String mediaKey;

    private String pluginKey;

//    private String assetType; // video, animation, image, script, document
    private String formKey;
    private String fileName;

    private String url;

    private String entryPoint;

    private String implementationScript;

    private Integer duration; // 资源时长（秒）

    private String uploadedBy;

    private String status;

    private LocalDateTime createdAt;

    // 存储所属章节
    private HashMap<String,String> chapterKeyName;

    // 存储对应知识点数组
    private HashMap<String,String> knowledgeKeyName;

    // 存储对应标签id数组
    private HashMap<String,String> tagIdName;
}