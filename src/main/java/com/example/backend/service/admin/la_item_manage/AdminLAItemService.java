package com.example.backend.service.admin.la_item_manage;

import com.example.backend.controller.admin.dto.*;
import com.example.backend.entity.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ClassName: AdminLAItemService
 * Package: com.example.backend.service.admin.la_item_manage
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/10 15:40
 * @Version 1.0
 */
public interface AdminLAItemService {
    List<Item> getItemList(AdminLAItemQueryListDto req);

    Long getItemCount(AdminLAItemQueryListDto req);

    boolean addLAItemWithAllRelations(AdminLAItemAddDto req);

    boolean updateLAItemWithAllRelations(AdminLAItemUpdateDto req);

    boolean updateLAItemStatus(AdminLAItemUpdateStatusDto req);

    boolean deleteItemById(Integer id);

    String storeImage(MultipartFile file);
}
