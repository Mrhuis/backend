package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.LinkedListAlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 链表算法可视化服务层接口
 */
public interface StudentLinkedListAlgorithmVisualizationService {

    /**
     * 在链表开头插入节点可视化
     * 将输入数组转换为链表结构后，在开头插入指定值，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> insertAtHeadVisualization(int[] array, int value);
    
    /**
     * 在链表末尾插入节点可视化
     * 将输入数组转换为链表结构后，在末尾插入指定值，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> insertAtTailVisualization(int[] array, int value);
    
    /**
     * 删除链表中指定值的节点可视化
     * 将输入数组转换为链表结构后，删除指定值的节点，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要删除的值
     * @return 删除过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> deleteNodeVisualization(int[] array, int value);
    
    /**
     * 反转链表可视化
     * 将输入数组转换为链表结构后，反转链表，并记录整个过程
     *
     * @param array 初始整数数组
     * @return 反转过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> reverseListVisualization(int[] array);
    
    /**
     * 查找链表中间节点可视化
     * 使用快慢指针技术查找链表的中间节点
     *
     * @param array 初始整数数组
     * @return 查找过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> findMiddleNodeVisualization(int[] array);
    
    /**
     * 检测链表中是否存在环可视化
     * 使用快慢指针技术检测链表中是否存在环
     *
     * @param array 初始整数数组
     * @return 检测过程中每一步的状态列表
     */
    List<LinkedListAlgorithmStep> hasCycleVisualization(int[] array);
}