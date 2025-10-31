package com.example.backend.service.student.lc_recommend_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnResourceDto;

import java.util.List;

/**
 * ClassName: StudentRecommendLearnService
 * Package: com.example.backend.service.student
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 15:00
 * @Version 1.0
 */

public interface StudentLCRecommendLearnService {
    Result getRecommendLearnResource(List<StudentLCRecommendLearnResourceDto> datas);

    Result recode(StudentLCRecommendLearnRecodeDto req);
}
