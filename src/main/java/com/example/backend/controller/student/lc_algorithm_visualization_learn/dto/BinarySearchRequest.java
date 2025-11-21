package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 二分搜索请求DTO
 */
public class BinarySearchRequest {
    private int[] array;
    private int target;
    
    public int[] getArray() {
        return array;
    }
    
    public void setArray(int[] array) {
        this.array = array;
    }
    
    public int getTarget() {
        return target;
    }
    
    public void setTarget(int target) {
        this.target = target;
    }
}