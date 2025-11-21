package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.RedBlackNode;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.TreeAlgorithmStep;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentTreeAlgorithmVisualizationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 学生端算法可视化学习 - 树算法可视化服务层实现类
 */
@Service
public class StudentTreeAlgorithmVisualizationServiceImpl implements StudentTreeAlgorithmVisualizationService {

    /**
     * 前序遍历可视化
     * 对二叉树进行前序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> preorderTraversalVisualization(int[] values) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法进行前序遍历"));
            return steps;
        }
        
        // 构建二叉树
        RedBlackNode root = buildBinaryTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉树: " + Arrays.toString(values)));
        
        // 前序遍历
        List<String> traversalOrder = new ArrayList<>();
        List<String> pointers = new ArrayList<>();
        preorderTraversal(root, traversalOrder, pointers, steps, treeState);
        
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "前序遍历完成，访问顺序: " + traversalOrder));
        
        return steps;
    }
    private final AtomicInteger nodeIdGenerator = new AtomicInteger(0);

    private void resetNodeIdGenerator() {
        nodeIdGenerator.set(0);
    }

    private int generateNodeId() {
        return nodeIdGenerator.getAndIncrement();
    }

    /**
     * 中序遍历可视化
     * 对二叉树进行中序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> inorderTraversalVisualization(int[] values) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法进行中序遍历"));
            return steps;
        }
        
        // 构建二叉树
        RedBlackNode root = buildBinaryTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉树: " + Arrays.toString(values)));
        
        // 中序遍历
        List<String> traversalOrder = new ArrayList<>();
        List<String> pointers = new ArrayList<>();
        inorderTraversal(root, traversalOrder, pointers, steps, treeState);
        
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "中序遍历完成，访问顺序: " + traversalOrder));
        
        return steps;
    }
    
    /**
     * 后序遍历可视化
     * 对二叉树进行后序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> postorderTraversalVisualization(int[] values) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法进行后序遍历"));
            return steps;
        }
        
        // 构建二叉树
        RedBlackNode root = buildBinaryTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉树: " + Arrays.toString(values)));
        
        // 后序遍历
        List<String> traversalOrder = new ArrayList<>();
        List<String> pointers = new ArrayList<>();
        postorderTraversal(root, traversalOrder, pointers, steps, treeState);
        
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "后序遍历完成，访问顺序: " + traversalOrder));
        
        return steps;
    }
    
    /**
     * 层序遍历可视化
     * 对二叉树进行层序遍历，并记录整个过程
     *
     * @param values 节点值数组
     * @return 遍历过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> levelOrderTraversalVisualization(int[] values) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法进行层序遍历"));
            return steps;
        }
        
        // 构建二叉树
        RedBlackNode root = buildBinaryTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉树: " + Arrays.toString(values)));
        
        // 层序遍历
        List<String> traversalOrder = new ArrayList<>();
        levelOrderTraversal(root, traversalOrder, steps, treeState);
        
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "层序遍历完成，访问顺序: " + traversalOrder));
        
        return steps;
    }
    
    /**
     * 插入节点可视化
     * 向二叉搜索树中插入节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要插入的值
     * @return 插入过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> insertNodeVisualization(int[] values, int value) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();

        resetNodeIdGenerator();
        
        // 构建二叉搜索树
        RedBlackNode root = buildBinarySearchTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉搜索树: " + Arrays.toString(values)));
        
        // 插入节点
        RedBlackNode newRoot = insertIntoBST(root, value, steps, treeState);
        List<RedBlackNode> newTreeState = convertTreeToList(newRoot);
        
        steps.add(new TreeAlgorithmStep(newTreeState, new ArrayList<>(), "插入节点 " + value + " 完成"));
        
        return steps;
    }
    
    /**
     * 删除节点可视化
     * 从二叉搜索树中删除节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要删除的值
     * @return 删除过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> deleteNodeVisualization(int[] values, int value) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法删除节点"));
            return steps;
        }
        
        resetNodeIdGenerator();
        
        // 构建二叉搜索树
        RedBlackNode root = buildBinarySearchTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉搜索树: " + Arrays.toString(values)));
        
        // 删除节点
        RedBlackNode newRoot = deleteFromBST(root, value, steps, treeState);
        List<RedBlackNode> newTreeState = convertTreeToList(newRoot);
        
        steps.add(new TreeAlgorithmStep(newTreeState, new ArrayList<>(), "删除节点 " + value + " 完成"));
        
        return steps;
    }
    
    /**
     * 查找节点可视化
     * 在二叉搜索树中查找节点，并记录整个过程
     *
     * @param values 节点值数组
     * @param value 要查找的值
     * @return 查找过程中每一步的状态列表
     */
    @Override
    public List<TreeAlgorithmStep> searchNodeVisualization(int[] values, int value) {
        List<TreeAlgorithmStep> steps = new ArrayList<>();
        
        if (values.length == 0) {
            steps.add(new TreeAlgorithmStep(new ArrayList<>(), new ArrayList<>(), "树为空，无法查找节点"));
            return steps;
        }
        
        resetNodeIdGenerator();
        
        // 构建二叉搜索树
        RedBlackNode root = buildBinarySearchTree(values);
        List<RedBlackNode> treeState = convertTreeToList(root);
        steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "构建二叉搜索树: " + Arrays.toString(values)));
        
        // 查找节点
        boolean found = searchInBST(root, value, steps, treeState);
        
        if (found) {
            steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "查找节点 " + value + " 成功"));
        } else {
            steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "查找节点 " + value + " 失败，节点不存在"));
        }
        
        return steps;
    }

    // 辅助方法
    
    /**
     * 构建二叉树
     */
    private RedBlackNode buildBinaryTree(int[] values) {
        if (values.length == 0) return null;
        
        RedBlackNode root = new RedBlackNode(0, values[0]);
        root.setPosition("root");
        
        Queue<RedBlackNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int i = 1;
        int nodeId = 1;
        while (!queue.isEmpty() && i < values.length) {
            RedBlackNode node = queue.poll();
            
            // 添加左子节点
            if (i < values.length) {
                RedBlackNode left = new RedBlackNode(nodeId++, values[i]);
                left.setPosition("left");
                node.setLeft(left);
                left.setParent(node);
                queue.offer(left);
                i++;
            }
            
            // 添加右子节点
            if (i < values.length) {
                RedBlackNode right = new RedBlackNode(nodeId++, values[i]);
                right.setPosition("right");
                node.setRight(right);
                right.setParent(node);
                queue.offer(right);
                i++;
            }
        }
        
        return root;
    }
    
    /**
     * 构建二叉搜索树
     */
    private RedBlackNode buildBinarySearchTree(int[] values) {
        if (values.length == 0) return null;
        
        RedBlackNode root = new RedBlackNode(generateNodeId(), values[0]);
        root.setPosition("root");
        
        for (int i = 1; i < values.length; i++) {
            root = insertIntoBST(root, values[i], null, null);
        }
        
        return root;
    }
    
    /**
     * 前序遍历递归实现
     */
    private void preorderTraversal(RedBlackNode node, List<String> traversalOrder, List<String> pointers, 
                                  List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (node == null) return;
        
        // 访问当前节点
        traversalOrder.add(String.valueOf(node.getVal()));
        pointers.clear();
        pointers.add(String.valueOf(node.getVal()));
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(pointers), "访问节点 " + node.getVal()));
        }
        
        // 遍历左子树
        preorderTraversal(node.getLeft(), traversalOrder, pointers, steps, treeState);
        
        // 遍历右子树
        preorderTraversal(node.getRight(), traversalOrder, pointers, steps, treeState);
    }
    
    /**
     * 中序遍历递归实现
     */
    private void inorderTraversal(RedBlackNode node, List<String> traversalOrder, List<String> pointers, 
                                 List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (node == null) return;
        
        // 遍历左子树
        inorderTraversal(node.getLeft(), traversalOrder, pointers, steps, treeState);
        
        // 访问当前节点
        traversalOrder.add(String.valueOf(node.getVal()));
        pointers.clear();
        pointers.add(String.valueOf(node.getVal()));
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(pointers), "访问节点 " + node.getVal()));
        }
        
        // 遍历右子树
        inorderTraversal(node.getRight(), traversalOrder, pointers, steps, treeState);
    }
    
    /**
     * 后序遍历递归实现
     */
    private void postorderTraversal(RedBlackNode node, List<String> traversalOrder, List<String> pointers, 
                                   List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (node == null) return;
        
        // 遍历左子树
        postorderTraversal(node.getLeft(), traversalOrder, pointers, steps, treeState);
        
        // 遍历右子树
        postorderTraversal(node.getRight(), traversalOrder, pointers, steps, treeState);
        
        // 访问当前节点
        traversalOrder.add(String.valueOf(node.getVal()));
        pointers.clear();
        pointers.add(String.valueOf(node.getVal()));
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(pointers), "访问节点 " + node.getVal()));
        }
    }
    
    /**
     * 层序遍历实现
     */
    private void levelOrderTraversal(RedBlackNode root, List<String> traversalOrder, 
                                    List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (root == null) return;
        
        Queue<RedBlackNode> queue = new LinkedList<>();
        queue.offer(root);
        List<String> pointers = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            RedBlackNode node = queue.poll();
            traversalOrder.add(String.valueOf(node.getVal()));
            pointers.clear();
            pointers.add(String.valueOf(node.getVal()));
            
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(pointers), "访问节点 " + node.getVal()));
            }
            
            if (node.getLeft() != null) {
                queue.offer(node.getLeft());
            }
            
            if (node.getRight() != null) {
                queue.offer(node.getRight());
            }
        }
    }
    
    /**
     * 向二叉搜索树插入节点
     */
    private RedBlackNode insertIntoBST(RedBlackNode root, int value, List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (root == null) {
            RedBlackNode newNode = new RedBlackNode(generateNodeId(), value);
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(value)), "创建新节点 " + value));
            }
            return newNode;
        }
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "比较节点 " + root.getVal() + " 与值 " + value));
        }
        
        if (value < root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 小于 " + root.getVal() + "，向左子树插入"));
            }
            RedBlackNode newLeft = insertIntoBST(root.getLeft(), value, steps, treeState);
            root.setLeft(newLeft);
            if (newLeft != null) {
                newLeft.setParent(root);
                newLeft.setPosition("left");
            }
        } else if (value > root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 大于 " + root.getVal() + "，向右子树插入"));
            }
            RedBlackNode newRight = insertIntoBST(root.getRight(), value, steps, treeState);
            root.setRight(newRight);
            if (newRight != null) {
                newRight.setParent(root);
                newRight.setPosition("right");
            }
        } else {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 已存在，无需插入"));
            }
        }
        
        return root;
    }
    
    /**
     * 从二叉搜索树删除节点
     */
    private RedBlackNode deleteFromBST(RedBlackNode root, int value, List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (root == null) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "树为空，无法删除节点 " + value));
            }
            return null;
        }
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "比较节点 " + root.getVal() + " 与值 " + value));
        }
        
        if (value < root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 小于 " + root.getVal() + "，在左子树中查找"));
            }
            RedBlackNode newLeft = deleteFromBST(root.getLeft(), value, steps, treeState);
            root.setLeft(newLeft);
        } else if (value > root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 大于 " + root.getVal() + "，在右子树中查找"));
            }
            RedBlackNode newRight = deleteFromBST(root.getRight(), value, steps, treeState);
            root.setRight(newRight);
        } else {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "找到要删除的节点 " + value));
            }
            
            // 找到要删除的节点
            if (root.getLeft() == null) {
                if (steps != null) {
                    steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "节点 " + value + " 无左子树，用右子树替代"));
                }
                return root.getRight();
            } else if (root.getRight() == null) {
                if (steps != null) {
                    steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "节点 " + value + " 无右子树，用左子树替代"));
                }
                return root.getLeft();
            } else {
                if (steps != null) {
                    steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "节点 " + value + " 有两个子节点，寻找后继节点"));
                }
                
                // 找到右子树中的最小节点（后继节点）
                RedBlackNode successor = findMin(root.getRight());
                if (steps != null) {
                    steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(successor.getVal())), "找到后继节点 " + successor.getVal()));
                }
                
                // 用后继节点的值替换当前节点的值
                root.setVal(successor.getVal());
                if (steps != null) {
                    steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "用后继节点 " + successor.getVal() + " 的值替换当前节点"));
                }
                
                // 删除后继节点
                RedBlackNode newRight = deleteFromBST(root.getRight(), successor.getVal(), steps, treeState);
                root.setRight(newRight);
            }
        }
        
        return root;
    }
    
    /**
     * 在二叉搜索树中查找节点
     */
    private boolean searchInBST(RedBlackNode root, int value, List<TreeAlgorithmStep> steps, List<RedBlackNode> treeState) {
        if (root == null) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, new ArrayList<>(), "到达空节点，未找到值 " + value));
            }
            return false;
        }
        
        if (steps != null) {
            steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "比较节点 " + root.getVal() + " 与值 " + value));
        }
        
        if (value == root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "找到节点 " + value));
            }
            return true;
        } else if (value < root.getVal()) {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 小于 " + root.getVal() + "，在左子树中查找"));
            }
            return searchInBST(root.getLeft(), value, steps, treeState);
        } else {
            if (steps != null) {
                steps.add(new TreeAlgorithmStep(treeState, Arrays.asList(String.valueOf(root.getVal())), "值 " + value + " 大于 " + root.getVal() + "，在右子树中查找"));
            }
            return searchInBST(root.getRight(), value, steps, treeState);
        }
    }
    
    /**
     * 找到子树中的最小节点
     */
    private RedBlackNode findMin(RedBlackNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    
    /**
     * 将树转换为节点列表
     */
    private List<RedBlackNode> convertTreeToList(RedBlackNode root) {
        List<RedBlackNode> nodeList = new ArrayList<>();
        if (root == null) return nodeList;
        
        // 使用Set来跟踪已添加的节点，避免重复
        Set<Integer> addedNodeIds = new HashSet<>();
        
        // 使用队列进行层序遍历
        Queue<RedBlackNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            RedBlackNode node = queue.poll();
            
            // 检查节点是否已经添加过
            if (node != null && !addedNodeIds.contains(node.getId())) {
                // 创建节点副本（不包含引用关系）
                RedBlackNode nodeCopy = copyNode(node);
                
                // 设置位置信息
                if (node.getParent() == null) {
                    nodeCopy.setPosition("root");
                } else if (node.getParent().getLeft() == node) {
                    nodeCopy.setPosition("left");
                } else if (node.getParent().getRight() == node) {
                    nodeCopy.setPosition("right");
                }
                
                nodeList.add(nodeCopy);
                addedNodeIds.add(nodeCopy.getId());
                
                // 将子节点加入队列
                if (node.getLeft() != null) {
                    queue.offer(node.getLeft());
                }
                if (node.getRight() != null) {
                    queue.offer(node.getRight());
                }
            }
        }
        
        return nodeList;
    }
    
    /**
     * 复制节点
     */
    private RedBlackNode copyNode(RedBlackNode node) {
        RedBlackNode copy = new RedBlackNode(node.getId(), node.getVal());
        copy.setPosition(node.getPosition());
        copy.setRed(node.getRed());
        return copy;
    }
}