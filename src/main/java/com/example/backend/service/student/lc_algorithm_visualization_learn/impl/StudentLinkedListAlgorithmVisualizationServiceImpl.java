package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.LinkedListAlgorithmStep;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.ListNode;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentLinkedListAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生端算法可视化学习 - 链表算法可视化服务层实现类
 */
@Service
public class StudentLinkedListAlgorithmVisualizationServiceImpl implements StudentLinkedListAlgorithmVisualizationService {

    /**
     * 在链表开头插入节点可视化
     * 将输入数组转换为链表结构后，在开头插入指定值，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> insertAtHeadVisualization(int[] array, int value) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }
        for (int i = 1; i < nodeList.size(); i++) {
            nodeList.get(i).setPrevIndex(i - 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        // 第二步：创建新节点
        ListNode newNode = new ListNode(value, null); // null表示新节点还没有在链表中的位置
        nodeList.add(0, newNode);
        List<ListNode> nodeListWithNewNode = copyNodeList(nodeList);

        // 更新索引
        for (int i = 1; i < nodeListWithNewNode.size(); i++) {
            if (nodeListWithNewNode.get(i).getPrevIndex() != null) {
                nodeListWithNewNode.get(i).setPrevIndex(nodeListWithNewNode.get(i).getPrevIndex() + 1);
            }
            if (nodeListWithNewNode.get(i).getNextIndex() != null) {
                nodeListWithNewNode.get(i).setNextIndex(nodeListWithNewNode.get(i).getNextIndex() + 1);
            }
        }

        steps.add(new LinkedListAlgorithmStep(nodeListWithNewNode, List.of(0), "创建新节点: " + value));

        // 第三步：设置新节点的next指针指向原来的头节点
        if (nodeList.size() > 1) {
            nodeList.get(0).setNextIndex(1);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(0, 1), "设置新节点的next指针指向原头节点"));
        }

        // 第四步：设置原头节点的prev指针指向新节点
        if (nodeList.size() > 1) {
            nodeList.get(1).setPrevIndex(0);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(0, 1), "设置原头节点的prev指针指向新节点"));
        }

        // 第五步：完成插入
        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(), "完成在开头插入节点"));

        return steps;
    }
    
    /**
     * 在链表末尾插入节点可视化
     * 将输入数组转换为链表结构后，在末尾插入指定值，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> insertAtTailVisualization(int[] array, int value) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }
        for (int i = 1; i < nodeList.size(); i++) {
            nodeList.get(i).setPrevIndex(i - 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        // 第二步：创建新节点
        ListNode newNode = new ListNode(value, null); // null表示新节点还没有在链表中的位置
        nodeList.add(newNode);
        int newNodeIndex = nodeList.size() - 1;

        // 更新新节点的索引
        if (newNodeIndex > 0) {
            nodeList.get(newNodeIndex).setPrevIndex(newNodeIndex - 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(newNodeIndex), "创建新节点: " + value));

        // 第三步：连接新节点到链表末尾
        if (nodeList.size() > 1) {
            nodeList.get(newNodeIndex - 1).setNextIndex(newNodeIndex);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(newNodeIndex - 1, newNodeIndex), "将原尾节点的next指针指向新节点"));
        }

        // 第四步：完成插入
        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(), "完成在末尾插入节点"));

        return steps;
    }
    
    /**
     * 删除链表中指定值的节点可视化
     * 将输入数组转换为链表结构后，删除指定值的节点，并记录整个过程
     *
     * @param array 初始整数数组
     * @param value 要删除的值
     * @return 删除过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> deleteNodeVisualization(int[] array, int value) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }
        for (int i = 1; i < nodeList.size(); i++) {
            nodeList.get(i).setPrevIndex(i - 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        // 查找要删除的节点
        int deleteIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                deleteIndex = i;
                break;
            }
        }

        if (deleteIndex == -1) {
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "未找到值为 " + value + " 的节点"));
            return steps;
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(deleteIndex), "找到要删除的节点，值为 " + value + "，位置为 " + deleteIndex));

        // 删除节点
        if (nodeList.size() == 1) {
            // 如果只有一个节点
            nodeList.clear();
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "删除唯一节点，链表变为空"));
        } else if (deleteIndex == 0) {
            // 删除头节点
            nodeList.remove(0);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(0), "删除头节点"));

            // 更新新头节点的prev指针
            if (!nodeList.isEmpty()) {
                nodeList.get(0).setPrevIndex(null);
                steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(0), "更新新头节点的prev指针为null"));
            }
        } else if (deleteIndex == nodeList.size() - 1) {
            // 删除尾节点
            int tailIndex = nodeList.size() - 1;
            nodeList.remove(tailIndex);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(tailIndex - 1), "删除尾节点"));

            // 更新新尾节点的next指针
            if (!nodeList.isEmpty()) {
                nodeList.get(tailIndex - 1).setNextIndex(null);
                steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(tailIndex - 1), "更新新尾节点的next指针为null"));
            }
        } else {
            // 删除中间节点
            nodeList.remove(deleteIndex);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(deleteIndex - 1, deleteIndex, deleteIndex + 1), "删除中间节点"));

            // 更新前后节点的指针
            if (deleteIndex > 0 && deleteIndex < nodeList.size()) {
                nodeList.get(deleteIndex - 1).setNextIndex(deleteIndex);
            }
            if (deleteIndex < nodeList.size()) {
                nodeList.get(deleteIndex).setPrevIndex(deleteIndex - 1);
            }
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(deleteIndex - 1, deleteIndex), "更新相邻节点的指针"));
        }

        // 更新索引
        for (int i = 0; i < nodeList.size(); i++) {
            if (i > 0) {
                nodeList.get(i).setPrevIndex(i - 1);
            } else {
                nodeList.get(i).setPrevIndex(null);
            }

            if (i < nodeList.size() - 1) {
                nodeList.get(i).setNextIndex(i + 1);
            } else {
                nodeList.get(i).setNextIndex(null);
            }
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "完成删除节点操作"));

        return steps;
    }
    
    /**
     * 反转链表可视化
     * 将输入数组转换为链表结构后，反转链表，并记录整个过程
     *
     * @param array 初始整数数组
     * @return 反转过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> reverseListVisualization(int[] array) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }
        for (int i = 1; i < nodeList.size(); i++) {
            nodeList.get(i).setPrevIndex(i - 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        // 反转链表
        if (nodeList.size() <= 1) {
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "链表节点数小于等于1，无需反转"));
            return steps;
        }

        // 使用三指针法反转链表
        for (int i = 0; i < nodeList.size(); i++) {
            Integer nextIndex = nodeList.get(i).getNextIndex();
            Integer prevIndex = nodeList.get(i).getPrevIndex();
            nodeList.get(i).setNextIndex(prevIndex);
            nodeList.get(i).setPrevIndex(nextIndex);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "完成链表反转"));

        return steps;
    }
    
    /**
     * 查找链表中间节点可视化
     * 使用快慢指针技术查找链表的中间节点
     *
     * @param array 初始整数数组
     * @return 查找过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> findMiddleNodeVisualization(int[] array) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        if (nodeList.isEmpty()) {
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "链表为空，无中间节点"));
            return steps;
        }

        if (nodeList.size() == 1) {
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(0), "链表只有一个节点，中间节点为位置0"));
            return steps;
        }

        // 使用快慢指针查找中间节点
        int slow = 0;
        int fast = 0;
        
        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(slow), "初始化慢指针在位置" + slow));
        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(fast), "初始化快指针在位置" + fast));

        while (fast < nodeList.size() - 1 && nodeList.get(fast).getNextIndex() != null && nodeList.get(nodeList.get(fast).getNextIndex()).getNextIndex() != null) {
            slow = nodeList.get(slow).getNextIndex();
            fast = nodeList.get(nodeList.get(fast).getNextIndex()).getNextIndex();
            
            List<Integer> pointers = new ArrayList<>();
            pointers.add(slow);
            pointers.add(fast);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), pointers, "慢指针移动到位置" + slow + "，快指针移动到位置" + fast));
        }

        // 最后一步，快指针可能只能移动一步
        if (fast < nodeList.size() - 1 && nodeList.get(fast).getNextIndex() != null) {
            slow = nodeList.get(slow).getNextIndex();
            fast = nodeList.get(fast).getNextIndex();
            
            List<Integer> pointers = new ArrayList<>();
            pointers.add(slow);
            pointers.add(fast);
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), pointers, "慢指针移动到位置" + slow + "，快指针移动到位置" + fast));
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), List.of(slow), "找到中间节点，位置为" + slow + "，值为" + nodeList.get(slow).getValue()));

        return steps;
    }
    
    /**
     * 检测链表中是否存在环可视化
     * 使用快慢指针技术检测链表中是否存在环
     *
     * @param array 初始整数数组
     * @return 检测过程中每一步的状态列表
     */
    @Override
    public List<LinkedListAlgorithmStep> hasCycleVisualization(int[] array) {
        List<LinkedListAlgorithmStep> steps = new ArrayList<>();

        // 第一步：创建初始链表
        List<ListNode> nodeList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            nodeList.add(new ListNode(array[i], i));
        }

