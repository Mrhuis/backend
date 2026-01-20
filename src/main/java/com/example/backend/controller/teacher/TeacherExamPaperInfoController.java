package com.example.backend.controller.teacher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.controller.teacher.vo.TeacherExamPaperListVo;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.ExamPaperDistribution;
import com.example.backend.mapper.ExamPaperDistributionMapper;
import com.example.backend.service.teacher.test_manage.TeacherExamPaperInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ExamPaperDistributionMapper examPaperDistributionMapper;

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

            // 转换为VO并检查批阅权限
            LocalDateTime now = LocalDateTime.now();
            List<TeacherExamPaperListVo> voList = examPapers.stream().map(paper -> {
                TeacherExamPaperListVo vo = new TeacherExamPaperListVo();
                // 复制ExamPaper的所有属性
                vo.setId(paper.getId());
                vo.setPaperName(paper.getPaperName());
                vo.setSubject(paper.getSubject());
                vo.setDifficulty(paper.getDifficulty());
                vo.setTotalScore(paper.getTotalScore());
                vo.setTimeLimit(paper.getTimeLimit());
                vo.setCreatorKey(paper.getCreatorKey());
                vo.setCreateTime(paper.getCreateTime());
                vo.setUpdateTime(paper.getUpdateTime());
                vo.setIsEnabled(paper.getIsEnabled());

                // 检查是否可以批阅：查询该试卷的所有发布记录
                QueryWrapper<ExamPaperDistribution> distributionQuery = new QueryWrapper<>();
                distributionQuery.eq("paper_id", paper.getId());
                List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectList(distributionQuery);

                // 判断是否可以批阅：只要有一个发布记录的截止时间已过（或没有截止时间），就可以批阅
                boolean canGrade = false;
                if (!distributions.isEmpty()) {
                    // 检查是否有至少一个发布记录的截止时间已过或没有截止时间
                    for (ExamPaperDistribution distribution : distributions) {
                        if (distribution.getDeadline() == null) {
                            // 没有截止时间，可以批阅
                            canGrade = true;
                            break;
                        } else if (now.isAfter(distribution.getDeadline())) {
                            // 截止时间已过，可以批阅
                            canGrade = true;
                            break;
                        }
                    }
                }
                vo.setCanGrade(canGrade);

                return vo;
            }).collect(Collectors.toList());

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(voList.stream().map(vo -> (Object) vo).collect(Collectors.toList()));
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
    public Result<String> addExamPaper(@RequestBody TeacherExamPaperAddDto req) {
        try {
            log.info("收到创建试卷请求，参数: {}", req);
            boolean success = teacherExamPaperInfoService.addExamPaper(req);
            if (success) {
                return Result.success("试卷创建成功");
            } else {
                return Result.error("试卷创建失败");
            }
        } catch (Exception e) {
            log.error("创建试卷时发生异常", e);
            // 尽量把根因信息透传给前端，方便排查
            String rootMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return Result.error("创建试卷时发生错误: " + rootMsg);
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
    public Result<String> deleteExamPaper(@PathVariable Long id) {
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