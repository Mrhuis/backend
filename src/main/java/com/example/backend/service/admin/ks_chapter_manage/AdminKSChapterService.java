package com.example.backend.service.admin.ks_chapter_manage;

import com.example.backend.controller.admin.dto.AdminKSChapterAddDto;
import com.example.backend.controller.admin.dto.AdminKSChapterQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSChapterUpdateDto;
import com.example.backend.entity.Chapter;

import java.util.List;

/**
 * ClassName: AdminKSChapterService
 * Package: com.example.backend.service.admin.ks_chapter_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/1 17:16
 * @Version 1.0
 */
public interface AdminKSChapterService {
    List<Chapter> getChapterList(AdminKSChapterQueryListDto req);

    Long getChaptersCount(AdminKSChapterQueryListDto req);

    boolean addChapter(AdminKSChapterAddDto req);

    boolean updateChapter(AdminKSChapterUpdateDto req);

    boolean deleteChapterById(Integer id);
}
