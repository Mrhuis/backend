package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.TreeAlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 树算法可视化服务层接口
 */
public interface StudentTreeAlgorithmVisualizationService {

    /**
     * 前序遍历可视化
     * 对二叉树进行前序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> preorderTraversalVisualization(int[] values);
    
    /**
     * 中序遍历可视化
     * 对二叉树进行中序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> inorderTraversalVisualization(int[] values);
    
    /**
     * 后序遍历可视化
     * 对二叉树进行后序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> postorderTraversalVisualization(int[] values);
    
    /**
     * 层序遍历可视化
     * 对二叉树进行层序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> levelOrderTraversalVisualization(int[] values);
    
    /**
     * 插入节点可视化
     * 向二叉搜索树中插入节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> insertNodeVisualization(int[] values, int value);
    
    /**
     * 删除节点可视化
     * 从二叉搜索树中删除节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要删除的值
     * @return 删除过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> deleteNodeVisualization(int[] values, int value);
    
    /**
     * 查找节点可视化
     * 在二叉搜索树中查找节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要查找的值
     * @return 查找过程中每一步的状态列表
     */
    List<TreeAlgorithmStep> searchNodeVisualization(int[] values, int value);
}