package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("class_student_enrollments")
public class ClassStudentEnrollment {
    /**
     * 数据库自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联班级（classes.class_key）
     */
    private String classKey;

    /**
     * 关联学生（users.user_key）
     */
    private String userKey;

    /**
     * 加入状态：已加入/待审核
     */
    private Integer status;

    /**
     * 加入班级时间（用于计算班级活跃度）
     */
    private LocalDateTime enrolledAt;
}