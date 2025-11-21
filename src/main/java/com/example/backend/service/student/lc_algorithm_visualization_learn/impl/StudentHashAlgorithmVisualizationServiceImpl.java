package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.ArrayElement;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentHashAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生端算法可视化学习 - 哈希算法可视化服务层实现类
 */
@Service
public class StudentHashAlgorithmVisualizationServiceImpl implements StudentHashAlgorithmVisualizationService {

    // 哈希表的默认大小
    private static final int HASH_TABLE_SIZE = 10;

    /**
     * 插入操作可视化
     * 向哈希表中插入键值对，并记录整个过程
     *
     * @param keys 键数组
     * @param values 值数组
     * @param key 要插入的键
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> insertVisualization(int[] keys, int[] values, int key, int value) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化哈希表状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < HASH_TABLE_SIZE; i++) {
            initialState.add(new ArrayElement(-1, i)); // -1表示空槽位
        }
        
        // 填充已有键值对
        for (int i = 0; i < Math.min(keys.length, values.length); i++) {
            int index = hash(keys[i]);
            initialState.set(index, new ArrayElement(values[i], keys[i]));
        }
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化哈希表，大小为 " + HASH_TABLE_SIZE));
        
        // 计算插入位置
        int index = hash(key);
        List<Integer> pointers = new ArrayList<>();
        pointers.add(index);
        
        steps.add(new AlgorithmStep(initialState, pointers, "计算键 " + key + " 的哈希值，得到索引 " + index));
        
        // 执行插入操作
        List<ArrayElement> newState = new ArrayList<>(initialState);
        newState.set(index, new ArrayElement(value, key));
        
        steps.add(new AlgorithmStep(newState, pointers, "在索引 " + index + " 处插入键值对 (" + key + ", " + value + ")"));
        steps.add(new AlgorithmStep(newState, new ArrayList<>(), "插入操作完成"));
        
        return steps;
    }
    
    /**
     * 查找操作可视化
     * 根据键查找对应的值，并记录整个过程
     *
     * @param keys 键数组
     * @param values 值数组
     * @param key 要查找的键
     * @return 查找过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> searchVisualization(int[] keys, int[] values, int key) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化哈希表状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < HASH_TABLE_SIZE; i++) {
            initialState.add(new ArrayElement(-1, i)); // -1表示空槽位
        }
        
        // 填充已有键值对
        for (int i = 0; i < Math.min(keys.length, values.length); i++) {
            int index = hash(keys[i]);
            initialState.set(index, new ArrayElement(values[i], keys[i]));
        }
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化哈希表，大小为 " + HASH_TABLE_SIZE));
        
        // 计算查找位置
        int index = hash(key);
        List<Integer> pointers = new ArrayList<>();
        pointers.add(index);
        
        steps.add(new AlgorithmStep(initialState, pointers, "计算键 " + key + " 的哈希值，得到索引 " + index));
        
        // 执行查找操作
        ArrayElement element = initialState.get(index);
        if (element.getOriginalIndex() == key) {
            steps.add(new AlgorithmStep(initialState, pointers, "在索引 " + index + " 处找到键 " + key + "，对应的值为 " + element.getValue()));
            steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "查找操作完成，找到值 " + element.getValue()));
        } else {
            steps.add(new AlgorithmStep(initialState, pointers, "在索引 " + index + " 处未找到键 " + key));
            steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "查找操作完成，未找到对应值"));
        }
        
        return steps;
    }
    
    /**
     * 删除操作可视化
     * 删除指定的键值对，并记录整个过程
     *
     * @param keys 键数组
     * @param values 值数组
     * @param key 要删除的键
     * @return 删除过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> deleteVisualization(int[] keys, int[] values, int key) {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // 初始化哈希表状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < HASH_TABLE_SIZE; i++) {
            initialState.add(new ArrayElement(-1, i)); // -1表示空槽位
        }
        
        // 填充已有键值对
        for (int i = 0; i < Math.min(keys.length, values.length); i++) {
            int index = hash(keys[i]);
            initialState.set(index, new ArrayElement(values[i], keys[i]));
        }
        
        steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始化哈希表，大小为 " + HASH_TABLE_SIZE));
        
        // 计算删除位置
        int index = hash(key);
        List<Integer> pointers = new ArrayList<>();
        pointers.add(index);
        
        steps.add(new AlgorithmStep(initialState, pointers, "计算键 " + key + " 的哈希值，得到索引 " + index));
        
        // 执行删除操作
        ArrayElement element = initialState.get(index);
        List<ArrayElement> newState = new ArrayList<>(initialState);
        if (element.getOriginalIndex() == key) {
            newState.set(index, new ArrayElement(-1, index)); // -1表示空槽位
            steps.add(new AlgorithmStep(newState, pointers, "在索引 " + index + " 处找到并删除键 " + key));
            steps.add(new AlgorithmStep(newState, new ArrayList<>(), "删除操作完成"));
        } else {
            steps.add(new AlgorithmStep(initialState, pointers, "在索引 " + index + " 处未找到键 " + key + "，无法删除"));
            steps.add(new AlgorithmStep(initialState, new ArrayList<>(), "删除操作完成，未找到对应键值对"));
        }
        
        return steps;
    }

    /**
     * 简单的哈希函数
     *
     * @param key 键
     * @return 哈希值（索引）
     */
    private int hash(int key) {
        return Math.abs(key) % HASH_TABLE_SIZE;
    }

    /**
     * 将数组转换为字符串表示
     *
     * @param keys 键数组
     * @param values 值数组
     * @return 字符串表示
     */
    private String arrayToString(int[] keys, int[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(keys.length, values.length); i++) {
            sb.append("(").append(keys[i]).append(", ").append(values[i]).append(")");
            if (i < Math.min(keys.length, values.length) - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}