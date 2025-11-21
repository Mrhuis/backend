package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.HashDeleteRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.HashInsertRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.HashSearchRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentHashAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 哈希算法可视化控制器
 * 提供各种哈希算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/hash")
public class StudentHashAlgorithmVisualizationController {

    @Autowired
    private StudentHashAlgorithmVisualizationService hashAlgorithmVisualizationService;

    /**
     * 插入操作可视化
     * 向哈希表中插入键值对，并记录整个过程
     *
     * @param request 包含键数组、值数组、要插入的键和值的请求对象
     * @return 插入过程中每一步的状态列表
     */
    @PostMapping("/insert")
    public Result<List<AlgorithmStep>> insertVisualization(@RequestBody HashInsertRequest request) {
        List<AlgorithmStep> result = hashAlgorithmVisualizationService.insertVisualization(
            request.getKeys(), request.getValues(), request.getKey(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 查找操作可视化
     * 根据键查找对应的值，并记录整个过程
     *
     * @param request 包含键数组、值数组和要查找的键的请求对象
     * @return 查找过程中每一步的状态列表
     */
    @PostMapping("/search")
    public Result<List<AlgorithmStep>> searchVisualization(@RequestBody HashSearchRequest request) {
        List<AlgorithmStep> result = hashAlgorithmVisualizationService.searchVisualization(
            request.getKeys(), request.getValues(), request.getKey());
        return Result.success(result);
    }
    
    /**
     * 删除操作可视化
     * 删除指定的键值对，并记录整个过程
     *
     * @param request 包含键数组、值数组和要删除的键的请求对象
     * @return 删除过程中每一步的状态列表
     */
    @PostMapping("/delete")
    public Result<List<AlgorithmStep>> deleteVisualization(@RequestBody HashDeleteRequest request) {
        List<AlgorithmStep> result = hashAlgorithmVisualizationService.deleteVisualization(
            request.getKeys(), request.getValues(), request.getKey());
        return Result.success(result);
    }
}