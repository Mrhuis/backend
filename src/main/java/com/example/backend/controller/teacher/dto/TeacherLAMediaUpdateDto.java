package com.example.backend.controller.teacher.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherLAMediaUpdateDto {
    private Long id;

    @JsonAlias({"video_file", "videoFile"})
    private MultipartFile videoFile;

    @JsonAlias({"media_key", "mediaKey"})
    private String mediaKey;

    @JsonAlias({"file_name", "fileName"})
    private String fileName;

    private String url;

    private String status;

    @JsonAlias({"chapter_key", "chapterKey"})
    private List<String> chapter_key;

    @JsonAlias({"knowledge_key", "knowledgeKey"})
    private List<String> knowledge_key;

    @JsonAlias({"tag_id", "tagId"})
    private List<Long> tagId;
}