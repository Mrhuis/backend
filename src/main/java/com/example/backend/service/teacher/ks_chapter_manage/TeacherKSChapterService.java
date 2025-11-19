package com.example.backend.service.teacher.ks_chapter_manage;

import com.example.backend.controller.teacher.dto.TeacherKSChapterAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSChapterQueryListDto;
import com.example.backend.entity.Chapter;

import java.util.List;

public interface TeacherKSChapterService {
    /**
     * 获取章节列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 章节列表
     */
    List<Chapter> getChapterList(TeacherKSChapterQueryListDto req);

    /**
     * 获取章节总数
     * @param req 查询条件
     * @return 章节总数
     */
    Long getChaptersCount(TeacherKSChapterQueryListDto req);

    /**
     * 添加章节
     * @param req 章节信息
     * @return 是否添加成功
     */
    boolean addChapter(TeacherKSChapterAddDto req);

    /**
     * 删除章节
     * @param id 章节ID
     * @return 是否删除成功
     */
    boolean deleteChapterById(Integer id);
}