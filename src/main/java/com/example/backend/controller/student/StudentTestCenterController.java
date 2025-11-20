package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentAnswerQueryDto;
import com.example.backend.controller.student.dto.StudentAnswerSaveDto;
import com.example.backend.controller.student.vo.StudentExamPaperListVo;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.Item;
import com.example.backend.entity.StudentAnswer;
import com.example.backend.service.student.test_center.ExamPaperQuestionService;
import com.example.backend.service.student.test_center.ItemService;
import com.example.backend.service.student.test_center.StudentAnswerService;
import com.example.backend.service.student.test_center.StudentExamPaperService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/test-center")
public class StudentTestCenterController {

    private final StudentAnswerService studentAnswerService;
    private final ItemService itemService;
    private final StudentExamPaperService studentExamPaperService;
    private final ExamPaperQuestionService examPaperQuestionService;

    public StudentTestCenterController(StudentAnswerService studentAnswerService, ItemService itemService, StudentExamPaperService studentExamPaperService, ExamPaperQuestionService examPaperQuestionService) {
        this.studentAnswerService = studentAnswerService;
        this.itemService = itemService;
        this.studentExamPaperService = studentExamPaperService;
        this.examPaperQuestionService = examPaperQuestionService;
    }

    /**
     * 根据user_key、paper_id、item_key查询对应数据，
     * 如果有数据，则更新答案，如果没数据则插入新数据
     *
     * @param saveDto 保存答案的DTO对象
     * @return 保存后的学生答案对象
     */
    @PostMapping("/answer")
    public Result<StudentAnswer> saveOrUpdateAnswer(@RequestBody StudentAnswerSaveDto saveDto) {
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setUserKey(saveDto.getUserKey());
        studentAnswer.setPaperId(saveDto.getPaperId());
        studentAnswer.setItemKey(saveDto.getItemKey());
        studentAnswer.setAnswer(saveDto.getAnswer());
        studentAnswer.setScore(saveDto.getScore());
        
        StudentAnswer result = studentAnswerService.saveOrUpdateAnswer(studentAnswer);
        return Result.success(result);
    }

    /**
     * 根据user_key、paper_id、item_key查询对应答案
     *
     * @param queryDto 查询条件DTO对象
     * @return 学生答案对象
     */
    @GetMapping("/answer")
    public Result<StudentAnswer> getAnswerByUserKeyPaperIdItemKey(StudentAnswerQueryDto queryDto) {
        StudentAnswer result = studentAnswerService.getAnswerByUserKeyPaperIdItemKey(
                queryDto.getUserKey(),
                queryDto.getPaperId(),
                queryDto.getItemKey()
        );
        return Result.success(result);
    }
    
    /**
     * 根据item_key查询题目数据
     *
     * @param itemKey 习题标识
     * @return 题目数据
     */
    @GetMapping("/item/{itemKey}")
    public Result<Item> getItemByItemKey(@PathVariable String itemKey) {
        Item item = itemService.getItemByItemKey(itemKey);
        return Result.success(item);
    }
    
    /**
     * 根据学生userKey查询该学生所属班级的试卷分配信息
     *
     * @param userKey 学生userKey
     * @return 试卷分配信息列表
     */
    @GetMapping("/exam-papers")
    public Result<List<StudentExamPaperListVo>> getExamPapersByStudentUserKey(@RequestParam String userKey) {
        List<StudentExamPaperListVo> examPapers = studentExamPaperService.getExamPapersByStudentUserKey(userKey);
        return Result.success(examPapers);
    }
    
    /**
     * 根据试卷ID查询试卷中的所有题目
     *
     * @param paperId 试卷ID
     * @return 题目列表
     */
    @GetMapping("/exam-paper-questions")
    public Result<List<ExamPaperQuestion>> getQuestionsByPaperId(@RequestParam Long paperId) {
        List<ExamPaperQuestion> questions = examPaperQuestionService.getQuestionsByPaperId(paperId);
        return Result.success(questions);
    }
    
    /**
     * 完成考试接口，将指定用户和试卷的所有答题记录标记为已完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return 操作结果
     */
    @PostMapping("/complete-exam")
    public Result<String> completeExam(@RequestParam String userKey, @RequestParam Long paperId) {
        try {
            studentAnswerService.completeExam(userKey, paperId);
            return Result.success("考试完成状态更新成功");
        } catch (Exception e) {
            return Result.error("考试完成状态更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查考试是否已完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return 考试完成状态
     */
    @GetMapping("/check-exam-completed")
    public Result<String> checkExamCompleted(@RequestParam String userKey, @RequestParam Long paperId) {
        try {
            boolean isCompleted = studentAnswerService.isExamCompleted(userKey, paperId);
            if (isCompleted) {
                return Result.success("考试已完成");
            } else {
                return Result.success("考试未完成");
            }
        } catch (Exception e) {
            return Result.error("检查考试完成状态失败: " + e.getMessage());
        }
    }
}