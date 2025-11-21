package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

/**
 * 数组元素VO类
 * 包含元素值和原始索引位置
 */
public class ArrayElement {
    // 元素值
    private Integer value;
    
    // 该元素在原始数组中的索引位置
    private Integer originalIndex;
    
    public ArrayElement() {
    }
    
    public ArrayElement(Integer value, Integer originalIndex) {
        this.value = value;
        this.originalIndex = originalIndex;
    }
    
    public Integer getValue() {
        return value;
    }
    
    public void setValue(Integer value) {
        this.value = value;
    }
    
    public Integer getOriginalIndex() {
        return originalIndex;
    }
    
    public void setOriginalIndex(Integer originalIndex) {
        this.originalIndex = originalIndex;
    }
}