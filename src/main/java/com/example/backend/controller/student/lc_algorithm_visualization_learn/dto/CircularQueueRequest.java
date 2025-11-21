package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 循环队列请求DTO
 */
public class CircularQueueRequest {
    private int[] array;
    private int value;
    private int capacity;
    
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
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}