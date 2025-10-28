package com.example.backend.service.admin.resource_manage;

import com.example.backend.entity.Plugin;
import com.example.backend.controller.admin.dto.AdminPluginsQueryListDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PluginService {

    void updatePluginStatus(Long id, String status);
    
    // 新增：智能更新插件状态（确保最多只有一个插件启用）
    void updatePluginStatusSmart(Long id, String status);
    
    // 新增：获取当前启用的插件
    Plugin getEnabledPlugin();

    String getEnabledPluginKey();
    Plugin getPluginByKey(String pluginKey);

    // 修改方法：支持分页和查询条件
    List<Plugin> getPluginsList(AdminPluginsQueryListDto queryDto);
    
    // 新增：获取插件总数（用于分页）
    Long getPluginsCount(AdminPluginsQueryListDto queryDto);

    Resource downloadPlugin(String pluginKey);
    
    // 新增的管理员功能方法
    Plugin getPluginById(Long id);



    
    // 新增：完整的插件删除方法
    boolean deletePluginCompletely(Long id);
}
