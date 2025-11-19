package com.example.backend.service.teacher.la_media_manage;

import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaQueryListDto;
import com.example.backend.entity.MediaAssets;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherLAMediaService {
    /**
     * 获取媒体资源列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 媒体资源列表
     */
    List<MediaAssets> getLAMediaList(TeacherLAMediaQueryListDto req);

    /**
     * 获取媒体资源总数
     * @param req 查询条件
     * @return 媒体资源总数
     */
    Long getLAMediasCount(TeacherLAMediaQueryListDto req);

    /**
     * 添加媒体资源
     * @param req 媒体资源信息
     * @return 是否添加成功
     */
    boolean addLAMedia(TeacherLAMediaAddDto req);
    
    /**
     * 教师端添加媒体资源（包含所有关联关系）
     * @param req 媒体资源信息
     * @return 是否添加成功
     */
    boolean addLAMediaWithAllRelations(TeacherLAMediaAddDto req);

    /**
     * 删除媒体资源
     * @param id 媒体资源ID
     * @return 是否删除成功
     */
    boolean deleteLAMediaById(Integer id);
}