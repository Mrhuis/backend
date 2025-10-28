package com.example.backend.service.admin.ks_knowledge_manage;

import com.example.backend.controller.admin.dto.AdminKSKnowledgeAddDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeUpdateDto;
import com.example.backend.entity.Knowledge;

import java.util.List;

/**
 * ClassName: KnowledgeService
 * Package: com.example.backend.service.admin.ks_knowledge_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/8/29 15:39
 * @Version 1.0
 */
public interface AdminKSKnowledgeService {
    List<Knowledge> getKnowledgeList(AdminKSKnowledgeQueryListDto req);

    Long getKnowledgesCount(AdminKSKnowledgeQueryListDto req);

    boolean addKnowledge(AdminKSKnowledgeAddDto req);

    boolean updateKnowledge(AdminKSKnowledgeUpdateDto req);

    boolean deleteKnowledgeById(Integer id);
}
