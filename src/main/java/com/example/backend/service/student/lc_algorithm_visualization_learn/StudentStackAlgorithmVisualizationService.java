package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 栈算法可视化服务层接口
 */
public interface StudentStackAlgorithmVisualizationService {

    /**
     * 入栈操作可视化
     * 将指定元素压入栈顶，并记录整个过程
     *
     * @param array 初始栈数组
     * @param value 要入栈的值
     * @return 入栈过程中每一步的状态列表
     */
    List<AlgorithmStep> pushVisualization(int[] array, int value);
    
    /**
     * 出栈操作可视化
     * 从栈顶弹出元素，并记录整个过程
     *
     * @param array 初始栈数组
     * @return 出栈过程中每一步的状态列表
     */
    List<AlgorithmStep> popVisualization(int[] array);
    
    /**
     * 查看栈顶元素可视化
     * 查看栈顶元素但不移除，并记录整个过程
     *
     * @param array 初始栈数组
     * @return 查看栈顶过程中每一步的状态列表
     */
    List<AlgorithmStep> peekVisualization(int[] array);
    
    /**
     * 栈反转可视化
     * 使用栈反转字符串或数组，并记录整个过程
     *
     * @param array 初始数组
     * @return 栈反转过程中每一步的状态列表
     */
    List<AlgorithmStep> reverseVisualization(int[] array);
}