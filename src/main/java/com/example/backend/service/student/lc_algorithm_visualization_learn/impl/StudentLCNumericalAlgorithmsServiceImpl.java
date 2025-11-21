package com.example.backend.service.student.lc_algorithm_visualization_learn.impl;

import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.AlgorithmStep;
import com.example.backend.controller.student.lc_algorithm_visualization_learn.vo.ArrayElement;
import com.example.backend.service.student.lc_algorithm_visualization_learn.StudentLCNumericalAlgorithmsService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生端算法可视化学习 - 数值相关算法服务层实现类
 */
@Service
public class StudentLCNumericalAlgorithmsServiceImpl implements StudentLCNumericalAlgorithmsService {

    /**
     * 冒泡排序可视化
     * 返回冒泡排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> bubbleSortVisualization(int[] array) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态"));

        // 执行冒泡排序并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        boolean swapped;

        for (int i = 0; i < workingArray.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < workingArray.length - 1 - i; j++) {
                // 记录比较操作
                List<Integer> pointers = Arrays.asList(j, j + 1);
                processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                    new ArrayList<>(pointers), "比较元素 " + workingArray[j] + " 和 " + workingArray[j + 1]));

                if (workingArray[j] > workingArray[j + 1]) {
                    // 交换元素
                    int temp = workingArray[j];
                    workingArray[j] = workingArray[j + 1];
                    workingArray[j + 1] = temp;
                    swapped = true;

                    // 记录交换操作
                    pointers = Arrays.asList(j, j + 1);
                    processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                        new ArrayList<>(pointers), "交换元素 " + workingArray[j + 1] + " 和 " + workingArray[j]));
                }
            }

            // 如果这一轮没有发生交换，说明已经排好序了
            if (!swapped) {
                List<Integer> pointers = new ArrayList<>();
                processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                    pointers, "数组已排序完成"));
                break;
            }
        }

        return processSteps;
    }
    
    /**
     * 快速排序可视化
     * 返回快速排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> quickSortVisualization(int[] array) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态"));

        // 执行快速排序并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        quickSort(workingArray, 0, workingArray.length - 1, processSteps, originalIndices);

        return processSteps;
    }
    
    /**
     * 快速排序递归实现
     */
    private void quickSort(int[] arr, int low, int high, List<AlgorithmStep> processSteps, Map<Integer, Integer> originalIndices) {
        if (low < high) {
            int pi = partition(arr, low, high, processSteps, originalIndices);
            
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(pi), "确定基准元素 " + arr[pi] + " 的最终位置"));
            
