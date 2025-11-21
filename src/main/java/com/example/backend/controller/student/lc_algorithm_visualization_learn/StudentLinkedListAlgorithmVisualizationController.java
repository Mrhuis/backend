package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.*;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.LinkedListAlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentLinkedListAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 链表算法可视化控制器
 * 提供各种链表算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/linked-list")
public class StudentLinkedListAlgorithmVisualizationController {

    @Autowired
    private StudentLinkedListAlgorithmVisualizationService linkedListAlgorithmVisualizationService;

    /**
     * 在链表开头插入节点可视化
     * 将输入数组转换为链表结构后，在开头插入指定值，并记录整个过程
     *
     * @param request 包含初始数组和要插入的值的请求对象
     * @return 插入过程中每一步的状态列表
     */
    @PostMapping("/insert-at-head")
    public Result<List<LinkedListAlgorithmStep>> insertAtHeadVisualization(@RequestBody InsertAtHeadRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.insertAtHeadVisualization(request.getArray(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 在链表末尾插入节点可视化
     * 将输入数组转换为链表结构后，在末尾插入指定值，并记录整个过程
     *
     * @param request 包含初始数组和要插入的值的请求对象
     * @return 插入过程中每一步的状态列表
     */
    @PostMapping("/insert-at-tail")
    public Result<List<LinkedListAlgorithmStep>> insertAtTailVisualization(@RequestBody InsertAtTailRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.insertAtTailVisualization(request.getArray(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 删除链表中指定值的节点可视化
     * 将输入数组转换为链表结构后，删除指定值的节点，并记录整个过程
     *
     * @param request 包含初始数组和要删除的值的请求对象
     * @return 删除过程中每一步的状态列表
     */
    @PostMapping("/delete-node")
    public Result<List<LinkedListAlgorithmStep>> deleteNodeVisualization(@RequestBody DeleteNodeRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.deleteNodeVisualization(request.getArray(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 反转链表可视化
     * 将输入数组转换为链表结构后，反转链表，并记录整个过程
     *
     * @param request 包含初始数组的请求对象
     * @return 反转过程中每一步的状态列表
     */
    @PostMapping("/reverse-list")
    public Result<List<LinkedListAlgorithmStep>> reverseListVisualization(@RequestBody ReverseListRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.reverseListVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 查找链表中间节点可视化
     * 使用快慢指针技术查找链表的中间节点
     *
     * @param request 包含初始数组的请求对象
     * @return 查找过程中每一步的状态列表
     */
    @PostMapping("/find-middle-node")
    public Result<List<LinkedListAlgorithmStep>> findMiddleNodeVisualization(@RequestBody FindMiddleNodeRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.findMiddleNodeVisualization(request.getArray());
        return Result.success(result);
    }
    
    /**
     * 检测链表中是否存在环可视化
     * 使用快慢指针技术检测链表中是否存在环
     *
     * @param request 包含初始数组的请求对象
     * @return 检测过程中每一步的状态列表
     */
    @PostMapping("/has-cycle")
    public Result<List<LinkedListAlgorithmStep>> hasCycleVisualization(@RequestBody HasCycleRequest request) {
        List<LinkedListAlgorithmStep> result = linkedListAlgorithmVisualizationService.hasCycleVisualization(request.getArray());
        return Result.success(result);
    }
}