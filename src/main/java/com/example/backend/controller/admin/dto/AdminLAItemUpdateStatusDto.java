package com.example.backend.controller.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: AdminLAItemUpdateStatusDto
 * Package: com.example.backend.controller.admin.dto
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/10 14:26
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLAItemUpdateStatusDto {
        /**
     * 数据库自增ID
     */
    private Long id;  // 全小写，无大写字母，无需加@JsonAlias


    /**
     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
     */
    private String status;  // 全小写，无大写字母，无需加@JsonAlias

    /**
     * 审核人标识（可选），前端可能传 reviewerKey
     */
    @JsonAlias({"reviewer_key", "reviewerKey"})
    private String reviewerKey;
}
