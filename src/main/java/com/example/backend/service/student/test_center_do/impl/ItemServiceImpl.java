package com.example.backend.service.student.test_center_do.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.Item;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.service.student.test_center_do.ItemService;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemsMapper, Item> implements ItemService {

    private final ItemsMapper itemsMapper;

    public ItemServiceImpl(ItemsMapper itemsMapper) {
        this.itemsMapper = itemsMapper;
    }

    @Override
    public Item getItemByItemKey(String itemKey) {
        return itemsMapper.selectByItemKey(itemKey);
    }
}