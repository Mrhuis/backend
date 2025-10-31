package com.example.backend.controller.student.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: StudentLCRecommendLearnRecodeDto
 * Package: com.example.backend.controller.student.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/29 15:49
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLCRecommendLearnRecodeDto {

    /**
     * 用户标识
     */
    @JsonAlias({"user_key", "userKey"})
    private String userKey;

    /**
     * 资源类型
     */
    @JsonAlias({"form_key", "formKey"})
    private String formKey;
    /**
     * 资源标识
     */
    @JsonAlias({"resource_key", "resourceKey"})
    private String resourceKey;
    /**
     * 是否完成该题
     */
    @JsonAlias({"is_complete", "isComplete"})
    private Boolean isComplete;

    /**
     * 是否正确
     */
    @JsonAlias({"is_correct", "isCorrect"})
    private Boolean isCorrect;
    /**
     * 是否查看解析
     */
    @JsonAlias({"is_view_analysis", "isViewAnalysis"})
    private Boolean isViewAnalysis;
    /**
     * 视频观看率
     */
    @JsonAlias({"watch_rate", "watchRate"})
    private Double watchRate;
    /**
     * 是否暂停
     */
    @JsonAlias({"is_pause", "isPause"})
    private Boolean isPause;
    /**
     * 是否反复观看
     */
    @JsonAlias({"is_replay", "isReplay"})
    private Boolean isReplay;
    /**
     * 交互时间
     */
    @JsonAlias({"interaction_time", "interactionTime"})
    private LocalDateTime interactionTime;

}