            quickSort(arr, low, pi - 1, processSteps, originalIndices);
            quickSort(arr, pi + 1, high, processSteps, originalIndices);
        }
    }
    
    /**
     * 快速排序分区操作
     */
    private int partition(int[] arr, int low, int high, List<AlgorithmStep> processSteps, Map<Integer, Integer> originalIndices) {
        int pivot = arr[high];
        int i = (low - 1);
        
        processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
            Arrays.asList(high), "选择基准元素 " + pivot));
        
        for (int j = low; j < high; j++) {
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(j, high), "比较元素 " + arr[j] + " 与基准元素 " + pivot));
                
            if (arr[j] <= pivot) {
                i++;
                
                // 交换元素
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                
                if (i != j) {
                    processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                        Arrays.asList(i, j), "交换元素 " + arr[j] + " 和 " + arr[i]));
                }
            }
        }
        
        // 交换pivot元素
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        
        processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
            Arrays.asList(i + 1, high), "将基准元素 " + pivot + " 放置到正确位置"));
        
        return i + 1;
    }
    
    /**
     * 归并排序可视化
     * 返回归并排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> mergeSortVisualization(int[] array) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态"));

        // 执行归并排序并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        int[] tempArray = new int[workingArray.length];
        mergeSort(workingArray, tempArray, 0, workingArray.length - 1, processSteps, originalIndices);

        return processSteps;
    }
    
    /**
     * 归并排序递归实现
     */
    private void mergeSort(int[] arr, int[] temp, int left, int right, List<AlgorithmStep> processSteps, Map<Integer, Integer> originalIndices) {
        if (left < right) {
            int mid = (left + right) / 2;
            
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(left, mid, right), "将数组分为两部分: [" + left + ", " + mid + "] 和 [" + (mid + 1) + ", " + right + "]"));
            
            mergeSort(arr, temp, left, mid, processSteps, originalIndices);
            mergeSort(arr, temp, mid + 1, right, processSteps, originalIndices);
            merge(arr, temp, left, mid, right, processSteps, originalIndices);
        }
    }
    
    /**
     * 归并操作
     */
    private void merge(int[] arr, int[] temp, int left, int mid, int right, List<AlgorithmStep> processSteps, Map<Integer, Integer> originalIndices) {
        // 复制数据到临时数组
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
            Arrays.asList(left, mid, right), "准备合并两个已排序的子数组"));
        
        int i = left;
        int j = mid + 1;
        int k = left;
        
        // 合并临时数组的两部分到原数组
        while (i <= mid && j <= right) {
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(i, j), "比较元素 " + temp[i] + " 和 " + temp[j]));
                
            if (temp[i] <= temp[j]) {
                arr[k] = temp[i];
                i++;
                processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                    Arrays.asList(k), "选择元素 " + arr[k] + " 放入位置 " + k));
            } else {
                arr[k] = temp[j];
                j++;
                processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                    Arrays.asList(k), "选择元素 " + arr[k] + " 放入位置 " + k));
            }
            k++;
        }
        
        // 复制剩余元素
        while (i <= mid) {
            arr[k] = temp[i];
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(k, i), "复制剩余元素 " + temp[i] + " 到位置 " + k));
            i++;
            k++;
        }
        
        while (j <= right) {
            arr[k] = temp[j];
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(k, j), "复制剩余元素 " + temp[j] + " 到位置 " + k));
            j++;
            k++;
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
            Arrays.asList(left, right), "完成合并子数组 [" + left + ", " + right + "]"));
    }
    
    /**
     * 插入排序可视化
     * 返回插入排序的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待排序的整数数组
     * @return 排序过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> insertionSortVisualization(int[] array) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态"));

        // 执行插入排序并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        
        for (int i = 1; i < workingArray.length; i++) {
            int key = workingArray[i];
            int j = i - 1;
            
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(i), "选择要插入的元素 " + key));
            
            while (j >= 0 && workingArray[j] > key) {
                workingArray[j + 1] = workingArray[j];
                processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                    Arrays.asList(j, j + 1), "向右移动元素 " + workingArray[j + 1]));
                j = j - 1;
            }
            
            workingArray[j + 1] = key;
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(j + 1), "将元素 " + key + " 插入到位置 " + (j + 1)));
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
            new ArrayList<>(), "插入排序完成"));

        return processSteps;
    }
    
    /**
     * 数组反转可视化
     * 返回数组反转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待反转的整数数组
     * @return 反转过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> reverseArrayVisualization(int[] array) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态"));

        // 执行数组反转并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        
        int start = 0;
        int end = workingArray.length - 1;
        
        while (start < end) {
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(start, end), "准备交换位置 " + start + " 和 " + end + " 的元素"));
            
            // 交换元素
            int temp = workingArray[start];
            workingArray[start] = workingArray[end];
            workingArray[end] = temp;
            
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(start, end), "交换元素 " + temp + " 和 " + workingArray[start]));
            
            start++;
            end--;
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
            new ArrayList<>(), "数组反转完成"));

        return processSteps;
    }
    
    /**
     * 线性搜索可视化
     * 返回线性搜索过程中比较元素的下标顺序列表
     *
     * @param array 待搜索的整数数组
     * @param target 搜索目标值
     * @return 搜索过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> linearSearchVisualization(int[] array, int target) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null) {
            return processSteps;
        }
        
        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "开始线性搜索元素 " + target));

        // 线性搜索，记录每次比较的下标
        for (int i = 0; i < array.length; i++) {
            processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                Arrays.asList(i), "比较位置 " + i + " 的元素 " + array[i] + " 与目标值 " + target));
                
            if (array[i] == target) {
                processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                    Arrays.asList(i), "在位置 " + i + " 找到目标元素 " + target));
                return processSteps;
            }
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
            new ArrayList<>(), "未找到目标元素 " + target));
        
        return processSteps;
    }
    
    /**
     * 二分搜索可视化
     * 返回二分搜索过程中比较元素的下标顺序列表
     *
     * @param array 待搜索的已排序整数数组
     * @param target 搜索目标值
     * @return 搜索过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> binarySearchVisualization(int[] array, int target) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }
        
        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "开始二分搜索元素 " + target + "，数组已排序"));

        int left = 0;
        int right = array.length - 1;
        
        // 二分搜索，记录每次比较的下标
        while (left <= right) {
            int mid = left + (right - left) / 2;
            processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                Arrays.asList(left, mid, right), "检查范围 [" + left + ", " + right + "]，中间位置 " + mid + " 的元素是 " + array[mid]));
            
            if (array[mid] == target) {
                processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                    Arrays.asList(mid), "在位置 " + mid + " 找到目标元素 " + target));
                return processSteps;
            } else if (array[mid] < target) {
                processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                    Arrays.asList(mid), "目标值 " + target + " 大于 " + array[mid] + "，在右半部分继续搜索"));
                left = mid + 1;
            } else {
                processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
                    Arrays.asList(mid), "目标值 " + target + " 小于 " + array[mid] + "，在左半部分继续搜索"));
                right = mid - 1;
            }
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(array, originalIndices), 
            new ArrayList<>(), "未找到目标元素 " + target));
        
        return processSteps;
    }
    
    /**
     * 数组旋转可视化
     * 返回数组按指定步长旋转的整个过程，每一步都包含数组状态、指针位置和行为描述
     *
     * @param array 待旋转的整数数组
     * @param steps 旋转步长
     * @return 旋转过程中每一步的状态列表
     */
    @Override
    public List<AlgorithmStep> rotateArrayVisualization(int[] array, int steps) {
        List<AlgorithmStep> processSteps = new ArrayList<>();
        
        if (array == null || array.length == 0) {
            return processSteps;
        }

        // 创建初始映射，记录每个元素的原始索引
        Map<Integer, Integer> originalIndices = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            originalIndices.put(array[i], i);
        }

        // 添加初始状态
        List<ArrayElement> initialState = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            initialState.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        processSteps.add(new AlgorithmStep(initialState, new ArrayList<>(), "初始数组状态，准备按步长 " + steps + " 旋转"));

        // 处理步长，使其在有效范围内
        int len = array.length;
        steps = steps % len;
        if (steps < 0) {
            steps += len;
        }

        // 执行数组旋转并记录每一步
        int[] workingArray = Arrays.copyOf(array, array.length);
        
        if (steps == 0) {
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                new ArrayList<>(), "旋转步长为0，数组无需变化"));
            return processSteps;
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
            new ArrayList<>(), "使用三次反转法进行旋转"));

        // 使用三次反转法进行旋转
        // 第一步：反转整个数组
        processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
            Arrays.asList(0, len - 1), "第一步：反转整个数组 [0, " + (len - 1) + "]"));
        reverse(workingArray, 0, len - 1, processSteps, originalIndices);
        
        // 第二步：反转前 len-steps 个元素
        if (len - steps > 1) {
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(0, len - steps - 1), "第二步：反转前 " + (len - steps) + " 个元素 [0, " + (len - steps - 1) + "]"));
            reverse(workingArray, 0, len - steps - 1, processSteps, originalIndices);
        }
        
        // 第三步：反转后 steps 个元素
        if (steps > 1) {
            processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
                Arrays.asList(len - steps, len - 1), "第三步：反转后 " + steps + " 个元素 [" + (len - steps) + ", " + (len - 1) + "]"));
            reverse(workingArray, len - steps, len - 1, processSteps, originalIndices);
        }
        
        processSteps.add(new AlgorithmStep(getArrayState(workingArray, originalIndices), 
            new ArrayList<>(), "数组旋转完成"));

        return processSteps;
    }
    
    /**
     * 反转数组的一部分，并记录步骤
     */
    private void reverse(int[] arr, int start, int end, List<AlgorithmStep> processSteps, Map<Integer, Integer> originalIndices) {
        while (start < end) {
            // 记录交换前的状态
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(start, end), "交换位置 " + start + " 和 " + end + " 的元素 " + arr[start] + " 和 " + arr[end]));
            
            // 交换元素
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            
            // 记录交换后的状态
            processSteps.add(new AlgorithmStep(getArrayState(arr, originalIndices), 
                Arrays.asList(start, end), "完成交换"));
            
            start++;
            end--;
        }
    }
    
    /**
     * 获取数组状态列表
     * 使用数组格式保持元素顺序，避免JSON序列化时键被排序
     */
    private List<ArrayElement> getArrayState(int[] array, Map<Integer, Integer> originalIndices) {
        List<ArrayElement> state = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            state.add(new ArrayElement(array[i], originalIndices.get(array[i])));
        }
        return state;
    }
}