package com.example.backend.service.admin.la_item_manage;

import com.example.backend.controller.admin.dto.AdminLAItemAddDto;
import com.example.backend.controller.admin.dto.AdminLAItemUpdateDto;
import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;

import java.util.HashMap;

/**
 * ClassName: KnowledgeResourcesService
 * Package: com.example.backend.service.common
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/9 14:52
 * @Version 1.0
 */
public interface AdminLAItemKnowledgeResourcesService {
    boolean addKnowledgeResources(AdminLAItemAddDto req);

    boolean updateKnowledgeResources(AdminLAItemUpdateDto req);

    boolean deleteKnowledgeResources(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}
