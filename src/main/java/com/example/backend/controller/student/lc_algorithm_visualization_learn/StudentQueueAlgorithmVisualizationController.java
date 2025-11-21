package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.CircularQueueRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.DequeueRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.EnqueueRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.PeekFrontRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentQueueAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 队列算法可视化控制器
 * 提供各种队列算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/queue")
public class StudentQueueAlgorithmVisualizationController {

    @Autowired
    private StudentQueueAlgorithmVisualizationService queueAlgorithmVisualizationService;

    /**
     * 入队操作可视化
     * 将指定元素添加到队尾，并记录整个过程
     *
     * @param request 包含初始队列数组和要入队的值的请求对象
     * @return 入队过程中每一步的状态列表
     */
    @PostMapping("/enqueue")
    public Result<List<AlgorithmStep>> enqueueVisualization(@RequestBody EnqueueRequest request) {
        List<AlgorithmStep> result = queueAlgorithmVisualizationService.enqueueVisualization(request.getArray(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 出队操作可视化
     * 从队首移除元素，并记录整个过程
     *
     * @param request 包含初始队列数组的请求对象
     * @return 出队过程中每一步的状态列表
     */
    @PostMapping("/dequeue")
    public Result<List<AlgorithmStep>> dequeueVisualization(@RequestBody DequeueRequest request) {
        List<AlgorithmStep> result = queueAlgorithmVisualizationService.dequeueVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 查看队首元素可视化
     * 查看队首元素但不移除，并记录整个过程
     *
     * @param request 包含初始队列数组的请求对象
     * @return 查看队首过程中每一步的状态列表
     */
    @PostMapping("/peek-front")
    public Result<List<AlgorithmStep>> peekFrontVisualization(@RequestBody PeekFrontRequest request) {
        List<AlgorithmStep> result = queueAlgorithmVisualizationService.peekFrontVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 循环队列可视化
     * 实现循环队列避免空间浪费，并记录整个过程
     *
     * @param request 包含初始队列数组、要入队的值和队列容量的请求对象
     * @return 循环队列过程中每一步的状态列表
     */
    @PostMapping("/circular-queue")
    public Result<List<AlgorithmStep>> circularQueueVisualization(@RequestBody CircularQueueRequest request) {
        List<AlgorithmStep> result = queueAlgorithmVisualizationService.circularQueueVisualization(
            request.getArray(), request.getValue(), request.getCapacity());
        return Result.success(result);
    }
}