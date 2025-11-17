package com.example.backend.service.student.lc_autonomous_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.mapper.TagResourceMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.student.lc_autonomous_learn.StudentLCALVideosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentLCALVideosServiceImpl implements StudentLCALVideosService {

    private static final Logger log = LoggerFactory.getLogger(StudentLCALVideosServiceImpl.class);

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;

    @Autowired
    private PluginService pluginService;

    // 添加Mapper注入
    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private TagResourceMapper tagResourceMapper;

    @Override
    public List<MediaAssets> getVideosList(StudentLCAutonomousLearnListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取视频列表 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }

            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());

            // 检查是否需要根据关联表进行筛选
            boolean hasChapterFilter = req != null && req.getChapter_key() != null && !req.getChapter_key().isEmpty();
            boolean hasKnowledgeFilter = req != null && req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty();
            boolean hasTagFilter = req != null && req.getTagId() != null && !req.getTagId().isEmpty();

            List<MediaAssets> result;

            if (hasChapterFilter || hasKnowledgeFilter || hasTagFilter) {
                // 需要根据关联表进行筛选
                result = getVideosListWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                result = getVideosListWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取视频信息列表失败", e);
            throw new RuntimeException("获取视频信息列表失败", e);
        }
    }

    /**
     * 不带关联表筛选的查询
     */
    private List<MediaAssets> getVideosListWithoutAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();

        // 必须限制为当前启用的插件
        queryWrapper.eq("plugin_key", pluginKey);

        // 添加查询条件
        if (req != null) {
            // 媒体标识模糊查询
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("media_key", req.getItemKey());
            }

            // 媒体名称模糊查询
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }

            // 状态精确查询
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");

        // 分页查询
        if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
        }

        log.info("执行查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectList(queryWrapper);
    }

    /**
     * 带关联表筛选的查询
     */
    private List<MediaAssets> getVideosListWithAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        // 先获取所有符合条件的media_key
        List<String> filteredMediaKeys = getFilteredMediaKeys(req);

        if (filteredMediaKeys.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据筛选后的media_key查询MediaAssets
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("media_key", filteredMediaKeys);

        // 添加其他查询条件
        if (req != null) {
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("media_key", req.getItemKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");

        // 分页查询
        if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
            queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
        }

        log.info("执行关联表筛选查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectList(queryWrapper);
    }

    /**
     * 根据关联表筛选条件获取符合条件的media_key列表
     */
    private List<String> getFilteredMediaKeys(StudentLCAutonomousLearnListDto req) {
        List<String> chapterMediaKeys = new ArrayList<>();
        List<String> knowledgeMediaKeys = new ArrayList<>();
        List<String> tagMediaKeys = new ArrayList<>();

        // 根据章节筛选
        if (req.getChapter_key() != null && !req.getChapter_key().isEmpty()) {
            for (String chapterKey : req.getChapter_key()) {
                List<String> mediaKeys = chapterResourcesMapper.selectResourceKeysByChapterKey(chapterKey, "media");
                chapterMediaKeys.addAll(mediaKeys);
            }
        }

        // 根据知识点筛选
        if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
            for (String knowledgeKey : req.getKnowledge_key()) {
                List<String> mediaKeys = knowledgeResourcesMapper.selectResourceKeysByKnowledgeKey(knowledgeKey, "media");
                knowledgeMediaKeys.addAll(mediaKeys);
            }
        }

        // 根据标签筛选
        if (req.getTagId() != null && !req.getTagId().isEmpty()) {
            for (Long tagId : req.getTagId()) {
                List<String> mediaKeys = tagResourceMapper.selectResourceKeysByTagId(tagId, "media");
                tagMediaKeys.addAll(mediaKeys);
            }
        }

        // 计算交集
        List<String> result = new ArrayList<>();

        if (!chapterMediaKeys.isEmpty() && !knowledgeMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 三个条件都有，取交集
            result = chapterMediaKeys.stream()
                    .filter(knowledgeMediaKeys::contains)
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty() && !knowledgeMediaKeys.isEmpty()) {
            // 章节和知识点
            result = chapterMediaKeys.stream()
                    .filter(knowledgeMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 章节和标签
            result = chapterMediaKeys.stream()
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!knowledgeMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 知识点和标签
            result = knowledgeMediaKeys.stream()
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty()) {
            // 只有章节
            result = chapterMediaKeys.stream().distinct().collect(Collectors.toList());
        } else if (!knowledgeMediaKeys.isEmpty()) {
            // 只有知识点
            result = knowledgeMediaKeys.stream().distinct().collect(Collectors.toList());
        } else if (!tagMediaKeys.isEmpty()) {
            // 只有标签
            result = tagMediaKeys.stream().distinct().collect(Collectors.toList());
        }

        log.info("关联表筛选结果 - 章节筛选: {}, 知识点筛选: {}, 标签筛选: {}, 最终结果: {}",
                chapterMediaKeys.size(), knowledgeMediaKeys.size(), tagMediaKeys.size(), result.size());

        return result;
    }

    @Override
    public Long getVideosCount(StudentLCAutonomousLearnListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取视频总数 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回0");
                return 0L;
            }

            // 检查是否需要根据关联表进行筛选
            boolean hasChapterFilter = req != null && req.getChapter_key() != null && !req.getChapter_key().isEmpty();
            boolean hasKnowledgeFilter = req != null && req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty();
            boolean hasTagFilter = req != null && req.getTagId() != null && !req.getTagId().isEmpty();

            Long count;

            if (hasChapterFilter || hasKnowledgeFilter || hasTagFilter) {
                // 需要根据关联表进行筛选
                count = getVideosCountWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                count = getVideosCountWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果总数: {}", count);
            return count;
        } catch (Exception e) {
            log.error("获取视频信息总数失败", e);
            throw new RuntimeException("获取视频信息总数失败", e);
        }
    }

    /**
     * 不带关联表筛选的计数查询
     */
    private Long getVideosCountWithoutAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);

        if (req != null) {
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("media_key", req.getItemKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectCount(queryWrapper);
    }

    /**
     * 带关联表筛选的计数查询
     */
    private Long getVideosCountWithAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        // 先获取所有符合条件的media_key
        List<String> filteredMediaKeys = getFilteredMediaKeys(req);

        if (filteredMediaKeys.isEmpty()) {
            return 0L;
        }

        // 根据筛选后的media_key查询MediaAssets数量
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("media_key", filteredMediaKeys);

        if (req != null) {
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("media_key", req.getItemKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectCount(queryWrapper);
    }
}