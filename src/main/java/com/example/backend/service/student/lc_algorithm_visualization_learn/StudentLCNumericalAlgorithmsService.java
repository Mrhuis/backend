package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 数值相关算法服务层接口
 */
public interface StudentLCNumericalAlgorithmsService {

    /**
     * 冒泡排序可视化
     * 返回冒泡排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    List<AlgorithmStep> bubbleSortVisualization(int[] array);
    
    /**
     * 快速排序可视化
     * 返回快速排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    List<AlgorithmStep> quickSortVisualization(int[] array);
    
    /**
     * 归并排序可视化
     * 返回归并排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    List<AlgorithmStep> mergeSortVisualization(int[] array);
    
    /**
     * 插入排序可视化
     * 返回插入排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    List<AlgorithmStep> insertionSortVisualization(int[] array);
    
    /**
     * 数组反转可视化
     * 返回数组反转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待反转的整数数组
     * @return 反转过程中每一步的状态列表
     */
    List<AlgorithmStep> reverseArrayVisualization(int[] array);
    
    /**
     * 线性搜索可视化
     * 返回线性搜索过程中比较元素的下标顺序列表
     *
     * @param array 待搜索的整数数组
     * @param target 搜索目标值
     * @return 搜索过程中每一步的状态列表
     */
    List<AlgorithmStep> linearSearchVisualization(int[] array, int target);
    
    /**
     * 二分搜索可视化
     * 返回二分搜索过程中比较元素的下标顺序列表
     *
     * @param array 待搜索的已排序整数数组
     * @param target 搜索目标值
     * @return 搜索过程中每一步的状态列表
     */
    List<AlgorithmStep> binarySearchVisualization(int[] array, int target);
    
    /**
     * 数组旋转可视化
     * 返回数组按指定步长旋转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待旋转的整数数组
     * @param steps 旋转步长
     * @return 旋转过程中每一步的状态列表
     */
    List<AlgorithmStep> rotateArrayVisualization(int[] array, int steps);
}