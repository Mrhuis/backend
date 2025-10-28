package com.example.backend.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: QueryListVo
 * Package: com.example.backend.common.vo
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/9 16:49
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryListVo {
    private List<Object> records; // 数据列表
    private Long total; // 总记录数
    private Integer pages; // 总页数
    private Integer current; // 当前页
    private Integer size; // 每页大小
}
