package com.example.backend.controller.student.lc_algorithm_visualization_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.TreeDeleteRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.TreeInsertRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.TreeSearchRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.dto.TreeTraversalRequest;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.TreeAlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentTreeAlgorithmVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端算法可视化学习 - 树算法可视化控制器
 * 提供各种树算法的可视化过程展示
 */
@RestController
@RequestMapping("/api/student/algorithm-visualization/tree")
public class StudentTreeAlgorithmVisualizationController {

    @Autowired
    private StudentTreeAlgorithmVisualizationService treeAlgorithmVisualizationService;

    /**
     * 前序遍历可视化
     * 对二叉树进行前序遍历，并记录整个过程
     *
     * @param request 包含节点值数组的请求对象
     * @return 遍历过程中每一步的状态列表
     */
    @PostMapping("/preorder-traversal")
    public Result<List<TreeAlgorithmStep>> preorderTraversalVisualization(@RequestBody TreeTraversalRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.preorderTraversalVisualization(request.getValues());
        return Result.success(result);
    }
    
    /**
     * 中序遍历可视化
     * 对二叉树进行中序遍历，并记录整个过程
     *
     * @param request 包含节点值数组的请求对象
     * @return 遍历过程中每一步的状态列表
     */
    @PostMapping("/inorder-traversal")
    public Result<List<TreeAlgorithmStep>> inorderTraversalVisualization(@RequestBody TreeTraversalRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.inorderTraversalVisualization(request.getValues());
        return Result.success(result);
    }
    
    /**
     * 后序遍历可视化
     * 对二叉树进行后序遍历，并记录整个过程
     *
     * @param request 包含节点值数组的请求对象
     * @return 遍历过程中每一步的状态列表
     */
    @PostMapping("/postorder-traversal")
    public Result<List<TreeAlgorithmStep>> postorderTraversalVisualization(@RequestBody TreeTraversalRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.postorderTraversalVisualization(request.getValues());
        return Result.success(result);
    }
    
    /**
     * 层序遍历可视化
     * 对二叉树进行层序遍历，并记录整个过程
     *
     * @param request 包含节点值数组的请求对象
     * @return 遍历过程中每一步的状态列表
     */
    @PostMapping("/level-order-traversal")
    public Result<List<TreeAlgorithmStep>> levelOrderTraversalVisualization(@RequestBody TreeTraversalRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.levelOrderTraversalVisualization(request.getValues());
        return Result.success(result);
    }
    
    /**
     * 插入节点可视化
     * 向二叉搜索树中插入节点，并记录整个过程
     *
     * @param request 包含节点值数组和要插入值的请求对象
     * @return 插入过程中每一步的状态列表
     */
    @PostMapping("/insert-node")
    public Result<List<TreeAlgorithmStep>> insertNodeVisualization(@RequestBody TreeInsertRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.insertNodeVisualization(
            request.getValues(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 删除节点可视化
     * 从二叉搜索树中删除节点，并记录整个过程
     *
     * @param request 包含节点值数组和要删除值的请求对象
     * @return 删除过程中每一步的状态列表
     */
    @PostMapping("/delete-node")
    public Result<List<TreeAlgorithmStep>> deleteNodeVisualization(@RequestBody TreeDeleteRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.deleteNodeVisualization(
            request.getValues(), request.getValue());
        return Result.success(result);
    }
    
    /**
     * 查找节点可视化
     * 在二叉搜索树中查找节点，并记录整个过程
     *
     * @param request 包含节点值数组和要查找值的请求对象
     * @return 查找过程中每一步的状态列表
     */
    @PostMapping("/search-node")
    public Result<List<TreeAlgorithmStep>> searchNodeVisualization(@RequestBody TreeSearchRequest request) {
        List<TreeAlgorithmStep> result = treeAlgorithmVisualizationService.searchNodeVisualization(
            request.getValues(), request.getValue());
        return Result.success(result);
    }
}