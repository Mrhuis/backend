package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.GraphAlgorithmRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.GraphAlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentGraphAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端算法可视化学习 - 图算法可视化控制器
 */
@RestController
@RequestMapping("/api/student/graph-algorithm")
public class StudentGraphAlgorithmVisualizationController {

    @Autowired
    private StudentGraphAlgorithmVisualizationService graphAlgorithmVisualizationService;

    /**
     * 深度优先搜索可视化
     *
     * @param request 图算法请求参数
     * @return 搜索过程中每一步的状态列表
     */
    @PostMapping("/dfs")
    public List<GraphAlgorithmStep> dfsVisualization(@RequestBody GraphAlgorithmRequest request) {
        return graphAlgorithmVisualizationService.dfsVisualization(
                request.getAdjacencyMatrix(), request.getNodes(), request.getStartNode());
    }

    /**
     * 广度优先搜索可视化
     *
     * @param request 图算法请求参数
     * @return 搜索过程中每一步的状态列表
     */
    @PostMapping("/bfs")
    public List<GraphAlgorithmStep> bfsVisualization(@RequestBody GraphAlgorithmRequest request) {
        return graphAlgorithmVisualizationService.bfsVisualization(
                request.getAdjacencyMatrix(), request.getNodes(), request.getStartNode());
    }

    /**
     * Dijkstra算法可视化
     *
     * @param request 图算法请求参数
     * @return 算法过程中每一步的状态列表
     */
    @PostMapping("/dijkstra")
    public List<GraphAlgorithmStep> dijkstraVisualization(@RequestBody GraphAlgorithmRequest request) {
        return graphAlgorithmVisualizationService.dijkstraVisualization(
                request.getAdjacencyMatrix(), request.getNodes(), request.getStartNode());
    }

    /**
     * Prim算法可视化
     *
     * @param request 图算法请求参数
     * @return 算法过程中每一步的状态列表
     */
    @PostMapping("/prim")
    public List<GraphAlgorithmStep> primVisualization(@RequestBody GraphAlgorithmRequest request) {
        return graphAlgorithmVisualizationService.primVisualization(
                request.getAdjacencyMatrix(), request.getNodes());
    }

    /**
     * Kruskal算法可视化
     *
     * @param request 图算法请求参数
     * @return 算法过程中每一步的状态列表
     */
    @PostMapping("/kruskal")
    public List<GraphAlgorithmStep> kruskalVisualization(@RequestBody GraphAlgorithmRequest request) {
        return graphAlgorithmVisualizationService.kruskalVisualization(
                request.getAdjacencyMatrix(), request.getNodes());
    }
}