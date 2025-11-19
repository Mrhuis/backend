package com.example.backend.controller.teacher.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教师端习题状态更新DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherLAItemUpdateStatusDto {
        /**
     * 数据库自增ID
     */
    private Long id;  // 全小写，无大写字母，无需加@JsonAlias


    /**
     * 状态：待审核(PENDING)、启用(ENABLED)、禁用(DISABLED)、拒绝(REJECTED)，默认ENABLED
     */
    private String status;  // 全小写，无大写字母，无需加@JsonAlias



}