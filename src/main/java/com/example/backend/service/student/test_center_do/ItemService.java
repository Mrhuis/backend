package com.example.backend.service.student.test_center_do;

import com.example.backend.entity.Item;

public interface ItemService {
    /**
     * 根据item_key查询题目数据
     *
     * @param itemKey 习题标识
     * @return 题目数据
     */
    Item getItemByItemKey(String itemKey);
}