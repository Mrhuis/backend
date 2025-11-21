package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 算法步骤VO类
 * 包含每一步的数组状态、指针位置和行为描述
 */
public class AlgorithmStep {
    // 数组状态，使用数组格式保持顺序，每个元素包含值和原始索引
    private List<ArrayElement> arrayState;
    
    // 指针位置列表，标记当前步骤中涉及的元素位置
    private List<Integer> pointers;
    
    // 当前步骤的行为描述
    private String action;
    
    public AlgorithmStep() {
        this.pointers = new ArrayList<>();
        this.arrayState = new ArrayList<>();
    }
    
    public AlgorithmStep(List<ArrayElement> arrayState, List<Integer> pointers, String action) {
        this.arrayState = arrayState;
        this.pointers = pointers;
        this.action = action;
    }
    
    public List<ArrayElement> getArrayState() {
        return arrayState;
    }
    
    public void setArrayState(List<ArrayElement> arrayState) {
        this.arrayState = arrayState;
    }
    
    public List<Integer> getPointers() {
        return pointers;
    }
    
    public void setPointers(List<Integer> pointers) {
        this.pointers = pointers;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
}