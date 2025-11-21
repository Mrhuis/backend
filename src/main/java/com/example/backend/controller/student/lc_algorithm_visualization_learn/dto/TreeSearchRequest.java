package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 树查找节点请求DTO
 */
public class TreeSearchRequest {
    private int[] values;
    private int value;
    
    public int[] getValues() {
        return values;
    }
    
    public void setValues(int[] values) {
        this.values = values;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}