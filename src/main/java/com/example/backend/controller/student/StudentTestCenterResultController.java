package com.example.backend.controller.student;

import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.controller.student.vo.StudentCompletedPaperVO;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.Item;
import com.example.backend.entity.StudentAnswer;
import com.example.backend.service.student.test_center_result.ExamPaperQuestionService;
import com.example.backend.service.student.test_center_result.ItemService;
import com.example.backend.service.student.test_center_result.StudentAnswerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 学生测试中心结果控制器
 */
@RestController
@RequestMapping("/api/student/test-center-result")
public class StudentTestCenterResultController {

    private final ExamPaperQuestionService examPaperQuestionService;
    private final ItemService itemService;
    private final StudentAnswerService studentAnswerService;

    public StudentTestCenterResultController(
            ExamPaperQuestionService examPaperQuestionService,
            ItemService itemService,
            StudentAnswerService studentAnswerService) {
        this.examPaperQuestionService = examPaperQuestionService;
        this.itemService = itemService;
        this.studentAnswerService = studentAnswerService;
    }

    /**
     * 根据试卷ID查询试卷中的所有题目
     *
     * @param paperId 试卷ID
     * @return 题目列表
     */
    @GetMapping("/exam-paper-questions")
    public Result<List<ExamPaperQuestion>> getQuestionsByPaperId(@RequestParam Long paperId) {
        try {
            List<ExamPaperQuestion> questions = examPaperQuestionService.getQuestionsByPaperId(paperId);
            return Result.success(questions);
        } catch (Exception e) {
            return Result.error("查询试卷题目失败: " + e.getMessage());
        }
    }

    /**
     * 根据item_key查询题目数据
     *
     * @param itemKey 习题标识
     * @return 题目数据
     */
    @GetMapping("/item/{itemKey}")
    public Result<Item> getItemByItemKey(@PathVariable String itemKey) {
        try {
            Item item = itemService.getItemByItemKey(itemKey);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error("查询题目数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据user_key、paper_id、item_key查询学生答案详情
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @param itemKey 习题标识
     * @return 学生答案详情
     */
    @GetMapping("/answer")
    public Result<StudentAnswer> getAnswerByUserKeyPaperIdItemKey(
            @RequestParam String userKey,
            @RequestParam Long paperId,
            @RequestParam String itemKey) {
        try {
            StudentAnswer answer = studentAnswerService.getAnswerByUserKeyPaperIdItemKey(userKey, paperId, itemKey);
            return Result.success(answer);
        } catch (Exception e) {
            return Result.error("查询学生答案失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查试卷是否已评分完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return 评分状态
     */
    @GetMapping("/check-graded")
    public Result<String> checkPaperGraded(
            @RequestParam String userKey,
            @RequestParam Long paperId) {
        try {
            boolean isGraded = studentAnswerService.isPaperGraded(userKey, paperId);
            if (isGraded) {
                return Result.success("试卷已评分完成");
            } else {
                return Result.success("试卷未评分完成");
            }
        } catch (Exception e) {
            return Result.error("检查试卷评分状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 查看已完成的试卷列表
     *
     * @param userKey 用户标识
     * @return 已完成的试卷列表
     */
    @GetMapping("/completed-papers")
    public Result<List<StudentAnswer>> getCompletedPapersByUserKey(@RequestParam String userKey) {
        try {
            List<StudentAnswer> completedPapers = studentAnswerService.getCompletedPapersByUserKey(userKey);
            return Result.success(completedPapers);
        } catch (Exception e) {
            return Result.error("查询已完成试卷列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询已完成试卷
     *
     * @param userKey 用户标识
     * @param page    页码
     * @param size    每页条数
     * @return 分页结果
     */
    @GetMapping("/completed-papers/page")
    public Result<PageResult<StudentCompletedPaperVO>> getCompletedPapersPage(
            @RequestParam String userKey,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<StudentCompletedPaperVO> pageResult = studentAnswerService.getCompletedPapersPage(userKey, page, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询已完成试卷分页数据失败: " + e.getMessage());
        }
    }

    /**
     * 查询单份试卷的完成情况汇总
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return 汇总信息
     */
    @GetMapping("/completed-papers/summary")
    public Result<StudentCompletedPaperVO> getCompletedPaperSummary(
            @RequestParam String userKey,
            @RequestParam Long paperId) {
        try {
            Optional<StudentCompletedPaperVO> summaryOptional = studentAnswerService.getCompletedPaperSummary(userKey, paperId);
            return summaryOptional.map(Result::success)
                    .orElseGet(() -> Result.error("未找到对应的试卷完成信息"));
        } catch (Exception e) {
            return Result.error("查询试卷完成信息失败: " + e.getMessage());
        }
    }
}