package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 检测链表中是否存在环请求DTO
 */
public class HasCycleRequest {
    private int[] array;

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }
}