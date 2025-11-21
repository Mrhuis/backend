package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 队列算法可视化服务层接口
 */
public interface StudentQueueAlgorithmVisualizationService {

    /**
     * 入队操作可视化
     * 将指定元素添加到队尾，并记录整个过程
     *
     * @param array 初始队列数组
     * @param value 要入队的值
     * @return 入队过程中每一步的状态列表
     */
    List<AlgorithmStep> enqueueVisualization(int[] array, int value);
    
    /**
     * 出队操作可视化
     * 从队首移除元素，并记录整个过程
     *
     * @param array 初始队列数组
     * @return 出队过程中每一步的状态列表
     */
    List<AlgorithmStep> dequeueVisualization(int[] array);
    
    /**
     * 查看队首元素可视化
     * 查看队首元素但不移除，并记录整个过程
     *
     * @param array 初始队列数组
     * @return 查看队首过程中每一步的状态列表
     */
    List<AlgorithmStep> peekFrontVisualization(int[] array);
    
    /**
     * 循环队列可视化
     * 实现循环队列避免空间浪费，并记录整个过程
     *
     * @param array 初始队列数组
     * @param value 要入队的值
     * @param capacity 队列容量
     * @return 循环队列过程中每一步的状态列表
     */
    List<AlgorithmStep> circularQueueVisualization(int[] array, int value, int capacity);
}