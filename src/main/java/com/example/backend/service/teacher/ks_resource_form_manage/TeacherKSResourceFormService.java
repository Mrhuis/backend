package com.example.backend.service.teacher.ks_resource_form_manage;

import com.example.backend.controller.teacher.dto.TeacherKSResourceFormAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSResourceFormQueryListDto;
import com.example.backend.entity.ResourceForm;

import java.util.List;

public interface TeacherKSResourceFormService {
    /**
     * 获取资源形式列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 资源形式列表
     */
    List<ResourceForm> getResourceFormList(TeacherKSResourceFormQueryListDto req);

    /**
     * 获取资源形式总数
     * @param req 查询条件
     * @return 资源形式总数
     */
    Long getResourceFormCount(TeacherKSResourceFormQueryListDto req);

    /**
     * 添加资源形式
     * @param req 资源形式信息
     * @return 是否添加成功
     */
    boolean addResourceForm(TeacherKSResourceFormAddDto req);

    /**
     * 删除资源形式
     * @param id 资源形式ID
     * @return 是否删除成功
     */
    boolean deleteResourceFormById(Integer id);
}