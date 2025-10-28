package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: AdminLAMediaAddDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 11:36
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLAMediaAddDto {
    @JsonAlias({"video_file", "videoFile"})
    private MultipartFile videoFile;

    @JsonAlias({"media_key", "mediaKey"})
    private String mediaKey;

//    @JsonAlias({"plugin_key", "pluginKey"})
//    private String pluginKey;
//
//    @JsonAlias({"asset_type", "assetType"})
//    private String assetType="video";

    @JsonAlias({"file_name", "fileName"})
    private String fileName;

//    private String url;

//    @JsonAlias({"entry_point", "entryPoint"})
//    private String entryPoint;
//
//    @JsonAlias({"implementation_script", "implementationScript"})
//    private String implementationScript;

//    private Integer duration;

    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

//    private String status;

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
