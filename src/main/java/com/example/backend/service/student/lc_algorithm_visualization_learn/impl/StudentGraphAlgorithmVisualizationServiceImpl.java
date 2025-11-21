package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.GraphAlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentGraphAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生端算法可视化学习 - 图算法可视化服务层实现类
 */
@Service
public class StudentGraphAlgorithmVisualizationServiceImpl implements StudentGraphAlgorithmVisualizationService {

    /**
     * 深度优先搜索可视化
     * 对图进行深度优先搜索，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 搜索过程中每一步的状态列表
     */
    @Override
    public List<GraphAlgorithmStep> dfsVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode) {
        List<GraphAlgorithmStep> steps = new ArrayList<>();
        
        if (adjacencyMatrix.length == 0 || startNode < 0 || startNode >= nodes.length) {
            steps.add(new GraphAlgorithmStep(null, Arrays.asList(nodes), new ArrayList<>(), 
                       new ArrayList<>(), new ArrayList<>(), "无效输入参数"));
            return steps;
        }
        
        // 初始化图状态
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   new ArrayList<>(), new ArrayList<>(), "初始化图，起始节点: " + nodes[startNode]));
        
        // DFS遍历
        boolean[] visited = new boolean[nodes.length];
        List<Integer> visitedNodes = new ArrayList<>();
        List<Integer> pointers = new ArrayList<>();
        
        dfsHelper(adjacencyMatrix, nodes, startNode, visited, visitedNodes, pointers, steps);
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(), "DFS遍历完成"));
        
        return steps;
    }
    
    /**
     * 广度优先搜索可视化
     * 对图进行广度优先搜索，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 搜索过程中每一步的状态列表
     */
    @Override
    public List<GraphAlgorithmStep> bfsVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode) {
        List<GraphAlgorithmStep> steps = new ArrayList<>();
        
        if (adjacencyMatrix.length == 0 || startNode < 0 || startNode >= nodes.length) {
            steps.add(new GraphAlgorithmStep(null, Arrays.asList(nodes), new ArrayList<>(), 
                       new ArrayList<>(), new ArrayList<>(), "无效输入参数"));
            return steps;
        }
        
        // 初始化图状态
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   new ArrayList<>(), new ArrayList<>(), "初始化图，起始节点: " + nodes[startNode]));
        
        // BFS遍历
        boolean[] visited = new boolean[nodes.length];
        List<Integer> visitedNodes = new ArrayList<>();
        List<Integer> pointers = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(startNode);
        visited[startNode] = true;
        visitedNodes.add(startNode);
        pointers.add(startNode);
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(pointers), 
                   "访问起始节点 " + nodes[startNode]));
        
        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            pointers.clear();
            pointers.add(currentNode);
            
            steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                       new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(pointers), 
                       "处理节点 " + nodes[currentNode]));
            
            // 遍历当前节点的所有邻居
            for (int i = 0; i < adjacencyMatrix[currentNode].length; i++) {
                if (adjacencyMatrix[currentNode][i] != 0 && !visited[i]) {
                    visited[i] = true;
                    queue.offer(i);
                    visitedNodes.add(i);
                    pointers.add(i);
                    
                    List<GraphAlgorithmStep.Edge> edges = new ArrayList<>();
                    edges.add(new GraphAlgorithmStep.Edge(currentNode, i, adjacencyMatrix[currentNode][i]));
                    
                    steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                               new ArrayList<>(visitedNodes), edges, new ArrayList<>(pointers), 
                               "发现未访问节点 " + nodes[i] + "，加入队列"));
                }
            }
        }
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(), "BFS遍历完成"));
        
        return steps;
    }
    
    /**
     * Dijkstra算法可视化
     * 使用Dijkstra算法寻找最短路径，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @param startNode 起始节点索引
     * @return 算法过程中每一步的状态列表
     */
    @Override
    public List<GraphAlgorithmStep> dijkstraVisualization(int[][] adjacencyMatrix, String[] nodes, int startNode) {
        List<GraphAlgorithmStep> steps = new ArrayList<>();
        
        if (adjacencyMatrix.length == 0 || startNode < 0 || startNode >= nodes.length) {
            steps.add(new GraphAlgorithmStep(null, Arrays.asList(nodes), new ArrayList<>(), 
                       new ArrayList<>(), new ArrayList<>(), "无效输入参数"));
            return steps;
        }
        
        int n = nodes.length;
        
        // 初始化图状态
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   new ArrayList<>(), new ArrayList<>(), "初始化图，起始节点: " + nodes[startNode]));
        
        // Dijkstra算法
        int[] distances = new int[n];
        boolean[] visited = new boolean[n];
        int[] previous = new int[n];
        
        // 初始化距离数组
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);
        distances[startNode] = 0;
        
        List<Integer> visitedNodes = new ArrayList<>();
        List<Integer> pointers = new ArrayList<>();
        
        for (int i = 0; i < n; i++) {
            // 找到未访问节点中距离最小的节点
            int minDistance = Integer.MAX_VALUE;
            int minNode = -1;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j] && distances[j] < minDistance) {
                    minDistance = distances[j];
                    minNode = j;
                }
            }
            
            if (minNode == -1) break;
            
            visited[minNode] = true;
            visitedNodes.add(minNode);
            pointers.clear();
            pointers.add(minNode);
            
            steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                       new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(pointers), 
                       "选择距离最小的未访问节点 " + nodes[minNode] + "，距离为 " + distances[minNode]));
            
            // 更新邻居节点的距离
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[minNode][j] != 0 && !visited[j]) {
                    int newDistance = distances[minNode] + adjacencyMatrix[minNode][j];
                    if (newDistance < distances[j]) {
                        distances[j] = newDistance;
                        previous[j] = minNode;
                        
                        List<GraphAlgorithmStep.Edge> edges = new ArrayList<>();
                        edges.add(new GraphAlgorithmStep.Edge(minNode, j, adjacencyMatrix[minNode][j]));
                        
                        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                                   new ArrayList<>(visitedNodes), edges, new ArrayList<>(pointers), 
                                   "更新节点 " + nodes[j] + " 的距离为 " + newDistance));
                    }
                }
            }
        }
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(), "Dijkstra算法执行完成"));
        
        return steps;
    }
    
    /**
     * Prim算法可视化
     * 使用Prim算法构建最小生成树，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @return 算法过程中每一步的状态列表
     */
    @Override
    public List<GraphAlgorithmStep> primVisualization(int[][] adjacencyMatrix, String[] nodes) {
        List<GraphAlgorithmStep> steps = new ArrayList<>();
        
        if (adjacencyMatrix.length == 0) {
            steps.add(new GraphAlgorithmStep(null, Arrays.asList(nodes), new ArrayList<>(), 
                       new ArrayList<>(), new ArrayList<>(), "图为空"));
            return steps;
        }
        
        int n = nodes.length;
        
        // 初始化图状态
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   new ArrayList<>(), new ArrayList<>(), "初始化图，开始Prim算法"));
        
        // Prim算法
        boolean[] visited = new boolean[n];
        int[] minWeight = new int[n];
        int[] parent = new int[n];
        
        // 初始化
        Arrays.fill(minWeight, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        minWeight[0] = 0;
        
        List<Integer> visitedNodes = new ArrayList<>();
        List<Integer> pointers = new ArrayList<>();
        List<GraphAlgorithmStep.Edge> mstEdges = new ArrayList<>();
        
        for (int i = 0; i < n; i++) {
            // 找到权重最小的未访问节点
            int minEdgeWeight = Integer.MAX_VALUE;
            int minNode = -1;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j] && minWeight[j] < minEdgeWeight) {
                    minEdgeWeight = minWeight[j];
                    minNode = j;
                }
            }
            
            if (minNode == -1) break;
            
            visited[minNode] = true;
            visitedNodes.add(minNode);
            
            if (parent[minNode] != -1) {
                mstEdges.add(new GraphAlgorithmStep.Edge(parent[minNode], minNode, minEdgeWeight));
            }
            
            pointers.clear();
            pointers.add(minNode);
            
            if (parent[minNode] != -1) {
                steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                           new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), new ArrayList<>(pointers), 
                           "将节点 " + nodes[minNode] + " 加入最小生成树，边权重为 " + minEdgeWeight));
            } else {
                steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                           new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), new ArrayList<>(pointers), 
                           "选择起始节点 " + nodes[minNode]));
            }
            
            // 更新邻居节点的最小权重
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[minNode][j] != 0 && !visited[j] && adjacencyMatrix[minNode][j] < minWeight[j]) {
                    minWeight[j] = adjacencyMatrix[minNode][j];
                    parent[j] = minNode;
                    
                    steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                               new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), new ArrayList<>(pointers), 
                               "更新节点 " + nodes[j] + " 的最小边权重为 " + minWeight[j]));
                }
            }
        }
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), new ArrayList<>(), "Prim算法执行完成，最小生成树构建完毕"));
        
        return steps;
    }
    
    /**
     * Kruskal算法可视化
     * 使用Kruskal算法构建最小生成树，并记录整个过程
     *
     * @param adjacencyMatrix 图的邻接矩阵
     * @param nodes 节点列表
     * @return 算法过程中每一步的状态列表
     */
    @Override
    public List<GraphAlgorithmStep> kruskalVisualization(int[][] adjacencyMatrix, String[] nodes) {
        List<GraphAlgorithmStep> steps = new ArrayList<>();
        
        if (adjacencyMatrix.length == 0) {
            steps.add(new GraphAlgorithmStep(null, Arrays.asList(nodes), new ArrayList<>(), 
                       new ArrayList<>(), new ArrayList<>(), "图为空"));
            return steps;
        }
        
        int n = nodes.length;
        
        // 初始化图状态
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   new ArrayList<>(), new ArrayList<>(), "初始化图，开始Kruskal算法"));
        
        // 创建边列表
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(i, j, adjacencyMatrix[i][j]));
                }
            }
        }
        
        // 按权重排序
        edges.sort(Comparator.comparingInt(e -> e.weight));
        
        List<GraphAlgorithmStep.Edge> stepEdges = new ArrayList<>();
        for (Edge e : edges) {
            stepEdges.add(new GraphAlgorithmStep.Edge(e.from, e.to, e.weight));
        }
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), new ArrayList<>(), 
                   stepEdges, new ArrayList<>(), "构建边列表并按权重排序"));
        
        // Kruskal算法
        UnionFind unionFind = new UnionFind(n);
        List<GraphAlgorithmStep.Edge> mstEdges = new ArrayList<>();
        List<Integer> visitedNodes = new ArrayList<>();
        
        for (Edge edge : edges) {
            if (mstEdges.size() == n - 1) break;
            
            int root1 = unionFind.find(edge.from);
            int root2 = unionFind.find(edge.to);
            
            if (root1 != root2) {
                unionFind.union(root1, root2);
                mstEdges.add(new GraphAlgorithmStep.Edge(edge.from, edge.to, edge.weight));
                
                List<GraphAlgorithmStep.Edge> currentEdge = new ArrayList<>();
                currentEdge.add(new GraphAlgorithmStep.Edge(edge.from, edge.to, edge.weight));
                
                // 更新访问节点列表
                if (!visitedNodes.contains(edge.from)) visitedNodes.add(edge.from);
                if (!visitedNodes.contains(edge.to)) visitedNodes.add(edge.to);
                
                steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                           new ArrayList<>(visitedNodes), currentEdge, Arrays.asList(edge.from, edge.to), 
                           "选择边 (" + nodes[edge.from] + ", " + nodes[edge.to] + ")，权重为 " + edge.weight));
            } else {
                List<GraphAlgorithmStep.Edge> currentEdge = new ArrayList<>();
                currentEdge.add(new GraphAlgorithmStep.Edge(edge.from, edge.to, edge.weight));
                
                steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                           new ArrayList<>(visitedNodes), currentEdge, Arrays.asList(edge.from, edge.to), 
                           "跳过边 (" + nodes[edge.from] + ", " + nodes[edge.to] + ")，会形成环"));
            }
        }
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), new ArrayList<>(), "Kruskal算法执行完成，最小生成树构建完毕"));
        
        return steps;
    }

    // 辅助方法
    
    /**
     * DFS辅助方法
     */
    private void dfsHelper(int[][] adjacencyMatrix, String[] nodes, int currentNode, boolean[] visited, 
                          List<Integer> visitedNodes, List<Integer> pointers, List<GraphAlgorithmStep> steps) {
        visited[currentNode] = true;
        visitedNodes.add(currentNode);
        pointers.clear();
        pointers.add(currentNode);
        
        steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                   new ArrayList<>(visitedNodes), new ArrayList<>(), new ArrayList<>(pointers), 
                   "访问节点 " + nodes[currentNode]));
        
        // 遍历当前节点的所有邻居
        for (int i = 0; i < adjacencyMatrix[currentNode].length; i++) {
            if (adjacencyMatrix[currentNode][i] != 0 && !visited[i]) {
                List<GraphAlgorithmStep.Edge> edges = new ArrayList<>();
                edges.add(new GraphAlgorithmStep.Edge(currentNode, i, adjacencyMatrix[currentNode][i]));
                
                steps.add(new GraphAlgorithmStep(copyMatrix(adjacencyMatrix), Arrays.asList(nodes), 
                           new ArrayList<>(visitedNodes), edges, new ArrayList<>(pointers), 
                           "从节点 " + nodes[currentNode] + " 访问邻居节点 " + nodes[i]));
                
                dfsHelper(adjacencyMatrix, nodes, i, visited, visitedNodes, pointers, steps);
            }
        }
    }
    
    /**
     * 复制邻接矩阵
     */
    private int[][] copyMatrix(int[][] matrix) {
        if (matrix == null) return null;
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }
    
    /**
     * 边类
     */
    private static class Edge {
        int from;
        int to;
        int weight;
        
        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    
    /**
     * 并查集类
     */
    private static class UnionFind {
        private int[] parent;
        private int[] rank;
        
        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }
        
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // 路径压缩
            }
            return parent[x];
        }
        
        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX != rootY) {
                // 按秩合并
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }
}