package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.TeacherKSTagAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSTagQueryListDto;
import com.example.backend.entity.Tag;
import com.example.backend.service.teacher.ks_tag_manage.TeacherKSTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端知识体系管理控制器
 * 负责标签的列表、添加、删除功能
 */
@RestController
@RequestMapping("/api/teacher/kstag")
public class TeacherKSTagController {

    private static final Logger log = LoggerFactory.getLogger(TeacherKSTagController.class);

    @Autowired
    private TeacherKSTagService teacherKSTagService;

    /**
     * 获取标签列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 标签列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getTagList(@RequestBody TeacherKSTagQueryListDto req) {
        try {
            log.info("收到获取标签列表请求，参数: {}", req);

            // 获取标签列表
            List<Tag> tags = teacherKSTagService.getTagList(req);
            log.info("获取到标签列表，数量: {}", tags.size());

            // 获取总数
            Long total = teacherKSTagService.getTagsCount(req);
            log.info("获取到标签总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(tags.stream().map(tag -> (Object) tag).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return Result.error("获取标签列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addTag(@RequestBody TeacherKSTagAddDto req) {
        try {
            boolean success = teacherKSTagService.addTag(req);
            if (success) {
                return Result.success("标签创建成功");
            } else {
                return Result.error("标签创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建标签时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteTag(@PathVariable Integer id) {
        try {
            boolean success = teacherKSTagService.deleteTagById(id);
            if (success) {
                return Result.success("标签删除成功");
            } else {
                return Result.error("标签删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除标签时发生错误: " + e.getMessage());
        }
    }
}