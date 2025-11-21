package com.example.backend.controller.student.dto;

/**
 * 数组元素DTO
 * 用于算法可视化中表示数组元素及其原始索引
 */
public class ArrayElementDto {
    private int value;  // 数组元素的值
    private int originalIndex;  // 元素在原始数组中的索引

    public ArrayElementDto() {
    }

    public ArrayElementDto(int value, int originalIndex) {
        this.value = value;
        this.originalIndex = originalIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }
}

