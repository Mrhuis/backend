package com.example.backend.service.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.GraphAlgorithmStep;

import java.util.List;

/**
 * 学生端算法可视化学习 - 图算法可视化服务层接口
 */
public interface StudentGraphAlgorithmVisualizationService {

    /**
     * 深度优先搜索可视化
     * 对图进行深度优先搜索，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 搜索过程中每一步的状态列表
     */
    List<GraphAlgorithmStep> dfsVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode);
    
    /**
     * 广度优先搜索可视化
     * 对图进行广度优先搜索，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 搜索过程中每一步的状态列表
     */
    List<GraphAlgorithmStep> bfsVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode);
    
    /**
     * Dijkstra算法可视化
     * 使用Dijkstra算法寻找最短路径，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 算法过程中每一步的状态列表
     */
    List<GraphAlgorithmStep> dijkstraVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode);
    
    /**
     * Prim算法可视化
     * 使用Prim算法构建最小生成树，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @return 算法过程中每一步的状态列表
     */
    List<GraphAlgorithmStep> primVisualization(int[][] adjacencyMatrix, String[] nodes);
    
    /**
     * Kruskal算法可视化
     * 使用Kruskal算法构建最小生成树，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @return 算法过程中每一步的状态列表
     */
    List<GraphAlgorithmStep> kruskalVisualization(int[][] adjacencyMatrix, String[] nodes);
}