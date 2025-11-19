package com.example.backend.service.teacher.la_item_manage;

import com.example.backend.controller.teacher.dto.TeacherLAItemAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemQueryListDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateDto;
import com.example.backend.controller.teacher.dto.TeacherLAItemUpdateStatusDto;
import com.example.backend.entity.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherLAItemService {
    /**
     * 获取习题列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 习题列表
     */
    List<Item> getItemList(TeacherLAItemQueryListDto req);

    /**
     * 获取习题总数
     * @param req 查询条件
     * @return 习题总数
     */
    Long getItemCount(TeacherLAItemQueryListDto req);

    /**
     * 添加习题
     * @param req 习题信息
     * @return 是否添加成功
     */
    boolean addItem(TeacherLAItemAddDto req);
    
    /**
     * 教师端添加习题（包含所有关联关系）
     * @param req 习题信息
     * @return 是否添加成功
     */
    boolean addItemWithAllRelations(TeacherLAItemAddDto req);
    
    /**
     * 更新习题（包含所有关联关系）
     * @param req 习题信息
     * @return 是否更新成功
     */
    boolean updateItemWithAllRelations(TeacherLAItemUpdateDto req);
    
    /**
     * 更新习题状态
     * @param req 状态信息
     * @return 是否更新成功
     */
    boolean updateItemStatus(TeacherLAItemUpdateStatusDto req);

    /**
     * 删除习题
     * @param id 习题ID
     * @return 是否删除成功
     */
    boolean deleteItemById(Integer id);
    
    /**
     * 存储图片文件
     * @param file 图片文件
     * @return 文件访问URL
     */
    String storeImage(MultipartFile file);
}