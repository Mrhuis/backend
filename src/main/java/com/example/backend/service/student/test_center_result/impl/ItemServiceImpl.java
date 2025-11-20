package com.example.backend.service.student.test_center_result.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.Item;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.service.student.test_center_result.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 学生测试中心结果-题目服务实现类
 */
@Service("testCenterResultItemService")
public class ItemServiceImpl extends ServiceImpl<ItemsMapper, Item> implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemsMapper itemsMapper;

    public ItemServiceImpl(ItemsMapper itemsMapper) {
        this.itemsMapper = itemsMapper;
    }

    @Override
    public Item getItemByItemKey(String itemKey) {
        try {
            log.info("开始查询题目，itemKey={}", itemKey);
            Item item = itemsMapper.selectByItemKey(itemKey);
            log.info("查询题目{}成功", item != null ? "成功" : "未找到");
            return item;
        } catch (Exception e) {
            log.error("查询题目失败，itemKey={}", itemKey, e);
            throw new RuntimeException("查询题目数据失败", e);
        }
    }
}