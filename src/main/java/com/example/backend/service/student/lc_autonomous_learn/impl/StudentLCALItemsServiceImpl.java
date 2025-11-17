package com.example.backend.service.student.lc_autonomous_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.entity.Item;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.mapper.TagResourceMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.student.lc_autonomous_learn.StudentLCALItemsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentLCALItemsServiceImpl implements StudentLCALItemsService {

    private static final Logger log = LoggerFactory.getLogger(StudentLCALItemsServiceImpl.class);

    @Autowired
    private ItemsMapper itemsMapper;

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
    public List<Item> getItemsList(StudentLCAutonomousLearnListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取习题列表 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }

            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());

            // 检查是否需要根据关联表进行筛选
            boolean hasChapterFilter = req != null && req.getChapter_key() != null && !req.getChapter_key().isEmpty();
            boolean hasKnowledgeFilter = req != null && req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty();
            boolean hasTagFilter = req != null && req.getTagId() != null && !req.getTagId().isEmpty();

            List<Item> result;

            if (hasChapterFilter || hasKnowledgeFilter || hasTagFilter) {
                // 需要根据关联表进行筛选
                result = getItemsListWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                result = getItemsListWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取习题信息列表失败", e);
            throw new RuntimeException("获取习题信息列表失败", e);
        }
    }

    /**
     * 不带关联表筛选的查询
     */
    private List<Item> getItemsListWithoutAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();

        // 必须限制为当前启用的插件
        queryWrapper.eq("plugin_key", pluginKey);

        // 添加查询条件
        if (req != null) {
            // 习题标识模糊查询
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("item_key", req.getItemKey());
            }

            // 习题类型精确查询（字符串类型，用hasText判断非空+非空白，再用eq精确匹配）
            if (StringUtils.hasText(req.getFormKey())) {
                queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
            }

            // 难度精确查询（Integer类型，判断非null即可，再用eq精确匹配）
            if (req.getDifficulty() != null) { // 整数类型直接判断是否为null
                queryWrapper.eq("difficulty", req.getDifficulty()); // 精确匹配难度
            }

            // 题干模糊查询
            if (StringUtils.hasText(req.getContent())) {
                queryWrapper.like("content", req.getContent());
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
        return itemsMapper.selectList(queryWrapper);
    }

    /**
     * 带关联表筛选的查询
     */
    private List<Item> getItemsListWithAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        // 先获取所有符合条件的item_key
        List<String> filteredItemKeys = getFilteredItemKeys(req);

        if (filteredItemKeys.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据筛选后的item_key查询ItemAssets
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("item_key", filteredItemKeys);

        // 添加其他查询条件
        if (req != null) {
            // 习题标识模糊查询
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("item_key", req.getItemKey());
            }

            // 习题类型精确查询（字符串类型，用hasText判断非空+非空白，再用eq精确匹配）
            if (StringUtils.hasText(req.getFormKey())) {
                queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
            }

            // 难度精确查询（Integer类型，判断非null即可，再用eq精确匹配）
            if (req.getDifficulty() != null) { // 整数类型直接判断是否为null
                queryWrapper.eq("difficulty", req.getDifficulty()); // 精确匹配难度
            }

            // 题干模糊查询
            if (StringUtils.hasText(req.getContent())) {
                queryWrapper.like("content", req.getContent());
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

        log.info("执行关联表筛选查询，SQL条件: {}", queryWrapper.getTargetSql());
        return itemsMapper.selectList(queryWrapper);
    }

    /**
     * 根据关联表筛选条件获取符合条件的item_key列表
     */
    private List<String> getFilteredItemKeys(StudentLCAutonomousLearnListDto req) {
        List<String> chapterItemKeys = new ArrayList<>();
        List<String> knowledgeItemKeys = new ArrayList<>();
        List<String> tagItemKeys = new ArrayList<>();

        // 根据章节筛选
        if (req.getChapter_key() != null && !req.getChapter_key().isEmpty()) {
            for (String chapterKey : req.getChapter_key()) {
                List<String> itemKeys = chapterResourcesMapper.selectResourceKeysByChapterKey(chapterKey, "item");
                chapterItemKeys.addAll(itemKeys);
            }
        }

        // 根据知识点筛选
        if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
            for (String knowledgeKey : req.getKnowledge_key()) {
                List<String> itemKeys = knowledgeResourcesMapper.selectResourceKeysByKnowledgeKey(knowledgeKey, "item");
                knowledgeItemKeys.addAll(itemKeys);
            }
        }

        // 根据标签筛选
        if (req.getTagId() != null && !req.getTagId().isEmpty()) {
            for (Long tagId : req.getTagId()) {
                List<String> itemKeys = tagResourceMapper.selectResourceKeysByTagId(tagId, "item");
                tagItemKeys.addAll(itemKeys);
            }
        }

        // 计算交集
        List<String> result = new ArrayList<>();

        if (!chapterItemKeys.isEmpty() && !knowledgeItemKeys.isEmpty() && !tagItemKeys.isEmpty()) {
            // 三个条件都有，取交集
            result = chapterItemKeys.stream()
                    .filter(knowledgeItemKeys::contains)
                    .filter(tagItemKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterItemKeys.isEmpty() && !knowledgeItemKeys.isEmpty()) {
            // 章节和知识点
            result = chapterItemKeys.stream()
                    .filter(knowledgeItemKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterItemKeys.isEmpty() && !tagItemKeys.isEmpty()) {
            // 章节和标签
            result = chapterItemKeys.stream()
                    .filter(tagItemKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!knowledgeItemKeys.isEmpty() && !tagItemKeys.isEmpty()) {
            // 知识点和标签
            result = knowledgeItemKeys.stream()
                    .filter(tagItemKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterItemKeys.isEmpty()) {
            // 只有章节
            result = chapterItemKeys.stream().distinct().collect(Collectors.toList());
        } else if (!knowledgeItemKeys.isEmpty()) {
            // 只有知识点
            result = knowledgeItemKeys.stream().distinct().collect(Collectors.toList());
        } else if (!tagItemKeys.isEmpty()) {
            // 只有标签
            result = tagItemKeys.stream().distinct().collect(Collectors.toList());
        }

        log.info("关联表筛选结果 - 章节筛选: {}, 知识点筛选: {}, 标签筛选: {}, 最终结果: {}",
                chapterItemKeys.size(), knowledgeItemKeys.size(), tagItemKeys.size(), result.size());

        return result;
    }

    @Override
    public Long getItemsCount(StudentLCAutonomousLearnListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取习题总数 - 当前启用的插件: {}", enabledPlugin);

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
                count = getItemsCountWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                count = getItemsCountWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果总数: {}", count);
            return count;
        } catch (Exception e) {
            log.error("获取习题信息总数失败", e);
            throw new RuntimeException("获取习题信息总数失败", e);
        }
    }

    /**
     * 不带关联表筛选的计数查询
     */
    private Long getItemsCountWithoutAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);

        if (req != null) {
            // 习题标识模糊查询
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("item_key", req.getItemKey());
            }

            // 习题类型精确查询（字符串类型，用hasText判断非空+非空白，再用eq精确匹配）
            if (StringUtils.hasText(req.getFormKey())) {
                queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
            }

            // 难度精确查询（Integer类型，判断非null即可，再用eq精确匹配）
            if (req.getDifficulty() != null) { // 整数类型直接判断是否为null
                queryWrapper.eq("difficulty", req.getDifficulty()); // 精确匹配难度
            }

            // 题干模糊查询
            if (StringUtils.hasText(req.getContent())) {
                queryWrapper.like("content", req.getContent());
            }

            // 状态精确查询
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return itemsMapper.selectCount(queryWrapper);
    }

    /**
     * 带关联表筛选的计数查询
     */
    private Long getItemsCountWithAssociationFilter(StudentLCAutonomousLearnListDto req, String pluginKey) {
        // 先获取所有符合条件的item_key
        List<String> filteredItemKeys = getFilteredItemKeys(req);

        if (filteredItemKeys.isEmpty()) {
            return 0L;
        }

        // 根据筛选后的item_key查询ItemAssets数量
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("item_key", filteredItemKeys);

        if (req != null) {
            // 习题标识模糊查询
            if (StringUtils.hasText(req.getItemKey())) {
                queryWrapper.like("item_key", req.getItemKey());
            }

            // 习题类型精确查询（字符串类型，用hasText判断非空+非空白，再用eq精确匹配）
            if (StringUtils.hasText(req.getFormKey())) {
                queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
            }

            // 难度精确查询（Integer类型，判断非null即可，再用eq精确匹配）
            if (req.getDifficulty() != null) { // 整数类型直接判断是否为null
                queryWrapper.eq("difficulty", req.getDifficulty()); // 精确匹配难度
            }

            // 题干模糊查询
            if (StringUtils.hasText(req.getContent())) {
                queryWrapper.like("content", req.getContent());
            }

            // 状态精确查询
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
        }

        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return itemsMapper.selectCount(queryWrapper);
    }
}