package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.PopRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.PeekRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.PushRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.ReverseRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentStackAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 栈算法可视化控制器
 * 提供各种栈算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/stack")
public class StudentStackAlgorithmVisualizationController {

    @Autowired
    private StudentStackAlgorithmVisualizationService stackAlgorithmVisualizationService;

    /**
     * 入栈操作可视化
     * 将指定元素压入栈顶，并记录整个过程
     *
     * @param request 包含初始栈数组和要入栈的值的请求对象
     * @return 入栈过程中每一步的状态列表
     */
    @PostMapping("/push")
    public Result<List<AlgorithmStep>> pushVisualization(@RequestBody PushRequest request) {
        List<AlgorithmStep> result = stackAlgorithmVisualizationService.pushVisualization(request.getArray(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 出栈操作可视化
     * 从栈顶弹出元素，并记录整个过程
     *
     * @param request 包含初始栈数组的请求对象
     * @return 出栈过程中每一步的状态列表
     */
    @PostMapping("/pop")
    public Result<List<AlgorithmStep>> popVisualization(@RequestBody PopRequest request) {
        List<AlgorithmStep> result = stackAlgorithmVisualizationService.popVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 查看栈顶元素可视化
     * 查看栈顶元素但不移除，并记录整个过程
     *
     * @param request 包含初始栈数组的请求对象
     * @return 查看栈顶过程中每一步的状态列表
     */
    @PostMapping("/peek")
    public Result<List<AlgorithmStep>> peekVisualization(@RequestBody PeekRequest request) {
        List<AlgorithmStep> result = stackAlgorithmVisualizationService.peekVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 栈反转可视化
     * 使用栈反转字符串或数组，并记录整个过程
     *
     * @param request 包含初始数组的请求对象
     * @return 栈反转过程中每一步的状态列表
     */
    @PostMapping("/reverse")
    public Result<List<AlgorithmStep>> reverseVisualization(@RequestBody ReverseRequest request) {
        List<AlgorithmStep> result = stackAlgorithmVisualizationService.reverseVisualization(request.getArray());
        return Result.success(result);
    }
}