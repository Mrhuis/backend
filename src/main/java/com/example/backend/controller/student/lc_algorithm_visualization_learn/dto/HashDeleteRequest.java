package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

/**
 * 哈希表删除请求DTO
 */
public class HashDeleteRequest {
    private int[] keys;
    private int[] values;
    private int key;
    
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
}