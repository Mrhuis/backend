package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 数组旋转请求DTO
 */
public class RotateArrayRequest {
    private int[] array;
    private int steps;
    
    public int[] getArray() {
        return array;
    }
    
    public void setArray(int[] array) {
        this.array = array;
    }
    
    public int getSteps() {
        return steps;
    }
    
    public void setSteps(int steps) {
        this.steps = steps;
    }
}