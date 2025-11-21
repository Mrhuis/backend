package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 入队请求DTO
 */
public class EnqueueRequest {
    private int[] array;
    private int value;
    
    public int[] getArray() {
        return array;
    }
    
    public void setArray(int[] array) {
        this.array = array;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}