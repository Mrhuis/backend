package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 * 红黑树节点VO类
 */
public class RedBlackNode {
    // 节点唯一标识（使用数组下标）
    private Integer id;
    
    // 节点值
    private Integer val;
    
    // 左子节点
    private RedBlackNode left;
    
    // 右子节点
    private RedBlackNode right;
    
    // 父节点
    private RedBlackNode parent;
    
    // 节点颜色（红/黑）
    private Boolean isRed;
    
    // 节点在树中的位置标识
    private String position;

    public RedBlackNode() {
    }

    public RedBlackNode(Integer id, Integer val) {
        this.id = id;
        this.val = val;
    }

    public RedBlackNode(Integer val, RedBlackNode left, RedBlackNode right, RedBlackNode parent) {
        this.val = val;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    @JsonIgnore
    public RedBlackNode getLeft() {
        return left;
    }

    public void setLeft(RedBlackNode left) {
        this.left = left;
    }

    @JsonIgnore
    public RedBlackNode getRight() {
        return right;
    }

    public void setRight(RedBlackNode right) {
        this.right = right;
    }

    @JsonIgnore
    public RedBlackNode getParent() {
        return parent;
    }

    public void setParent(RedBlackNode parent) {
        this.parent = parent;
    }

    public Boolean getRed() {
        return isRed;
    }

    public void setRed(Boolean red) {
        isRed = red;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedBlackNode that = (RedBlackNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}