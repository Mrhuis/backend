package com.example.backend.controller.student.lc_algorithm_visualization_learn.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 图算法步骤VO类
 * 包含每一步的图状态、指针位置和行为描述
 */
public class GraphAlgorithmStep {
    // 图的邻接矩阵状态
    private int[][] adjacencyMatrix;
    
    // 节点列表
    private List<String> nodes;
    
    // 当前访问的节点列表
    private List<Integer> visitedNodes;
    
    // 当前处理的边列表
    private List<Edge> edges;
    
    // 指针位置列表，标记当前步骤中涉及的节点位置
    private List<Integer> pointers;
    
    // 当前步骤的行为描述
    private String action;

    public GraphAlgorithmStep() {
        this.pointers = new ArrayList<>();
        this.visitedNodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    public GraphAlgorithmStep(int[][] adjacencyMatrix, List<String> nodes, List<Integer> visitedNodes, 
                             List<Edge> edges, List<Integer> pointers, String action) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nodes = nodes;
        this.visitedNodes = visitedNodes;
        this.edges = edges;
        this.pointers = pointers;
        this.action = action;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public List<Integer> getVisitedNodes() {
        return visitedNodes;
    }

    public void setVisitedNodes(List<Integer> visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Integer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Integer> pointers) {
        this.pointers = pointers;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * 边VO类
     */
    public static class Edge {
        private int from;
        private int to;
        private int weight;
        
        public Edge() {
        }
        
        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
        
        public int getFrom() {
            return from;
        }
        
        public void setFrom(int from) {
            this.from = from;
        }
        
        public int getTo() {
            return to;
        }
        
        public void setTo(int to) {
            this.to = to;
        }
        
        public int getWeight() {
            return weight;
        }
        
        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}