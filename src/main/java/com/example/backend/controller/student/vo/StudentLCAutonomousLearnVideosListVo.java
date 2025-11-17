package com.example.backend.controller.student.vo;

import com.example.backend.common.dto.BaseEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClassName: StudentLCAutonomousLearnVideosListVo
 * Package: com.example.backend.controller.student.vo
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/11/16 22:30
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StudentLCAutonomousLearnVideosListVo extends BaseEntity {
    /**
     * 数据库自增ID
     */
    private Long id;

    /**
     * 媒体资源业务唯一标识（如M_stack_001）
     */
    @JsonAlias({"media_key", "mediaKey"})
    private String mediaKey;

    /**
     * 所属插件（关联plugins.plugin_key）
     */
    @JsonAlias({"plugin_key", "pluginKey"})
    private String pluginKey;

    /**
     * 文件名（如video.mp4）
     */
    @JsonAlias({"file_name", "fileName"})
    private String fileName;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 上传者标识（关联users.user_key）
     */
    @JsonAlias({"uploaded_by", "uploadedBy"})
    private String uploadedBy;

    /**
     * 媒体类型：视频(video)、音频(audio)、图片(image)等
     */
    @JsonAlias({"asset_type", "assetType"})
    private String assetType;

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