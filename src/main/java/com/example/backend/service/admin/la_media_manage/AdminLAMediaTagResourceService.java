package com.example.backend.service.admin.la_media_manage;

import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;

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
public interface AdminLAMediaTagResourceService {
    boolean addResourceTag(AdminLAMediaAddDto req);

    boolean updateResourceTag(AdminLAMediaUpdateDto req);

    boolean deleteResourceTag(Integer id);

    HashMap<String,String> selectChapterKeyAndChapterNameByResourceKey(String resourceKey);
}
