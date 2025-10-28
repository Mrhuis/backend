package com.example.backend.controller.admin.dto;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: AdminLAMediaQueryListDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 11:38
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLAMediaQueryListDto extends BaseEntity {

//    private Long id;

    @JsonAlias({"media_key", "mediaKey"})
    private String mediaKey;

//    @JsonAlias({"plugin_key", "pluginKey"})
//    private String pluginKey;

//    @JsonAlias({"asset_type", "assetType"})
//    private String assetType;

    @JsonAlias({"file_name", "fileName"})
    private String fileName;

//    private String url;

//    @JsonAlias({"entry_point", "entryPoint"})
//    private String entryPoint;
//
//    @JsonAlias({"implementation_script", "implementationScript"})
//    private String implementationScript;

//    private Integer duration; // 资源时长（秒）

//    @JsonAlias({"uploaded_by", "uploadedBy"})
//    private String uploadedBy;

    private String status;

//    @JsonAlias({"created_at", "createdAt"})
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
