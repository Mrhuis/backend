package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.BinarySearchRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.LinearSearchRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.RotateArrayRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentLCNumericalAlgorithmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 数值相关算法控制器
 * 提供各种数值算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/numerical")
public class StudentLCNumericalAlgorithmsController {

    @Autowired
    private StudentLCNumericalAlgorithmsService numericalAlgorithmsService;

    /**
     * 冒泡排序可视化
     * 返回冒泡排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @PostMapping("/bubble-sort")
    public Result<List<AlgorithmStep>> bubbleSortVisualization(@RequestBody int[] array) {
        List<AlgorithmStep> result = numericalAlgorithmsService.bubbleSortVisualization(array);
        return Result.success(result);
    }
    
    /**
     * 快速排序可视化
     * 返回快速排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @PostMapping("/quick-sort")
    public Result<List<AlgorithmStep>> quickSortVisualization(@RequestBody int[] array) {
        List<AlgorithmStep> result = numericalAlgorithmsService.quickSortVisualization(array);
        return Result.success(result);
    }
    
    /**
     * 归并排序可视化
     * 返回归并排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @PostMapping("/merge-sort")
    public Result<List<AlgorithmStep>> mergeSortVisualization(@RequestBody int[] array) {
        List<AlgorithmStep> result = numericalAlgorithmsService.mergeSortVisualization(array);
        return Result.success(result);
    }
    
    /**
     * 插入排序可视化
     * 返回插入排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @PostMapping("/insertion-sort")
    public Result<List<AlgorithmStep>> insertionSortVisualization(@RequestBody int[] array) {
        List<AlgorithmStep> result = numericalAlgorithmsService.insertionSortVisualization(array);
        return Result.success(result);
    }
    
    /**
     * 数组反转可视化
     * 返回数组反转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待反转的整数数组
     * @return 反转过程中每一步的状态列表
     */
    @PostMapping("/reverse-array")
    public Result<List<AlgorithmStep>> reverseArrayVisualization(@RequestBody int[] array) {
        List<AlgorithmStep> result = numericalAlgorithmsService.reverseArrayVisualization(array);
        return Result.success(result);
    }
    
    /**
     * 线性搜索可视化
     * 返回线性搜索过程中比较元素的下标顺序列表
     *
     * @param request 包含待搜索数组和目标值的请求对象
     * @return 搜索过程中比较元素的下标顺序列表
     */
    @PostMapping("/linear-search")
    public Result<List<AlgorithmStep>> linearSearchVisualization(@RequestBody LinearSearchRequest request) {
        List<AlgorithmStep> result = numericalAlgorithmsService.linearSearchVisualization(request.getArray(), request.getTarget());
        return Result.success(result);
    }
    
    /**
     * 二分搜索可视化
     * 返回二分搜索过程中比较元素的下标顺序列表
     *
     * @param request 包含待搜索数组和目标值的请求对象
     * @return 搜索过程中比较元素的下标顺序列表
     */
    @PostMapping("/binary-search")
    public Result<List<AlgorithmStep>> binarySearchVisualization(@RequestBody BinarySearchRequest request) {
        List<AlgorithmStep> result = numericalAlgorithmsService.binarySearchVisualization(request.getArray(), request.getTarget());
        return Result.success(result);
    }
    
    /**
     * 数组旋转可视化
     * 返回数组按指定步长旋转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param request 包含待旋转数组和步长的请求对象
     * @return 旋转过程中每一步的状态列表
     */
    @PostMapping("/rotate-array")
    public Result<List<AlgorithmStep>> rotateArrayVisualization(@RequestBody RotateArrayRequest request) {
        List<AlgorithmStep> result = numericalAlgorithmsService.rotateArrayVisualization(request.getArray(), request.getSteps());
        return Result.success(result);
    }
}