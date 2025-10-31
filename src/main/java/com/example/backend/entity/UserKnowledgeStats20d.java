package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: UserKnowledgeStats20d
 * Package: com.example.backend.entity
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/29 16:03
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_knowledge_stats_20d")
public class UserKnowledgeStats20d {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userKey;

    private String knowledgeKey;

    private Integer correctCount;

    private Integer totalCount;

    private LocalDateTime recordTime;

}
