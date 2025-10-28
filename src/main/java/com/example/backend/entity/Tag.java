package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: Tag
 * Package: com.example.backend.entity
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 11:12
 * @Version 1.0
 */
@Data
@TableName("tags")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String pluginKey;

    private String tagApplicableType;

    private String tagContent;

    private String tagDesc;

    private String uploadedBy;

    private String status;

    private LocalDateTime createdAt;
}
