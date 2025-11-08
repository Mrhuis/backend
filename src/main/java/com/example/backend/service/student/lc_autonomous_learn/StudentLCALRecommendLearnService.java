package com.example.backend.service.student.lc_autonomous_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnResourceDto;

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

public interface StudentLCALRecommendLearnService {
    Result getRecommendLearnResource(List<StudentLCRLRecommendLearnResourceDto> datas);

    Result recode(StudentLCAutonomousLearnRecodeDto req);
}
