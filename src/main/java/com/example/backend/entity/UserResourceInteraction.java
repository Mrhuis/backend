package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName: UserResourceInteraction
 * Package: com.example.backend.entity
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/29 16:12
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_resource_interaction")
public class UserResourceInteraction {

    @TableId(type = IdType.AUTO)
    private Long id;

     private String userKey;

     private String formKey;

     private String resourceKey;

     private String post3dCorrectRate;

     private String postPracticeCount;

     private Integer isFirstSubmit24h;

     private String correctRateChange;

     private Integer isComplete;

     private Integer isCorrect;

     private Integer isViewAnalysis;

     private Double watchRate;

     private Integer isPause;

     private Integer isReplay;

     private LocalDateTime interactionTime;

     private LocalDateTime effectCalcTime;


}