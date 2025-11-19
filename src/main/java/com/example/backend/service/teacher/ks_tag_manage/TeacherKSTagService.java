package com.example.backend.service.teacher.ks_tag_manage;

import com.example.backend.controller.teacher.dto.TeacherKSTagAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSTagQueryListDto;
import com.example.backend.entity.Tag;

import java.util.List;

public interface TeacherKSTagService {
    /**
     * 获取标签列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 标签列表
     */
    List<Tag> getTagList(TeacherKSTagQueryListDto req);

    /**
     * 获取标签总数
     * @param req 查询条件
     * @return 标签总数
     */
    Long getTagsCount(TeacherKSTagQueryListDto req);

    /**
     * 添加标签
     * @param req 标签信息
     * @return 是否添加成功
     */
    boolean addTag(TeacherKSTagAddDto req);

    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否删除成功
     */
    boolean deleteTagById(Integer id);
}