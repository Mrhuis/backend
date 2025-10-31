package com.example.backend.controller.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: StudentLCRecommendLearnResourceVo
 * Package: com.example.backend.controller.student.vo
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 16:06
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLCRecommendLearnResourceVo {
    private String formType;
    private String formKey;
    private String resourceKey;
    //习题字段
    private Integer difficulty;
    private String content;
    private String options;
    private String answer;
    private String solution;
    //视频字段
    private String fileName;
    private String url;
    private Integer duration;

}
