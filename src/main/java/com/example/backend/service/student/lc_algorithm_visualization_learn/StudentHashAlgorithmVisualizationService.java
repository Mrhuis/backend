package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 哈希算法可视化服务层接口
 */
public interface StudentHashAlgorithmVisualizationService {

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
    List<AlgorithmStep> insertVisualization(int[] keys, int[] values, int key, int value);
    
    /**
     * 查找操作可视化
     * 根据键查找对应的值，并记录整个过程
     *
     * @param keys 键数组
     * @param values 值数组
     * @param key 要查找的键
     * @return 查找过程中每一步的状态列表
     */
    List<AlgorithmStep> searchVisualization(int[] keys, int[] values, int key);
    
    /**
     * 删除操作可视化
     * 删除指定的键值对，并记录整个过程
     *
     * @param keys 键数组
     * @param values 值数组
     * @param key 要删除的键
     * @return 删除过程中每一步的状态列表
     */
    List<AlgorithmStep> deleteVisualization(int[] keys, int[] values, int key);
}