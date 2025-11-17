package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherExamPaperInfoController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端试卷信息管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/17 10:13
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/test/paper")
public class TeacherExamPaperInfoController {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperInfoController.class);

    @Autowired
    private TeacherExamPaperInfoService teacherExamPaperInfoService;

    /**
     * 获取试卷列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 试卷列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getExamPaperList(@RequestBody TeacherExamPaperQueryListDto req) {
        try {
            log.info("收到获取试卷列表请求，参数: {}", req);

            // 获取试卷列表
            List<ExamPaper> examPapers = teacherExamPaperInfoService.getExamPaperList(req);
            log.info("获取到试卷列表，数量: {}", examPapers.size());

            // 获取总数
            Long total = teacherExamPaperInfoService.getExamPapersCount(req);
            log.info("获取到试卷总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(examPapers.stream().map(paper -> (Object) paper).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            log.info("返回试卷列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取试卷列表失败", e);
            return Result.error("获取试卷列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加试卷
     *
     * @param req 试卷信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addExamPaper(@RequestBody TeacherExamPaperAddDto req) {
        try {
            boolean success = teacherExamPaperInfoService.addExamPaper(req);
            if (success) {
                return Result.success("试卷创建成功");
            } else {
                return Result.error("试卷创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建试卷时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新试卷
     *
     * @param req 试卷信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<String> updateExamPaper(@RequestBody TeacherExamPaperUpdateDto req) {
        try {
            boolean success = teacherExamPaperInfoService.updateExamPaper(req);
            if (success) {
                return Result.success("试卷更新成功");
            } else {
                return Result.error("试卷更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新试卷时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除试卷
     *
     * @param id 试卷ID
     * @return 删除结果
     */
    @GetMapping("/delete/{id}")
    public Result deleteExamPaper(@PathVariable Long id) {
        try {
            boolean success = teacherExamPaperInfoService.deleteExamPaperById(id);
            if (success) {
                return Result.success("试卷删除成功");
            } else {
                return Result.error("试卷删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除试卷时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取试卷详情
     *
     * @param id 试卷ID
     * @return 试卷详情
     */
    @GetMapping("/detail/{id}")
    public Result<ExamPaper> getExamPaperDetail(@PathVariable Long id) {
        try {
            ExamPaper examPaper = teacherExamPaperInfoService.getExamPaperById(id);
            if (examPaper != null) {
                return Result.success(examPaper);
            } else {
                return Result.error("试卷不存在");
            }
        } catch (Exception e) {
            return Result.error("获取试卷详情时发生错误: " + e.getMessage());
        }
    }
}