package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

/**
 * 链表节点VO类
 * 包含节点值和前后指针指向的节点索引
 */
public class ListNode {
    // 节点值
    private Integer value;

    // 前驱节点索引
    private Integer prevIndex;

    // 后继节点索引
    private Integer nextIndex;

    public ListNode() {
    }

    public ListNode(Integer value, Integer prevIndex, Integer nextIndex) {
        this.value = value;
        this.prevIndex = prevIndex;
        this.nextIndex = nextIndex;
    }

    public ListNode(Integer value, Integer prevIndex) {
        this.value = value;
        this.prevIndex = prevIndex;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getPrevIndex() {
        return prevIndex;
    }

    public void setPrevIndex(Integer prevIndex) {
        this.prevIndex = prevIndex;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }
}