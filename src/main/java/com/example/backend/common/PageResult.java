package com.example.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 当前页的数据列表
     */
    private List<T> records;

    /**
     * 数据总条数
     */
    private long total;

    /**
     * 当前页码（从1开始）
     */
    private long page;

    /**
     * 每页条数
     */
    private long size;
}

