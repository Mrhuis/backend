package com.example.backend.service.admin.ks_tag_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.admin.dto.AdminKSTagAddDto;
import com.example.backend.controller.admin.dto.AdminKSTagQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSTagUpdateDto;
import com.example.backend.entity.Plugin;
import com.example.backend.entity.Tag;
import com.example.backend.mapper.TagsMapper;
import com.example.backend.service.admin.ks_tag_manage.AdminKSTagService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AdminKSTagServiceImpl
 * Package: com.example.backend.service.admin.ks_tag_manage.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 11:55
 * @Version 1.0
 */
@Service
public class AdminKSTagServiceImpl implements AdminKSTagService {

    private static final Logger log = LoggerFactory.getLogger(AdminKSTagServiceImpl.class);

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private PluginService pluginService;



    @Override
    public List<Tag> getTagList(AdminKSTagQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取标签列表 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }

            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());

            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();

            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());



            if (req != null) {
                // 标签名模糊查询
                if (StringUtils.hasText(req.getTagContent())) {
                    queryWrapper.like("tag_content", req.getTagContent());
                }



                // 标签类型精确查询
                if (StringUtils.hasText(req.getTagApplicableType())) {
                    queryWrapper.eq("tag_applicable_type", req.getTagApplicableType());
                }


                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }


                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("tag_content", searchValue)
                            .or()
                            .like("tag_desc", searchValue)
                    );
                }
            }

            // 按创建时间倒序排列（如果有created_at字段）
            queryWrapper.orderByDesc("id");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            log.info("执行查询，SQL条件: {}", queryWrapper.getTargetSql());

            List<Tag> result = tagsMapper.selectList(queryWrapper);
            log.info("查询结果数量: {}", result.size());

            return result;
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            throw new RuntimeException("获取标签列表失败", e);
        }
    }

    @Override
    public Long getTagsCount(AdminKSTagQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取标签总数 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回0");
                return 0L; // 如果没有启用的插件，返回0
            }

            // 必须限制为当前启用的插件
            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());

            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();

            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());



            if (req != null) {
                // 标签名模糊查询
                if (StringUtils.hasText(req.getTagContent())) {
                    queryWrapper.like("tag_content", req.getTagContent());
                }



                // 标签类型精确查询
                if (StringUtils.hasText(req.getTagApplicableType())) {
                    queryWrapper.eq("tag_applicable_type", req.getTagApplicableType());
                }


                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }


                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("tag_content", searchValue)
                            .or()
                            .like("tag_desc", searchValue)
                    );
                }
            }

            log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());

            Long count = tagsMapper.selectCount(queryWrapper);
            log.info("查询结果总数: {}", count);

            return count;
        } catch (Exception e) {
            log.error("获取标签总数失败", e);
            throw new RuntimeException("获取标签总数失败", e);
        }
    }

    @Override
    public boolean addTag(AdminKSTagAddDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加标签");
            }

            // 创建标签对象
            Tag tag = new Tag();
            tag.setPluginKey(enabledPlugin.getPluginKey());
            tag.setTagApplicableType(req.getTagApplicableType());
            tag.setTagContent(req.getTagContent());
            tag.setTagDesc(req.getTagDesc());
            tag.setUploadedBy(req.getUploadedBy());
            tag.setCreatedAt(LocalDateTime.now());
            tag.setStatus("DISABLED");  // 新添加的标签默认启用

            // 插入数据库
            int result = tagsMapper.insert(tag);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加标签失败", e);
        }
    }

    @Override
    public boolean updateTag(AdminKSTagUpdateDto req) {
        try {
            // 先查询标签是否存在
            Tag tag = tagsMapper.selectById(req.getId());
            if (tag == null) {
                throw new RuntimeException("标签不存在");
            }

            // 构建更新条件
            UpdateWrapper<Tag> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空/非0的字段
            if (StringUtils.hasText(req.getTagApplicableType())) {
                updateWrapper.set("tag_applicable_type", req.getTagApplicableType());
            }
            if (StringUtils.hasText(req.getTagContent())) {
                updateWrapper.set("tag_content", req.getTagContent());
            }
            if (StringUtils.hasText(req.getTagDesc())) {
                updateWrapper.set("tag_desc", req.getTagDesc());
            }
            if (StringUtils.hasText(req.getStatus())) {
                updateWrapper.set("status", req.getStatus());
            }

            // 执行更新
            int result = tagsMapper.update(null, updateWrapper);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新标签失败", e);
        }
    }

    @Override
    public boolean deleteTagById(Integer id) {
        try {
            // 先查询要删除的标签
            Tag tag = tagsMapper.selectById(id);
            if (tag == null) {
                throw new RuntimeException("标签不存在");
            }

            // 删除目标标签
            int result = tagsMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除标签失败", e);
        }
    }
}