        // 连接节点
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNextIndex(i + 1);
        }

        steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "初始化链表: " + arrayToString(array)));

        if (nodeList.size() <= 1) {
            steps.add(new LinkedListAlgorithmStep(copyNodeList(nodeList), new ArrayList<>(), "链表节点数小于等于1，不存在环"));
            return steps;
        }

        // 为了演示目的，我们创建一个简单的环：将尾节点指向头节点
        int tailIndex = nodeList.size() - 1;
        nodeList.get(tailIndex).setNextIndex(0);
        
        List<ListNode> nodeListWithCycle = copyNodeList(nodeList);
        steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, List.of(tailIndex, 0), "创建环：将尾节点指向头节点"));

        // 使用快慢指针检测环
        int slow = 0;
        int fast = 0;
        
        steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, List.of(slow), "初始化慢指针在位置" + slow));
        steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, List.of(fast), "初始化快指针在位置" + fast));

        // 移动指针并检测是否相遇
        boolean hasCycle = false;
        for (int i = 0; i < nodeList.size() * 2; i++) { // 限制循环次数以避免无限循环
            if (slow < nodeListWithCycle.size() && fast < nodeListWithCycle.size() && 
                nodeListWithCycle.get(slow).getNextIndex() != null && 
                nodeListWithCycle.get(fast).getNextIndex() != null &&
                nodeListWithCycle.get(nodeListWithCycle.get(fast).getNextIndex()).getNextIndex() != null) {
                
                slow = nodeListWithCycle.get(slow).getNextIndex();
                fast = nodeListWithCycle.get(nodeListWithCycle.get(fast).getNextIndex()).getNextIndex();
                
                List<Integer> pointers = new ArrayList<>();
                pointers.add(slow);
                pointers.add(fast);
                steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, pointers, "慢指针移动到位置" + slow + "，快指针移动到位置" + fast));
                
                if (slow == fast) {
                    hasCycle = true;
                    steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, List.of(slow), "快慢指针相遇，位置为" + slow + "，说明链表中存在环"));
                    break;
                }
            } else {
                break;
            }
        }

        if (!hasCycle) {
            steps.add(new LinkedListAlgorithmStep(nodeListWithCycle, new ArrayList<>(), "快慢指针未相遇，说明链表中不存在环"));
        }

        return steps;
    }

    /**
     * 复制节点列表
     *
     * @param original 原始节点列表
     * @return 复制后的节点列表
     */
    private List<ListNode> copyNodeList(List<ListNode> original) {
        List<ListNode> copy = new ArrayList<>();
        for (ListNode node : original) {
            copy.add(new ListNode(node.getValue(), node.getPrevIndex(), node.getNextIndex()));
        }
        return copy;
    }

    /**
     * 将数组转换为字符串表示
     *
     * @param array 数组
     * @return 字符串表示
     */
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}