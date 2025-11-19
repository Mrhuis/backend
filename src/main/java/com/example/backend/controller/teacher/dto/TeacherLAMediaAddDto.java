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
public class TeacherLAMediaAddDto {
    @JsonAlias({"video_file", "videoFile"})
    private MultipartFile videoFile;

    @JsonAlias({"media_key", "mediaKey"})
    private String mediaKey;

    @JsonAlias({"file_name", "fileName"})
    private String fileName;

    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

    @JsonAlias({"chapter_key", "chapterKey"})
    private List<String> chapter_key;

    @JsonAlias({"knowledge_key", "knowledgeKey"})
    private List<String> knowledge_key;

    @JsonAlias({"tag_id", "tagId"})
    private List<Long> tagId;

    @JsonAlias({"video_url", "videoUrl", "url"})
    private String videoUrl; // 可选的视频URL路径，如果提供则直接使用，不进行文件上传

    public MultipartFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public List<String> getChapter_key() {
        return chapter_key;
    }

    public void setChapter_key(List<String> chapter_key) {
        this.chapter_key = chapter_key;
    }

    public List<String> getKnowledge_key() {
        return knowledge_key;
    }

    public void setKnowledge_key(List<String> knowledge_key) {
        this.knowledge_key = knowledge_key;
    }

    public List<Long> getTagId() {
        return tagId;
    }

    public void setTagId(List<Long> tagId) {
        this.tagId = tagId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}