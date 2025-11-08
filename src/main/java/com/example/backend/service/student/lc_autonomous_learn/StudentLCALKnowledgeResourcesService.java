package com.example.backend.service.student.lc_autonomous_learn;

import java.util.List;

/**
 * ClassName: StudentLCKnowledgeResourcesService
 * Package: com.example.backend.service.student.lc_recommend_learn
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/31 9:19
 * @Version 1.0
 */
public interface StudentLCALKnowledgeResourcesService {
    List<String> getKnowledgeKeys(String resourceType, String resourceKey);
}
