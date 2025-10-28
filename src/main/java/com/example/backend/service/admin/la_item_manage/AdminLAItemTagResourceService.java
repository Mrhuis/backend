package com.example.backend.service.admin.la_item_manage;

import com.example.backend.controller.admin.dto.AdminLAItemAddDto;
import com.example.backend.controller.admin.dto.AdminLAItemUpdateDto;

import java.util.HashMap;

/**
 * ClassName: ResourceTagService
 * Package: com.example.backend.service.common
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/9 14:53
 * @Version 1.0
 */
public interface AdminLAItemTagResourceService {
    boolean addResourceTag(AdminLAItemAddDto req);

    boolean updateResourceTag(AdminLAItemUpdateDto req);

    boolean deleteResourceTag(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}
