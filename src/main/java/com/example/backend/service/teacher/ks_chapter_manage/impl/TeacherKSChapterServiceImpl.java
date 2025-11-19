package com.example.backend.service.teacher.ks_chapter_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherKSChapterAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSChapterQueryListDto;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ChapterMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.ks_chapter_manage.TeacherKSChapterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherKSChapterServiceImpl implements TeacherKSChapterService {

    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private PluginService pluginService;

    @Override
    public List<Chapter> getChapterList(TeacherKSChapterQueryListDto req) {
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<Chapter> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return chapterMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getChaptersCount(TeacherKSChapterQueryListDto req) {
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        
        if (req.getKey() != null && !req.getKey().isEmpty()) {
            queryWrapper.like("key", req.getKey());
        }
        
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        
        return chapterMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addChapter(TeacherKSChapterAddDto req) {
        // 获取当前启用的插件
        Plugin enabledPlugin = pluginService.getEnabledPlugin();
        
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(req, chapter);
        chapter.setChapterKey(req.getKey());
        chapter.setPluginKey(enabledPlugin != null ? enabledPlugin.getPluginKey() : "");
        chapter.setStatus("PENDING"); // 教师端添加的章节默认状态为PENDING
        
        // 处理父章节关系
        if (req.getParentId() != null) {
            // 根据 parentId 查询父章节的 chapterKey
            Chapter parentChapter = chapterMapper.selectById(req.getParentId());
            if (parentChapter != null) {
                chapter.setParentChapterKey(parentChapter.getChapterKey());
                // 如果未提供 level，则根据父章节的 level + 1
                if (req.getLevel() == null) {
                    chapter.setLevel(parentChapter.getLevel() != null ? parentChapter.getLevel() + 1 : 1);
                } else {
                    chapter.setLevel(req.getLevel());
                }
            } else {
                // 父章节不存在，设置为 null
                chapter.setParentChapterKey(null);
                chapter.setLevel(req.getLevel() != null ? req.getLevel() : 1);
            }
        } else {
            // 没有父章节，设置为 null
            chapter.setParentChapterKey(null);
            // 如果未提供 level，默认为 1
            chapter.setLevel(req.getLevel() != null ? req.getLevel() : 1);
        }
        
        return chapterMapper.insert(chapter) > 0;
    }

    @Override
    public boolean deleteChapterById(Integer id) {
        return chapterMapper.deleteById(id) > 0;
    }
}