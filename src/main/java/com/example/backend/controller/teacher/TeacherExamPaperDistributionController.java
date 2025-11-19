package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaperDistribution;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherExamPaperDistributionController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端试卷下发管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 16:30
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/test/distribution")
public class TeacherExamPaperDistributionController {

    private static final Logger log = LoggerFactory.getLogger(TeacherExamPaperDistributionController.class);

    @Autowired
    private TeacherExamPaperDistributionService teacherExamPaperDistributionService;

    /**
     * 获取试卷下发列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 试卷下发列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getExamPaperDistributionList(@RequestBody TeacherExamPaperDistributionQueryListDto req) {
        try {
            log.info("收到获取试卷下发列表请求，参数: {}", req);
            
            // 查询试卷下发列表
            List<ExamPaperDistribution> distributions = teacherExamPaperDistributionService.getExamPaperDistributionList(req);
            
            // 查询总数
            Long total = teacherExamPaperDistributionService.getExamPaperDistributionsCount(req);
            
            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(distributions.stream().map(distribution -> (Object) distribution).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setPages((int) Math.ceil((double) total / result.getSize()));
            
            log.info("返回试卷下发列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取试卷下发列表失败", e);
            return Result.error("获取试卷下发列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加试卷下发
     *
     * @param req 试卷下发信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addExamPaperDistribution(@RequestBody TeacherExamPaperDistributionAddDto req) {
        try {
            boolean success = teacherExamPaperDistributionService.addExamPaperDistribution(req);
            if (success) {
                return Result.success("试卷下发创建成功");
            } else {
                return Result.error("试卷下发创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建试卷下发时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新试卷下发
     *
     * @param req 试卷下发信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<String> updateExamPaperDistribution(@RequestBody TeacherExamPaperDistributionUpdateDto req) {
        try {
            boolean success = teacherExamPaperDistributionService.updateExamPaperDistribution(req);
            if (success) {
                return Result.success("试卷下发更新成功");
            } else {
                return Result.error("试卷下发更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新试卷下发时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除试卷下发
     *
     * @param id 试卷下发ID
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<String> deleteExamPaperDistribution(@PathVariable Long id) {
        try {
            boolean success = teacherExamPaperDistributionService.deleteExamPaperDistributionById(id);
            if (success) {
                return Result.success("试卷下发删除成功");
            } else {
                return Result.error("试卷下发删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除试卷下发时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取试卷下发详情
     *
     * @param id 试卷下发ID
     * @return 试卷下发详情
     */
    @GetMapping("/detail/{id}")
    public Result<ExamPaperDistribution> getExamPaperDistributionDetail(@PathVariable Long id) {
        try {
            ExamPaperDistribution distribution = teacherExamPaperDistributionService.getExamPaperDistributionById(id);
            if (distribution != null) {
                return Result.success(distribution);
            } else {
                return Result.error("试卷下发记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取试卷下发详情时发生错误: " + e.getMessage());
        }
    }

    /**
     * 回收试卷
     *
     * @param req 回收信息
     * @return 回收结果
     */
    @PostMapping("/recycle")
    public Result<String> recycleExamPaperDistribution(@RequestBody TeacherExamPaperDistributionRecycleDto req) {
        try {
            boolean success = teacherExamPaperDistributionService.recycleExamPaperDistribution(req);
            if (success) {
                return Result.success("试卷回收操作成功");
            } else {
                return Result.error("试卷回收操作失败");
            }
        } catch (Exception e) {
            return Result.error("回收试卷时发生错误: " + e.getMessage());
        }
    }
}