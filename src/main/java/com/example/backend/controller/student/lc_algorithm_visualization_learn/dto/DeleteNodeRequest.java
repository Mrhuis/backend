package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 删除链表节点请求DTO
 */
public class DeleteNodeRequest {
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