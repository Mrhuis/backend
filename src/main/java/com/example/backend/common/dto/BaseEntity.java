package com.example.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @program: em-api
 * @description: 基类
 * @author: xjq
 * @create: 2024-01-22 15:40
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity {
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex == null ? 1 : pageIndex;
        setOffset(null);
    }


    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize == null ? 100 : pageSize;
        setOffset(null);
    }

    public void setOffset(Integer offset) {
        // 数据库分页参数， sql: limit offset,pageSize
        if(offset == null){
            this.offset = (this.pageIndex - 1) * this.pageSize;
        }else {
            this.offset = offset;
        }
    }

    public BaseEntity(){
        setOffset(null);
    }

    /**
     * 当前页数
     */
    @JsonAlias({"page_index", "pageIndex"})
//    @JsonProperty("page_index")
    private Integer pageIndex = 1;
    /**
     * 每页数量
     */
    @JsonAlias({"page_size", "pageSize"})
//    @JsonProperty("page_size")
    private Integer pageSize = 100;
    /**
     * 数据库分页
     */
    private Integer offset = 0;
    /**
     * 模糊搜索值
     */
    @JsonAlias({"search_value", "searchValue"})
//    @JsonProperty("search_value")
    private String searchValue;
}
