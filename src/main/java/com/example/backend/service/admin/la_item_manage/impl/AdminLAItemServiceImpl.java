package com.example.backend.service.admin.la_item_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.backend.controller.admin.dto.*;
import com.example.backend.entity.Item;
import com.example.backend.entity.ResourceForm;

import com.example.backend.entity.Plugin;
import com.example.backend.mapper.*;
import com.example.backend.service.admin.la_item_manage.AdminLAItemChapterResourcesService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemKnowledgeResourcesService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemTagResourceService;
import com.example.backend.service.admin.la_item_manage.AdminLAItemService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;

import com.example.backend.tool.DirectoryTool;
import com.example.backend.tool.media.MultipartFileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminLAItemServiceImpl
 * Package: com.example.backend.service.admin.la_item_manage.ipml
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/10 15:40
 * @Version 1.0
 */
@Service
public class AdminLAItemServiceImpl implements AdminLAItemService {


    private static final Logger log = LoggerFactory.getLogger(AdminLAItemServiceImpl.class);

    @Value("${upload.resource.path}")
    private String baseResourcePath;
    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private AdminLAItemChapterResourcesService chapterResourcesService;

    @Autowired
    private AdminLAItemKnowledgeResourcesService knowledgeResourcesService;

    @Autowired
    private AdminLAItemTagResourceService resourceTagService;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;

    // 添加Mapper注入
    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private TagResourceMapper tagResourceMapper;
    
    @Autowired
    private ResourceFormMapper resourceFormMapper;

    @Override
    public List<Item> getItemList(AdminLAItemQueryListDto req) {
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
                result = getLAItemListWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                result = getLAItemListWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }
            
            // 处理返回结果中的form_key，如果对应的resource_form表中status为DISABLED，则form_key显示为other
            processItemFormKeys(result, enabledPlugin.getPluginKey());

