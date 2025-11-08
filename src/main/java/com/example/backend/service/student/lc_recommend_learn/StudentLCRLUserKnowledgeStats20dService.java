package com.example.backend.service.student.lc_recommend_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnRecodeDto;

import java.util.List;

/**
 * ClassName: StudentLCUserKnowledgeStats20dService
 * Package: com.example.backend.service.student.lc_recommend_learn
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/30 17:55
 * @Version 1.0
 */
public interface StudentLCRLUserKnowledgeStats20dService {
    Result updateUserKnowledgeStats(StudentLCRLRecommendLearnRecodeDto req, List<String> knowledgeKeys);
}
