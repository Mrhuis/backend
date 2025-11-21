package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 哈希表插入请求DTO
 */
public class HashInsertRequest {
    private int[] keys;
    private int[] values;
    private int key;
    private int value;
    
    public int[] getKeys() {
        return keys;
    }
    
    public void setKeys(int[] keys) {
        this.keys = keys;
    }
    
    public int[] getValues() {
        return values;
    }
    
    public void setValues(int[] values) {
        this.values = values;
    }
    
    public int getKey() {
        return key;
    }
    
    public void setKey(int key) {
        this.key = key;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}