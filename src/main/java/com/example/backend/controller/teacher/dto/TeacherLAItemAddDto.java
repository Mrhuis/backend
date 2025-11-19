package com.example.backend.controller.teacher.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherLAItemAddDto {
    @JsonAlias({"item_key", "itemKey"})
    private String itemKey;
    
    @JsonAlias({"form_key", "formKey"})
    private String formKey;
    
    private Integer difficulty;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private String solution;
    
    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;
    
    @JsonAlias({"chapter_key", "chapterKey"})
    private List<String> chapter_key;
    
    @JsonAlias({"knowledge_key", "knowledgeKey"})
    private List<String> knowledge_key;
    
    @JsonAlias({"tag_id", "tagId"})
    private List<Long> tagId;

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
}