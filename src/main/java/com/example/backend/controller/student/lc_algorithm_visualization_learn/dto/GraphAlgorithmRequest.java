package com.example.backend.controller.student.lc_algorithm_visualization_learn.dto;

import lombok.Data;

@Data
public class GraphAlgorithmRequest {
    private int[][] adjacencyMatrix;
    private String[] nodes;
    private Integer startNode;
}