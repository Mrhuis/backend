package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 链表算法步骤VO类
 * 包含每一步的链表状态、指针位置和行为描述
 */
public class LinkedListAlgorithmStep {
    // 链表状态，使用列表格式保存所有节点
    private List<ListNode> listState;

    // 指针位置列表，标记当前步骤中涉及的节点位置
    private List<Integer> pointers;

    // 当前步骤的行为描述
    private String action;

    public LinkedListAlgorithmStep() {
        this.pointers = new ArrayList<>();
        this.listState = new ArrayList<>();
    }

    public LinkedListAlgorithmStep(List<ListNode> listState, List<Integer> pointers, String action) {
        this.listState = listState;
        this.pointers = pointers;
        this.action = action;
    }

    public List<ListNode> getListState() {
        return listState;
    }

    public void setListState(List<ListNode> listState) {
        this.listState = listState;
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