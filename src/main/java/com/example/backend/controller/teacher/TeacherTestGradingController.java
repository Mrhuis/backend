package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.controller.teacher.vo.TeacherTestGradingUserVo;
import com.example.backend.entity.Class;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.StudentAnswer;
import com.example.backend.service.teacher.test_grading.TeacherTestGradingService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教师端试卷批改控制器
 */
@RestController
@RequestMapping("/api/teacher/test-grading")
public class TeacherTestGradingController {

    private final TeacherTestGradingService teacherTestGradingService;

    public TeacherTestGradingController(TeacherTestGradingService teacherTestGradingService) {
        this.teacherTestGradingService = teacherTestGradingService;
    }

    /**
     * 根据paper_id查询对应的班级列表
     *
     * @param paperId 试卷ID
     * @return 班级列表
     */
    @GetMapping("/classes")
    public Result<List<Class>> getClassesByPaperId(@RequestParam Long paperId) {
        try {
            List<Class> classes = teacherTestGradingService.getClassesByPaperId(paperId);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error("查询班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据class_key查询班级学生列表
     *
     * @param classKey 班级key
     * @return 学生列表
     */
    @GetMapping("/students")
    public Result<List<TeacherTestGradingUserVo>> getStudentsByClassKey(@RequestParam String classKey) {
        try {
            List<TeacherTestGradingUserVo> students = teacherTestGradingService.getStudentsByClassKey(classKey);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error("查询学生列表失败: " + e.getMessage());
        }
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
            List<ExamPaperQuestion> questions = teacherTestGradingService.getQuestionsByPaperId(paperId);
            return Result.success(questions);
        } catch (Exception e) {
            return Result.error("查询试卷题目失败: " + e.getMessage());
        }
    }

    /**
     * 根据user_key、paper_id、item_key查询对应答案
     *
     * @param userKey  用户标识
     * @param paperId  试卷ID
     * @param itemKey  习题标识
     * @return 学生答案对象
     */
    @GetMapping("/answer")
    public Result<StudentAnswer> getAnswerByUserKeyPaperIdItemKey(
            @RequestParam String userKey,
            @RequestParam Long paperId,
            @RequestParam String itemKey) {
        try {
            StudentAnswer result = teacherTestGradingService.getAnswerByUserKeyPaperIdItemKey(userKey, paperId, itemKey);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询学生答案失败: " + e.getMessage());
        }
    }

    /**
     * 设置学生答案得分
     *
     * @param userKey  用户标识
     * @param paperId  试卷ID
     * @param itemKey  习题标识
     * @param score    得分
     * @return 操作结果
     */
    @PostMapping("/set-score")
    public Result<String> setAnswerScore(
            @RequestParam String userKey,
            @RequestParam Long paperId,
            @RequestParam String itemKey,
            @RequestParam BigDecimal score) {
        try {
            boolean success = teacherTestGradingService.setAnswerScore(userKey, paperId, itemKey, score);
            if (success) {
                return Result.success("得分设置成功");
            } else {
                return Result.error("得分设置失败");
            }
        } catch (Exception e) {
            return Result.error("设置得分失败: " + e.getMessage());
        }
    }
}