package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentRecommendLearnResourceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: StudentRecommendLearnController
 * Package: com.example.backend.controller.student
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 14:34
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/student/recommend")
public class StudentRecommendLearnController {
    @GetMapping("get")
    public Result get(@ModelAttribute StudentRecommendLearnResourceDto req) {

        return rcfwRecruitmentCompanyService.get(req);
    }
}