            log.info("查询结果数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取习题信息列表失败", e);
            throw new RuntimeException("获取习题信息列表失败", e);
        }
    }

    /**
     * 处理返回结果中的form_key，如果对应的resource_form表中status为DISABLED，则form_key显示为other
     * @param items 习题列表
     * @param pluginKey 插件key
     */
    private void processItemFormKeys(List<Item> items, String pluginKey) {
        // 获取当前插件下所有status为DISABLED的resource_form记录
        QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.eq("status", "DISABLED");
        List<ResourceForm> disabledForms = resourceFormMapper.selectList(queryWrapper);
        
        // 提取这些记录的form_key
        List<String> disabledFormKeys = disabledForms.stream()
                .map(ResourceForm::getFormKey)
                .collect(Collectors.toList());
        
        // 遍历items，如果form_key在disabledFormKeys中，则设置为other
        for (Item item : items) {
            if (disabledFormKeys.contains(item.getFormKey())) {
                item.setFormKey("other");
            }
        }
    }


    /**
     * 不带关联表筛选的查询
     */
    private List<Item> getLAItemListWithoutAssociationFilter(AdminLAItemQueryListDto req, String pluginKey) {
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
                if ("other".equals(req.getFormKey())) {
                    // 如果form_key是other，则查询所有status为DISABLED的记录
                    queryWrapper.eq("status", "DISABLED");
                } else {
                    queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
                }
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

            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("item_key", searchValue)
                        .or()
                        .like("options", searchValue)
                        .or()
                        .like("content", searchValue)
                        .or()
                        .like("answer", searchValue)
                        .or()
                        .like("solution", searchValue)
                );
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
    private List<Item> getLAItemListWithAssociationFilter(AdminLAItemQueryListDto req, String pluginKey) {
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
                if ("other".equals(req.getFormKey())) {
                    // 如果form_key是other，则查询所有status为DISABLED的记录
                    queryWrapper.eq("status", "DISABLED");
                } else {
                    queryWrapper.eq("form_key", req.getFormKey()); // 精确匹配类型
                }
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

            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("item_key", searchValue)
                        .or()
                        .like("options", searchValue)
                        .or()
                        .like("content", searchValue)
                        .or()
                        .like("answer", searchValue)
                        .or()
                        .like("solution", searchValue)
                );
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
    private List<String> getFilteredItemKeys(AdminLAItemQueryListDto req) {
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
    public Long getItemCount(AdminLAItemQueryListDto req) {
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
                count = getLAItemsCountWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                count = getLAItemsCountWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
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
    private Long getLAItemsCountWithoutAssociationFilter(AdminLAItemQueryListDto req, String pluginKey) {
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

            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("item_key", searchValue)
                        .or()
                        .like("options", searchValue)
                        .or()
                        .like("content", searchValue)
                        .or()
                        .like("answer", searchValue)
                        .or()
                        .like("solution", searchValue)
                );
            }
        }


        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return itemsMapper.selectCount(queryWrapper);
    }

    /**
     * 带关联表筛选的计数查询
     */
    private Long getLAItemsCountWithAssociationFilter(AdminLAItemQueryListDto req, String pluginKey) {
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

            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("item_key", searchValue)
                        .or()
                        .like("options", searchValue)
                        .or()
                        .like("content", searchValue)
                        .or()
                        .like("answer", searchValue)
                        .or()
                        .like("solution", searchValue)
                );
            }
        }


        log.info("执行关联表筛选计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return itemsMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addLAItemWithAllRelations(AdminLAItemAddDto req) {
        try {

            // 1. 添加习题资源到 items 表
            boolean itemResult = addItem(req);
            if (!itemResult) {
                log.error("添加习题资源失败，itemKey: {}", req.getItemKey());
                throw new RuntimeException("添加习题资源失败");
            }
            log.info("习题资源添加成功，itemKey: {}", req.getItemKey());

            // 2. 添加章节关联到 chapter_resources 表
            boolean chapterResult = chapterResourcesService.addChapterResources(req);
            if (!chapterResult) {
                log.error("添加章节关联失败，itemKey: {}", req.getItemKey());
                throw new RuntimeException("添加章节关联失败");
            }
            log.info("章节关联添加成功，itemKey: {}", req.getItemKey());

            // 3. 添加知识点关联到 knowledge_resources 表
            boolean knowledgeResult = knowledgeResourcesService.addKnowledgeResources(req);
            if (!knowledgeResult) {
                log.error("添加知识点关联失败，itemKey: {}", req.getItemKey());
                throw new RuntimeException("添加知识点关联失败");
            }
            log.info("知识点关联添加成功，itemKey: {}", req.getItemKey());

            // 4. 添加标签关联到 resource_tag 表
            boolean tagResult = resourceTagService.addResourceTag(req);
            if (!tagResult) {
                log.error("添加标签关联失败，itemKey: {}", req.getItemKey());
                throw new RuntimeException("添加标签关联失败");
            }
            log.info("标签关联添加成功，itemKey: {}", req.getItemKey());

            log.info("所有操作成功完成，itemKey: {}", req.getItemKey());
            return true;

        } catch (Exception e) {
            log.error("事务性添加习题资源失败，itemKey: {}, 错误: {}", req.getItemKey(), e.getMessage(), e);
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    private boolean addItem(AdminLAItemAddDto req) {
        try {
            // 获取启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加习题资源");
            }

            // 校验插件标识不为空（避免后续插入时外键约束异常）
            if (enabledPlugin.getPluginKey() == null || enabledPlugin.getPluginKey().trim().isEmpty()) {
                throw new RuntimeException("启用的插件缺少有效的pluginKey");
            }

            // 创建习题资源对象
            Item item = new Item();
            // 注意：若req的字段可能为null，需根据业务判断是否允许（此处假设必填字段已在调用前校验）
            item.setItemKey(req.getItemKey());
            item.setPluginKey(enabledPlugin.getPluginKey());
            item.setFormKey(req.getFormKey());
            item.setDifficulty(req.getDifficulty());
            item.setContent(req.getContent());
            item.setOptions(req.getOptions());
            item.setAnswer(req.getAnswer());
            item.setSolution(req.getSolution());
            item.setUploadedBy(req.getUploadedBy());
            item.setCreatedAt(LocalDateTime.now());
            item.setStatus("DISABLED");  // 修正常量大写：DISABLED → DISABLED（建议与枚举保持一致）

            // 插入习题资源
            itemsMapper.insert(item);

            return true;
        } catch (Exception e) {
            // 捕获其他未知异常
            throw new RuntimeException("添加习题资源时发生错误：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLAItemWithAllRelations(AdminLAItemUpdateDto req) {
        try {
            log.info("开始事务性更新习题资源，id: {}", req.getId());

            // 1. 更新习题资源
            boolean itemResult = updateLAItem(req);
            if (!itemResult) {
                log.error("更新习题资源失败，id: {}", req.getId());
                throw new RuntimeException("更新习题资源失败");
            }
            log.info("习题资源更新成功，id: {}", req.getId());

            // 2. 更新章节关联
            boolean chapterResult = chapterResourcesService.updateChapterResources(req);
            if (!chapterResult) {
                log.error("更新章节关联失败，id: {}", req.getId());
                throw new RuntimeException("更新章节关联失败");
            }
            log.info("章节关联更新成功，id: {}", req.getId());

            // 3. 更新知识点关联
            boolean knowledgeResult = knowledgeResourcesService.updateKnowledgeResources(req);
            if (!knowledgeResult) {
                log.error("更新知识点关联失败，id: {}", req.getId());
                throw new RuntimeException("更新知识点关联失败");
            }
            log.info("知识点关联更新成功，id: {}", req.getId());

            // 4. 更新标签关联
            boolean tagResult = resourceTagService.updateResourceTag(req);
            if (!tagResult) {
                log.error("更新标签关联失败，id: {}", req.getId());
                throw new RuntimeException("更新标签关联失败");
            }
            log.info("标签关联更新成功，id: {}", req.getId());

            log.info("所有更新操作成功完成，id: {}", req.getId());
            return true;

        } catch (Exception e) {
            log.error("事务性更新习题资源失败，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    private boolean updateLAItem(AdminLAItemUpdateDto req) {
        try {
            // 1. 根据ID查询现有的习题资源记录
            Item item = itemsMapper.selectById(req.getId());
            if (item == null) {
                throw new RuntimeException("未找到ID为 " + req.getId() + " 的习题资源");
            }

            // 2. 获取启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法更新习题资源");
            }


            // 4. 更新其他非空字段
            if (req.getItemKey() != null && !req.getItemKey().isEmpty()) {
                item.setItemKey(req.getItemKey());
            }

            // 习题类型（字符串类型：非null且非空才赋值）
            if (req.getFormKey() != null && !req.getFormKey().isEmpty()) {
                item.setFormKey(req.getFormKey());
            }

            // 难度（整数类型：仅非null才赋值）
            if (req.getDifficulty() != null) {
                item.setDifficulty(req.getDifficulty());
            }

            // 题干内容（字符串类型：非null且非空才赋值）
            if (req.getContent() != null && !req.getContent().isEmpty()) {
                item.setContent(req.getContent());
            }

            // 选择题选项（字符串类型：非null且非空才赋值）
            if (req.getOptions() != null && !req.getOptions().isEmpty()) {
                item.setOptions(req.getOptions());
            }

            // 参考答案（字符串类型：非null且非空才赋值）
            if (req.getAnswer() != null && !req.getAnswer().isEmpty()) {
                item.setAnswer(req.getAnswer());
            }

            // 解析（字符串类型：非null且非空才赋值）
            if (req.getSolution() != null && !req.getSolution().isEmpty()) {
                item.setSolution(req.getSolution());
            }

            // 5. 更新数据库记录
            int result = itemsMapper.updateById(item);
            log.info("习题资源更新完成，id: {}, 结果: {}", req.getId(), result > 0 ? "成功" : "失败");
            return result > 0;
        } catch (Exception e) {
            log.error("更新习题资源失败，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            throw new RuntimeException("更新习题资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateLAItemStatus(AdminLAItemUpdateStatusDto req) {
        try {
            Item item = itemsMapper.selectById(req.getId());
            if (item == null) {
                log.warn("未找到ID为 {} 的习题资源，无法更新状态", req.getId());
                return false;
            }
            item.setStatus(req.getStatus());
            int result = itemsMapper.updateById(item);

            if (result > 0 && item.getUploadedBy() != null && !item.getUploadedBy().isEmpty()) {
                // 审核结果通知上传者（学习活动-题目）
                resourceAuditNotifier.notifyAuditResult(
                        item.getUploadedBy(),
                        "学习活动-题目",
                        item.getItemKey(),
                        req.getStatus(),
                        null  // 暂无管理员user_key，使用system作为发送者
                );
            }
            return result > 0;

        } catch (Exception e) {
            log.error("更新习题资源状态失败，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteItemById(Integer id) {
        try {
            // 1. 根据ID查询现有的习题资源记录
            Item item = itemsMapper.selectById(id);
            System.out.println("item++"+item);
            if (item == null) {
                log.warn("未找到ID为 {} 的习题资源", id);
                return false; // 习题资源不存在，返回false
            }
            
            // 2. 获取到item中content，options，answer，solution中内容中的所有图片路径
            // 3. 根据获取的图片路径逐一删除该路径下的图片
            deleteImagesFromItem(item);

            // 4. 从数据库中删除记录
            int result = itemsMapper.deleteById(id);
            if (result > 0) {
                log.info("数据库记录删除成功，id: {}", id);
            } else {
                log.warn("数据库记录删除失败，id: {}", id);
            }
            return result > 0;
        } catch (Exception e) {
            log.error("删除习题资源失败，id: {}, 错误: {}", id, e.getMessage(), e);
            return false; // 发生异常，返回false
        }
    }
    
    /**
     * 从习题项中删除所有关联的图片文件
     * @param item 习题项实体
     */
    private void deleteImagesFromItem(Item item) {
        try {
            // 收集所有可能包含图片的字段
            StringBuilder contentBuilder = new StringBuilder();
            if (item.getContent() != null) {
                contentBuilder.append(item.getContent());
            }
            if (item.getOptions() != null) {
                contentBuilder.append(" ").append(item.getOptions());
            }
            if (item.getAnswer() != null) {
                contentBuilder.append(" ").append(item.getAnswer());
            }
            if (item.getSolution() != null) {
                contentBuilder.append(" ").append(item.getSolution());
            }
            
            String fullContent = contentBuilder.toString();
            
            // 提取所有图片路径
            List<String> imagePaths = extractImagePaths(fullContent);
            System.out.println("imagePaths++"+imagePaths);
            
            // 删除图片文件
            for (String imagePath : imagePaths) {
                try {
                    deleteImageFile(imagePath);
                } catch (Exception e) {
                    log.warn("删除图片文件失败: {}, 错误: {}", imagePath, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("处理习题图片删除时发生错误, itemId: {}, 错误: {}", item.getId(), e.getMessage());
        }
    }
    
    /**
     * 从文本中提取图片路径
     * @param content 文本内容
     * @return 图片路径列表
     */
    private List<String> extractImagePaths(String content) {
        List<String> imagePaths = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return imagePaths;
        }
        
        // 匹配图片语法: ![alt](path)
        // 注意：这里使用简单的正则表达式，实际项目中可能需要更复杂的处理
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String imagePath = matcher.group(1);
            // 只处理相对路径（以/开头的路径）
            if (imagePath.startsWith("/items/")) {
                imagePaths.add(imagePath);
            }
        }
        
        return imagePaths;
    }
    
    /**
     * 删除图片文件
     * @param imagePath 图片路径
     */
    private void deleteImageFile(String imagePath) {
        try {
            // 构建完整文件路径
            DirectoryTool tool = new DirectoryTool("items");
            String currentPluginFolderAbsolutePath = tool.findCurrentPluginResorceRootAbsolutePath(baseResourcePath, pluginService.getEnabledPluginKey());
            String fullPath = Paths.get(currentPluginFolderAbsolutePath, imagePath.substring(1)).toString();
            File imageFile = new File(fullPath);
            
            // 检查文件是否存在并删除
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    log.info("成功删除图片文件: {}", fullPath);
                } else {
                    log.warn("无法删除图片文件: {}", fullPath);
                }
            } else {
                log.debug("图片文件不存在: {}", fullPath);
            }
        } catch (Exception e) {
            log.error("删除图片文件时发生错误: {}, 错误: {}", imagePath, e.getMessage());
            throw new RuntimeException("删除图片文件失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String storeImage(MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        Path absolutePath = Paths.get(baseResourcePath).toAbsolutePath().normalize();
        MultipartFileTool imageTool = new MultipartFileTool(
            absolutePath.toString(), // 基础资源路径
            allowedExtensions,                                     // 允许的文件扩展名
            10 * 1024 * 1024,                                      // 最大文件大小10MB
            "image/"                                               // 内容类型前缀
        );

        try{
            log.info("\n--- 验证图片文件 ---");
            log.info("待验证文件: {}", file.getOriginalFilename());
            log.info("文件大小: {} 字节", file.getSize());
            log.info("内容类型: {}", file.getContentType());

            imageTool.validateFile(file);
            log.info("文件验证通过");

            log.info("\n--- 生成唯一图片文件名 ---");
            String originalFileName = file.getOriginalFilename();
            String fileExtension = MultipartFileTool.getFileExtension(originalFileName);
            String uniqueFileName = imageTool.generateUniqueFileName(fileExtension);
            log.info("原始文件名: {}", originalFileName);
            log.info("文件扩展名: {}", fileExtension);
            log.info("生成的唯一文件名: {}", uniqueFileName);
            
            log.info("\n--- 构建目标路径 ---");
            DirectoryTool items = new DirectoryTool("items");
            String targetDirectory = items.findOrCreateTargetDirectory(baseResourcePath);
            Path targetPath = Paths.get(targetDirectory, uniqueFileName);
            log.info("目标目录: {}", targetDirectory);
            log.info("完整目标路径: {}", targetPath.toString());
            
            log.info("\n--- 保存文件 ---");
            imageTool.saveFile(file, targetPath);
            log.info("文件已保存到: {}", targetPath.toString());
            
            log.info("\n--- 计算相对URL ---");
            String relativeUrl = "/items/"+uniqueFileName;
            log.info("相对URL: {}", relativeUrl);
            
            return relativeUrl;

        } catch (Exception e) {
            log.error("图片上传处理失败: ", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }
    }


}
