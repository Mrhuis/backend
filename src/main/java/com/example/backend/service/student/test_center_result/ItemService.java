package com.example.backend.service.student.test_center_result;

import com.example.backend.entity.Item;

/**
 * 学生测试中心结果-题目服务接口
 */
public interface ItemService {
    /**
     * 根据item_key查询题目数据
     *
     * @param itemKey 习题标识
     * @return 题目数据
     */
    Item getItemByItemKey(String itemKey);
}