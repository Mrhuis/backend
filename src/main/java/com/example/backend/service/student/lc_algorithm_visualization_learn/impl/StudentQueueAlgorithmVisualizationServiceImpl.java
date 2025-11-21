package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.ArrayElement;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentQueueAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生端算法可视化学习 - 队列算法可视化服务层实现类
 */
@Service
public class StudentQueueAlgorithmVisualizationServiceImpl implements StudentQueueAlgorithmVisualizationService {

    /**
     * 入队操作可视化
     * 将指定元素添加到队尾，并记录整个过程
     *
     * @param array 初始队列数组
     * @param value 要入队的值
     * @return 入队过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> enqueueVisualization(int[] array, int value) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化队列状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化队列: " + arrayToString(array)));
        
        // 执行入队操作
        List<ArrayElement> newState = new ArrayList<>(initialState);
        newState.add(new ArrayElement(value, array.length));
        List<Integer> pointers = new ArrayList<>();
        pointers.add(array.length);
        steps.add(new AlgorithmStep(newState, pointers, "将元素 " + value + " 添加到队尾"));
        
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "入队操作完成，队尾指针指向位置 " + array.length));
        
        return steps;
    }
    
    /**
     * 出队操作可视化
     * 从队首移除元素，并记录整个过程
     *
     * @param array 初始队列数组
     * @return 出队过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> dequeueVisualization(int[] array) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        if (array.length == 0) {
            steps.add(new AlgorithmStep(new ArrayList<>(), new ArrayList<>(), "队列为空，无法执行出队操作"));
            return steps;
        }
        
        // 初始化队列状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化队列: " + arrayToString(array)));
        
        // 执行出队操作
        int frontValue = array[0];
        List<Integer> pointers = new ArrayList<>();
        pointers.add(0);
        steps.add(new AlgorithmStep(initialState, pointers, "标记队首元素 " + frontValue + "，位置为 0"));
        
        // 移除队首元素
        List<ArrayElement> newState = new ArrayList<>();
        for (int i = 1; i < array.length; i++) {
            newState.add(new ArrayElement(array[i], i - 1));
        }
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "移除队首元素 " + frontValue + "，所有元素向前移动一位"));
        
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "出队操作完成，移除元素为 " + frontValue));
        
        return steps;
    }
    
    /**
     * 查看队首元素可视化
     * 查看队首元素但不移除，并记录整个过程
     *
     * @param array 初始队列数组
     * @return 查看队首过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> peekFrontVisualization(int[] array) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        if (array.length == 0) {
            steps.add(new AlgorithmStep(new ArrayList<>(), new ArrayList<>(), "队列为空，无法查看队首元素"));
            return steps;
        }
        
        // 初始化队列状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化队列: " + arrayToString(array)));
        
        // 查看队首元素
        int frontValue = array[0];
        List<Integer> pointers = new ArrayList<>();
        pointers.add(0);
        steps.add(new AlgorithmStep(initialState, pointers, "查看队首元素，值为 " + frontValue + "，位置为 0"));
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "查看队首操作完成，队首元素为 " + frontValue));
        
        return steps;
    }
    
    /**
     * 循环队列可视化
     * 实现循环队列避免空间浪费，并记录整个过程
     *
     * @param array 初始队列数组
     * @param value 要入队的值
     * @param capacity 队列容量
     * @return 循环队列过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> circularQueueVisualization(int[] array, int value, int capacity) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化队列状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < Math.min(array.length, capacity); i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化循环队列，容量为 " + capacity + "，当前元素: " + arrayToString(array)));
        
        // 检查队列是否已满
        if (array.length >= capacity) {
            steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "队列已满，无法继续入队"));
            return steps;
        }
        
        // 执行入队操作（循环队列方式）
        List<ArrayElement> newState = new ArrayList<>(initialState);
        newState.add(new ArrayElement(value, array.length));
        List<Integer> pointers = new ArrayList<>();
        pointers.add(array.length);
        steps.add(new AlgorithmStep(newState, pointers, "将元素 " + value + " 入队到位置 " + array.length));
        
        // 演示循环特性
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "循环队列操作完成，队列中元素数量: " + (array.length + 1)));
        
        if (array.length + 1 == capacity) {
            steps.add(new AlgorithmStep(newState, new ArrayList<>(), "队列即将满，下次入队将需要使用循环特性"));
        }
        
        return steps;
    }

    /**
     * 将数组转换为字符串表示
     *
     * @param array 数组
     * @return 字符串表示
     */
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 将数组状态转换为字符串表示
     *
     * @param state 数组元素状态列表
     * @return 字符串表示
     */
    private String stateToString(List<ArrayElement> state) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < state.size(); i++) {
            sb.append(state.get(i).getValue());
            if (i < state.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}