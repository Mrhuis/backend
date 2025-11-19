package com.example.backend.service.teacher.la_item_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemQueryListDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateStatusDto;
import com.example.backend.entity.Item;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemChapterResourcesService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemKnowledgeResourcesService;
import com.example.backend.service.teacher.la_item_manage.TeacherLAItemTagResourceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeacherLAItemServiceImpl implements TeacherLAItemService {

    @Autowired
    private ItemsMapper itemsMapper;
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private TeacherLAItemChapterResourcesService chapterResourcesService;
    
    @Autowired
    private TeacherLAItemKnowledgeResourcesService knowledgeResourcesService;
    
    @Autowired
    private TeacherLAItemTagResourceService resourceTagService;

    @Override
    public List<Item> getItemList(TeacherLAItemQueryListDto req) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        
        if (req.getItemKey() != null && !req.getItemKey().isEmpty()) {
            queryWrapper.like("item_key", req.getItemKey());
        }
        
        if (req.getContent() != null && !req.getContent().isEmpty()) {
            queryWrapper.like("content", req.getContent());
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<Item> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return itemsMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getItemCount(TeacherLAItemQueryListDto req) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        
        if (req.getItemKey() != null && !req.getItemKey().isEmpty()) {
            queryWrapper.like("item_key", req.getItemKey());
        }
        
        if (req.getContent() != null && !req.getContent().isEmpty()) {
            queryWrapper.like("content", req.getContent());
        }
        
        return itemsMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addItem(TeacherLAItemAddDto req) {
        // 获取当前启用的插件
        Plugin enabledPlugin = pluginService.getEnabledPlugin();
        
        Item item = new Item();
        BeanUtils.copyProperties(req, item);
        item.setItemKey(req.getItemKey());
        item.setPluginKey(enabledPlugin != null ? enabledPlugin.getPluginKey() : "");
        item.setStatus("PENDING"); // 教师端添加的习题默认状态为PENDING
        item.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        return itemsMapper.insert(item) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addItemWithAllRelations(TeacherLAItemAddDto req) {
        try {
            // 1. 添加习题资源到 items 表
            boolean itemResult = addItem(req);
            if (!itemResult) {
                throw new RuntimeException("添加习题资源失败");
            }

            // 2. 添加章节关联到 chapter_resources 表
            boolean chapterResult = chapterResourcesService.addChapterResources(req);
            if (!chapterResult) {
                throw new RuntimeException("添加章节关联失败");
            }

            // 3. 添加知识点关联到 knowledge_resources 表
            boolean knowledgeResult = knowledgeResourcesService.addKnowledgeResources(req);
            if (!knowledgeResult) {
                throw new RuntimeException("添加知识点关联失败");
            }

            // 4. 添加标签关联到 resource_tag 表
            boolean tagResult = resourceTagService.addResourceTag(req);
            if (!tagResult) {
                throw new RuntimeException("添加标签关联失败");
            }

            return true;
        } catch (Exception e) {
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    @Override
    public boolean deleteItemById(Integer id) {
        return itemsMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateItemWithAllRelations(TeacherLAItemUpdateDto req) {
        // 教师端暂时不支持更新功能
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateItemStatus(TeacherLAItemUpdateStatusDto req) {
        // 教师端暂时不支持状态更新
        return false;
    }
    
    @Override
    public String storeImage(MultipartFile file) {
        // 教师端暂时不支持图片存储
        return null;
    }
}