package com.example.backend.service.teacher.ks_knowledge_manage;

import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeQueryListDto;
import com.example.backend.entity.Knowledge;

import java.util.List;

public interface TeacherKSKnowledgeService {
    /**
     * 获取知识点列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 知识点列表
     */
    List<Knowledge> getKnowledgeList(TeacherKSKnowledgeQueryListDto req);

    /**
     * 获取知识点总数
     * @param req 查询条件
     * @return 知识点总数
     */
    Long getKnowledgesCount(TeacherKSKnowledgeQueryListDto req);

    /**
     * 添加知识点
     * @param req 知识点信息
     * @return 是否添加成功
     */
    boolean addKnowledge(TeacherKSKnowledgeAddDto req);

    /**
     * 删除知识点
     * @param id 知识点ID
     * @return 是否删除成功
     */
    boolean deleteKnowledgeById(Integer id);
}