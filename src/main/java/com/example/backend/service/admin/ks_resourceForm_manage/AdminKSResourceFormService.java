package com.example.backend.service.admin.ks_resourceForm_manage;

import com.example.backend.controller.admin.dto.AdminKSResourceFormAddDto;
import com.example.backend.controller.admin.dto.AdminKSResourceFormQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSResourceFormUpdateDto;
import com.example.backend.entity.ResourceForm;

import java.util.List;

/**
 * ClassName: AdminKSResourceFormService
 * Package: com.example.backend.service.admin.ks_resourceForm_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/2 17:18
 * @Version 1.0
 */
public interface AdminKSResourceFormService {
    List<ResourceForm> getResourceFormList(AdminKSResourceFormQueryListDto req);

    Long getResourceFormCount(AdminKSResourceFormQueryListDto req);

    boolean addResourceForm(AdminKSResourceFormAddDto req);

    boolean updateResourceForm(AdminKSResourceFormUpdateDto req);

    boolean deleteResourceFormById(Integer id);
}
