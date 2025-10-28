package com.example.backend.service.admin.ks_tag_manage;

import com.example.backend.controller.admin.dto.AdminKSTagAddDto;
import com.example.backend.controller.admin.dto.AdminKSTagQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSTagUpdateDto;
import com.example.backend.entity.Tag;

import java.util.List;

/**
 * ClassName: AdminKSTagService
 * Package: com.example.backend.service.admin.ks_tag_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 11:55
 * @Version 1.0
 */
public interface AdminKSTagService {
    List<Tag> getTagList(AdminKSTagQueryListDto req);

    Long getTagsCount(AdminKSTagQueryListDto req);

    boolean addTag(AdminKSTagAddDto req);

    boolean updateTag(AdminKSTagUpdateDto req);

    boolean deleteTagById(Integer id);
}
