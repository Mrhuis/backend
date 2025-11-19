package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.TeacherKSChapterAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSChapterQueryListDto;
import com.example.backend.entity.Chapter;
import com.example.backend.service.teacher.ks_chapter_manage.TeacherKSChapterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端知识体系管理控制器
 * 负责章节的列表、添加、删除功能
 */
@RestController
@RequestMapping("/api/teacher/kschapter")
public class TeacherKSChapterController {

    private static final Logger log = LoggerFactory.getLogger(TeacherKSChapterController.class);

    @Autowired
    private TeacherKSChapterService teacherKSChapterService;

    /**
     * 获取章节列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 章节列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getKnowledgeList(@RequestBody TeacherKSChapterQueryListDto req) {
        try {
            log.info("收到获取章节列表请求，参数: {}", req);

            // 获取列表
            List<Chapter> chapters = teacherKSChapterService.getChapterList(req);
            log.info("获取到章节列表，数量: {}", chapters.size());

            // 获取总数
            Long total = teacherKSChapterService.getChaptersCount(req);
            log.info("获取到章节总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(chapters.stream().map(chapter -> (Object) chapter).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取章节列表失败", e);
            return Result.error("获取章节列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addKnowledge(@RequestBody TeacherKSChapterAddDto req) {
        try {
            boolean success = teacherKSChapterService.addChapter(req);
            if (success) {
                return Result.success("章节创建成功");
            } else {
                return Result.error("章节创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建章节时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteKnowledge(@PathVariable Integer id) {
        try {
            boolean success = teacherKSChapterService.deleteChapterById(id);
            if (success) {
                return Result.success("章节删除成功");
            } else {
                return Result.error("章节删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除章节时发生错误: " + e.getMessage());
        }
    }
}