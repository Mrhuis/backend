package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.controller.teacher.vo.TeacherExamPaperQuestionListVo;
import com.example.backend.entity.ExamPaperQuestion;
import com.example.backend.entity.Item;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherExamPaperQuestionController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端试卷题目管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/17 10:13
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/test/question")
public class TeacherExamPaperQuestionController {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperQuestionController.class);

    @Autowired
    private TeacherExamPaperQuestionService teacherExamPaperQuestionService;

    @Autowired
    private ItemsMapper itemsMapper;

    /**
     * 获取试卷题目列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 题目列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getExamPaperQuestionList(@RequestBody TeacherExamPaperQuestionQueryListDto req) {
        try {
            log.info("收到获取试卷题目列表请求，参数: {}", req);

            // 获取试卷题目列表
            List<ExamPaperQuestion> examPaperQuestions = teacherExamPaperQuestionService.getExamPaperQuestionList(req);
            log.info("获取到试卷题目列表，数量: {}", examPaperQuestions.size());

            // 转换为VO并填充Item信息
            List<TeacherExamPaperQuestionListVo> voList = examPaperQuestions.stream().map(question -> {
                TeacherExamPaperQuestionListVo vo = new TeacherExamPaperQuestionListVo();
                // 复制ExamPaperQuestion的所有属性
                vo.setId(question.getId());
                vo.setPaperId(question.getPaperId());
                vo.setItemKey(question.getItemKey());
                vo.setSortNum(question.getSortNum());
                vo.setActualScore(question.getActualScore());
                
                // 查询对应的Item信息
                if (question.getItemKey() != null) {
                    try {
                        Item item = itemsMapper.selectByItemKey(question.getItemKey());
                        vo.setItem(item);
                    } catch (Exception e) {
                        log.warn("查询题目{}的Item信息失败: {}", question.getItemKey(), e.getMessage());
                        vo.setItem(null);
                    }
                }
                
                return vo;
            }).collect(java.util.stream.Collectors.toList());

            // 获取总数
            Long total = teacherExamPaperQuestionService.getExamPaperQuestionsCount(req);
            log.info("获取到试卷题目总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(voList.stream().map(vo -> (Object) vo).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            log.info("返回试卷题目列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取试卷题目列表失败", e);
            return Result.error("获取试卷题目列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加试卷题目
     *
     * @param req 题目信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<String> addExamPaperQuestion(@RequestBody TeacherExamPaperQuestionAddDto req) {
        try {
            boolean success = teacherExamPaperQuestionService.addExamPaperQuestion(req);
            if (success) {
                return Result.success("试卷题目添加成功");
            } else {
                return Result.error("试卷题目添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加试卷题目时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新试卷题目
     *
     * @param req 题目信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<String> updateExamPaperQuestion(@RequestBody TeacherExamPaperQuestionUpdateDto req) {
        try {
            boolean success = teacherExamPaperQuestionService.updateExamPaperQuestion(req);
            if (success) {
                return Result.success("试卷题目更新成功");
            } else {
                return Result.error("试卷题目更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新试卷题目时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除试卷题目
     *
     * @param id 关联ID
     * @return 删除结果
     */
    @GetMapping("/delete/{id}")
    public Result<String> deleteExamPaperQuestion(@PathVariable Long id) {
        try {
            boolean success = teacherExamPaperQuestionService.deleteExamPaperQuestionById(id);
            if (success) {
                return Result.success("试卷题目删除成功");
            } else {
                return Result.error("试卷题目删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除试卷题目时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取试卷题目详情
     *
     * @param id 关联ID
     * @return 题目详情
     */
    @GetMapping("/detail/{id}")
    public Result<ExamPaperQuestion> getExamPaperQuestionDetail(@PathVariable Long id) {
        try {
            ExamPaperQuestion examPaperQuestion = teacherExamPaperQuestionService.getExamPaperQuestionById(id);
            if (examPaperQuestion != null) {
                return Result.success(examPaperQuestion);
            } else {
                return Result.error("试卷题目不存在");
            }
        } catch (Exception e) {
            return Result.error("获取试卷题目详情时发生错误: " + e.getMessage());
        }
    }
}