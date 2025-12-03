package com.example.backend.service.admin.ks_resourceForm_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.backend.controller.admin.dto.AdminKSResourceFormAddDto;
import com.example.backend.controller.admin.dto.AdminKSResourceFormQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSResourceFormUpdateDto;
import com.example.backend.entity.Plugin;
import com.example.backend.entity.ResourceForm;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.service.admin.ks_resourceForm_manage.AdminKSResourceFormService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AdminKSResourceFormServiceImpl
 * Package: com.example.backend.service.admin.ks_resourceForm_manage.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/3 8:04
 * @Version 1.0
 */
@Service
public class AdminKSResourceFormServiceImpl implements AdminKSResourceFormService {

    @Autowired
    private ResourceFormMapper resourceFormMapper;

    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;
    @Override
    public List<ResourceForm> getResourceFormList(AdminKSResourceFormQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();

            if (enabledPlugin == null) {
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }

            QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();

            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());

            if (req != null) {
                // 标签类型精确查询
                if (StringUtils.hasText(req.getFormType())) {
                    queryWrapper.eq("form_type", req.getFormType());
                }

                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }

                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("form_name", searchValue)
                            .or()
                            .like("description", searchValue)
                    );
                }
            }
            
            // 按ID升序排列
            queryWrapper.orderByAsc("id");

            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }

            List<ResourceForm> result = resourceFormMapper.selectList(queryWrapper);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取标签列表失败", e);
        }
    }

    @Override
    public Long getResourceFormCount(AdminKSResourceFormQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();

            if (enabledPlugin == null) {
                return 0L; // 如果没有启用的插件，返回0
            }

            QueryWrapper<ResourceForm> queryWrapper = new QueryWrapper<>();

            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());

            if (req != null) {
                // 标签类型精确查询
                if (StringUtils.hasText(req.getFormType())) {
                    queryWrapper.eq("form_type", req.getFormType());
                }

                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }

                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("form_name", searchValue)
                            .or()
                            .like("description", searchValue)
                    );
                }
            }

            Long count = resourceFormMapper.selectCount(queryWrapper);

            return count;
        } catch (Exception e) {
            throw new RuntimeException("获取总数失败", e);
        }
    }

    @Override
    public boolean addResourceForm(AdminKSResourceFormAddDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加标签");
            }

            // 创建标签对象
            ResourceForm resourceForm = new ResourceForm();
            resourceForm.setPluginKey(enabledPlugin.getPluginKey());
            resourceForm.setFormKey(req.getFormKey());
            resourceForm.setFormName(req.getFormName());
            resourceForm.setFormType(req.getFormType());
            resourceForm.setDescription(req.getDescription());
            resourceForm.setUploadedBy(req.getUploadedBy());
            resourceForm.setCreatedAt(LocalDateTime.now());
            resourceForm.setStatus("DISABLED");  // 新添加的标签默认启用

            // 插入数据库
            int result = resourceFormMapper.insert(resourceForm);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加失败", e);
        }
    }

    @Override
    public boolean updateResourceForm(AdminKSResourceFormUpdateDto req) {
        try {
            // 先查询是否存在
            ResourceForm resourceForm = resourceFormMapper.selectById(req.getId());
            if (resourceForm == null) {
                throw new RuntimeException("资源类型不存在");
            }

            // 构建更新条件
            UpdateWrapper<ResourceForm> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", req.getId());

            // 只更新非空/非0的字段
            boolean hasUpdates = false;
            
            if (StringUtils.hasText(req.getFormName())) {
                updateWrapper.set("form_name", req.getFormName());
                hasUpdates = true;
            }
            
            if (StringUtils.hasText(req.getFormType())) {
                updateWrapper.set("form_type", req.getFormType());
                hasUpdates = true;
            }
            
            if (StringUtils.hasText(req.getDescription())) {
                updateWrapper.set("description", req.getDescription());
                hasUpdates = true;
            }
            
            if (StringUtils.hasText(req.getStatus())) {
                updateWrapper.set("status", req.getStatus());
                hasUpdates = true;
            }

            if (!hasUpdates) {
                return false;
            }
            
            // 执行更新
            int result = resourceFormMapper.update(null, updateWrapper);

            // 审核结果通知上传者（资源形式），仅在状态发生变更且存在上传者时发送
            if (result > 0) {
                ResourceForm latest = resourceFormMapper.selectById(req.getId());
                if (latest != null
                        && StringUtils.hasText(latest.getUploadedBy())
                        && StringUtils.hasText(latest.getStatus())) {
                    resourceAuditNotifier.notifyAuditResult(
                            latest.getUploadedBy(),
                            "资源形式",
                            latest.getFormName(),
                            latest.getStatus(),
                            null  // 暂无管理员user_key，使用system作为发送者
                    );
                }
            }

            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新失败", e);
        }
    }

    @Override
    public boolean deleteResourceFormById(Integer id) {
        try {
            // 先查询要删除的标签
            ResourceForm resourceForm = resourceFormMapper.selectById(id);
            if (resourceForm == null) {
                throw new RuntimeException("资源类型不存在");
            }

            // 删除目标资源类型
            int result = resourceFormMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除时发生错误: 请检查学习活动资源是否仍存在依赖该资源类型的资源。", e);
        }
    }
}
