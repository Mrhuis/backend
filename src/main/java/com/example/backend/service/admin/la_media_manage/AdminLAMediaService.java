package com.example.backend.service.admin.la_media_manage;

import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaQueryListDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;
import com.example.backend.entity.MediaAssets;

import java.util.List;

/**
 * ClassName: AdminLAMediaService
 * Package: com.example.backend.service.admin.la_media_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 11:42
 * @Version 1.0
 */
public interface AdminLAMediaService {
    List<MediaAssets> getLAMediaList(AdminLAMediaQueryListDto req);

    Long getLAMediasCount(AdminLAMediaQueryListDto req);

    boolean addLAMedia(AdminLAMediaAddDto req);

    boolean updateLAMedia(AdminLAMediaUpdateDto req);

    boolean deleteLAMediaById(Integer id);
    
    /**
     * 事务性添加视频资源（包含媒体资源、章节关联、知识点关联、标签关联）
     * @param req 添加请求
     * @return 是否成功
     */
    boolean addLAMediaWithAllRelations(AdminLAMediaAddDto req);
    
    /**
     * 事务性更新视频资源（包含媒体资源、章节关联、知识点关联、标签关联）
     * @param req 更新请求
     * @return 是否成功
     */
    boolean updateLAMediaWithAllRelations(AdminLAMediaUpdateDto req);

    boolean updateLAMediaStatus(Long id, String status);
}
