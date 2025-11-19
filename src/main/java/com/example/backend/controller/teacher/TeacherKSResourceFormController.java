package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.TeacherKSResourceFormAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSResourceFormQueryListDto;
import com.example.backend.entity.ResourceForm;
import com.example.backend.service.teacher.ks_resource_form_manage.TeacherKSResourceFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端知识体系管理控制器
 * 负责资源形式的列表、添加、删除功能
 */
@RestController
@RequestMapping("/api/teacher/ksresourceform")
public class TeacherKSResourceFormController {

    private static final Logger log = LoggerFactory.getLogger(TeacherKSResourceFormController.class);

    @Autowired
    private TeacherKSResourceFormService teacherKSResourceFormService;

    @PostMapping("/list")
    public Result<QueryListVo> getResourceFormList(@RequestBody TeacherKSResourceFormQueryListDto req) {
        try {
            // 获取列表
            List<ResourceForm> resourceForms = teacherKSResourceFormService.getResourceFormList(req);

            // 获取总数
            Long total = teacherKSResourceFormService.getResourceFormCount(req);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(resourceForms.stream().map(resourceForm -> (Object) resourceForm).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取资源形式列表失败", e);
            return Result.error("获取资源形式列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addResourceForm(@RequestBody TeacherKSResourceFormAddDto req) {
        try {
            boolean success = teacherKSResourceFormService.addResourceForm(req);
            if (success) {
                return Result.success("资源形式创建成功");
            } else {
                return Result.error("资源形式创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建资源形式时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteResourceForm(@PathVariable Integer id) {
        try {
            boolean success = teacherKSResourceFormService.deleteResourceFormById(id);
            if (success) {
                return Result.success("资源形式删除成功");
            } else {
                return Result.error("资源形式删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除资源形式时发生错误: " + e.getMessage());
        }
    }
}