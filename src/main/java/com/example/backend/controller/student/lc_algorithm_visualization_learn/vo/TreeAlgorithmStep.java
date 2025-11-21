package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 树算法步骤VO类
 * 包含每一步的树状态、指针位置和行为描述
 */
public class TreeAlgorithmStep {
    // 树状态，使用列表格式保存所有节点
    private List<RedBlackNode> treeState;

    // 指针位置列表，标记当前步骤中涉及的节点位置
    private List<String> pointers;

    // 当前步骤的行为描述
    private String action;

    public TreeAlgorithmStep() {
        this.pointers = new ArrayList<>();
        this.treeState = new ArrayList<>();
    }

    public TreeAlgorithmStep(List<RedBlackNode> treeState, List<String> pointers, String action) {
        this.treeState = treeState;
        this.pointers = pointers;
        this.action = action;
    }

    public List<RedBlackNode> getTreeState() {
        return treeState;
    }

    public void setTreeState(List<RedBlackNode> treeState) {
        this.treeState = treeState;
    }

    public List<String> getPointers() {
        return pointers;
    }

    public void setPointers(List<String> pointers) {
        this.pointers = pointers;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}