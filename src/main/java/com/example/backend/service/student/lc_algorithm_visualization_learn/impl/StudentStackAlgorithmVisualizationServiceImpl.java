package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.ArrayElement;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentStackAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生端算法可视化学习 - 栈算法可视化服务层实现类
 */
@Service
public class StudentStackAlgorithmVisualizationServiceImpl implements StudentStackAlgorithmVisualizationService {

    /**
     * 入栈操作可视化
     * 将指定元素压入栈顶，并记录整个过程
     *
     * @param array 初始栈数组
     * @param value 要入栈的值
     * @return 入栈过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> pushVisualization(int[] array, int value) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化栈状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化栈: " + arrayToString(array)));
        
        // 执行入栈操作
        List<ArrayElement> newState = new ArrayList<>(initialState);
        newState.add(new ArrayElement(value, array.length));
        List<Integer> pointers = new ArrayList<>();
        pointers.add(array.length);
        steps.add(new AlgorithmStep(newState, pointers, "将元素 " + value + " 压入栈顶"));
        
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "入栈操作完成，栈顶指针指向位置 " + array.length));
        
        return steps;
    }
    
    /**
     * 出栈操作可视化
     * 从栈顶弹出元素，并记录整个过程
     *
     * @param array 初始栈数组
     * @return 出栈过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> popVisualization(int[] array) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        if (array.length == 0) {
            steps.add(new AlgorithmStep(new ArrayList<>(), new ArrayList<>(), "栈为空，无法执行出栈操作"));
            return steps;
        }
        
        // 初始化栈状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化栈: " + arrayToString(array)));
        
        // 执行出栈操作
        int topIndex = array.length - 1;
        int poppedValue = array[topIndex];
        List<Integer> pointers = new ArrayList<>();
        pointers.add(topIndex);
        steps.add(new AlgorithmStep(initialState, pointers, "标记栈顶元素 " + poppedValue + "，位置为 " + topIndex));
        
        // 移除栈顶元素
        List<ArrayElement> newState = new ArrayList<>(initialState.subList(0, topIndex));
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "移除栈顶元素 " + poppedValue + "，新栈顶指针指向位置 " + (topIndex - 1)));
        
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "出栈操作完成，弹出元素为 " + poppedValue));
        
        return steps;
    }
    
    /**
     * 查看栈顶元素可视化
     * 查看栈顶元素但不移除，并记录整个过程
     *
     * @param array 初始栈数组
     * @return 查看栈顶过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> peekVisualization(int[] array) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        if (array.length == 0) {
            steps.add(new AlgorithmStep(new ArrayList<>(), new ArrayList<>(), "栈为空，无法查看栈顶元素"));
            return steps;
        }
        
        // 初始化栈状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化栈: " + arrayToString(array)));
        
        // 查看栈顶元素
        int topIndex = array.length - 1;
        int topValue = array[topIndex];
        List<Integer> pointers = new ArrayList<>();
        pointers.add(topIndex);
        steps.add(new AlgorithmStep(initialState, pointers, "查看栈顶元素，值为 " + topValue + "，位置为 " + topIndex));
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "查看栈顶操作完成，栈顶元素为 " + topValue));
        
        return steps;
    }
    
    /**
     * 栈反转可视化
     * 使用栈反转字符串或数组，并记录整个过程
     *
     * @param array 初始数组
     * @return 栈反转过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> reverseVisualization(int[] array) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        if (array.length == 0) {
            steps.add(new AlgorithmStep(new ArrayList<>(), new ArrayList<>(), "数组为空，无需反转"));
            return steps;
        }
        
        // 初始化数组状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], i));
        }
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化数组: " + arrayToString(array)));
        
        // 使用栈进行反转 - 第一步：将所有元素入栈
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "开始使用栈进行数组反转"));
        
        Deque<Integer> stack = new ArrayDeque<>();
        List<ArrayElement> stackVisualization = new ArrayList<>();
        
        // 模拟入栈过程，从数组的第一个元素开始
        for (int i = 0; i < array.length; i++) {
            stack.push(array[i]);
            // 更新栈的可视化状态（栈顶在前）
            stackVisualization.add(0, new ArrayElement(array[i], i));
            List<Integer> pointers = new ArrayList<>();
            pointers.add(i);
            steps.add(new AlgorithmStep(new ArrayList<>(stackVisualization), pointers, "将元素 " + array[i] + " 入栈，当前栈状态: " + stackToString(stack)));
        }
        
        steps.add(new AlgorithmStep(new ArrayList<>(stackVisualization), new ArrayList<>(), "所有元素入栈完成，当前栈状态: " + stackToString(stack)));
        
        // 模拟出栈过程，构建反转后的数组
        List<ArrayElement> reversedState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            int poppedValue = stack.pop();
            reversedState.add(new ArrayElement(poppedValue, i));
            List<Integer> pointers = new ArrayList<>();
            pointers.add(i);
            
            // 更新栈的可视化状态
            stackVisualization.remove(0); // 移除栈顶元素
            
            steps.add(new AlgorithmStep(new ArrayList<>(stackVisualization), new ArrayList<>(), 
                       "将元素 " + poppedValue + " 出栈，当前栈状态: " + (stack.isEmpty() ? "空" : stackToString(stack))));
            steps.add(new AlgorithmStep(new ArrayList<>(reversedState), pointers, 
                       "将元素 " + poppedValue + " 添加到结果数组位置 " + i));
        }
        
        steps.add(new AlgorithmStep(reversedState, new ArrayList<>(), "栈反转操作完成，原数组: " + arrayToString(array) + "，反转后数组: " + stateToString(reversedState)));
        
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
     * 将栈转换为字符串表示
     *
     * @param stack 栈
     * @return 字符串表示
     */
    private String stackToString(Deque<Integer> stack) {
        if (stack.isEmpty()) {
            return "空";
        }
        StringBuilder sb = new StringBuilder("[");
        Iterator<Integer> iterator = stack.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("] 栈顶");
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