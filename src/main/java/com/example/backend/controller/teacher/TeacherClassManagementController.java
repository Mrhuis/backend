package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.Class;
import com.example.backend.service.teacher.class_manage.TeacherClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherClassManagementController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端班级管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 10:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/class")
public class TeacherClassManagementController {

    private static final Logger log = LoggerFactory.getLogger(TeacherClassManagementController.class);

    @Autowired
    private TeacherClassService teacherClassService;

    /**
     * 获取班级列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 班级列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getClassList(@RequestBody TeacherClassQueryListDto req) {
        try {
            log.info("收到获取班级列表请求，参数: {}", req);
            
            // 查询班级列表
            List<Class> classes = teacherClassService.getClassList(req);
            
            // 查询总数
            Long total = teacherClassService.getClassesCount(req);
            
            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(classes.stream().map(clazz -> (Object) clazz).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setPages((int) Math.ceil((double) total / result.getSize()));
            
            log.info("返回班级列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取班级列表失败", e);
            return Result.error("获取班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加班级
     *
     * @param req 班级信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addClass(@RequestBody TeacherClassAddDto req) {
        try {
            boolean success = teacherClassService.addClass(req);
            if (success) {
                return Result.success("班级创建成功");
            } else {
                return Result.error("班级创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建班级时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新班级
     *
     * @param req 班级信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<String> updateClass(@RequestBody TeacherClassUpdateDto req) {
        try {
            boolean success = teacherClassService.updateClass(req);
            if (success) {
                return Result.success("班级更新成功");
            } else {
                return Result.error("班级更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新班级时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除班级
     *
     * @param id 班级ID
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<String> deleteClass(@PathVariable Long id) {
        try {
            boolean success = teacherClassService.deleteClassById(id);
            if (success) {
                return Result.success("班级删除成功");
            } else {
                return Result.error("班级删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除班级时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取班级详情
     *
     * @param id 班级ID
     * @return 班级详情
     */
    @GetMapping("/detail/{id}")
    public Result<Class> getClassDetail(@PathVariable Long id) {
        try {
            Class clazz = teacherClassService.getClassById(id);
            if (clazz != null) {
                return Result.success(clazz);
            } else {
                return Result.error("班级不存在");
            }
        } catch (Exception e) {
            return Result.error("获取班级详情时发生错误: " + e.getMessage());
        }
    }
}