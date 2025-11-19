package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ClassStudentEnrollment;
import com.example.backend.service.teacher.class_manage.TeacherClassStudentEnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeacherclassStudentEnrollmentsController
 * Package: com.example.backend.controller.teacher
 * Description: 教师端班级学生关系管理控制器
 *
 * @Author 陈昊锡
 * @Create 2025/11/18 10:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/teacher/class-student-enrollment")
public class TeacherclassStudentEnrollmentsController {

    private static final Logger log = LoggerFactory.getLogger(TeacherclassStudentEnrollmentsController.class);

    @Autowired
    private TeacherClassStudentEnrollmentService teacherClassStudentEnrollmentService;

    /**
     * 获取班级学生关系列表（支持分页和查询条件）
     *
     * @param req 查询条件
     * @return 班级学生关系列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getClassStudentEnrollmentList(@RequestBody TeacherClassStudentEnrollmentQueryListDto req) {
        try {
            log.info("收到获取班级学生关系列表请求，参数: {}", req);
            
            // 查询班级学生关系列表
            List<ClassStudentEnrollment> enrollments = teacherClassStudentEnrollmentService.getClassStudentEnrollmentList(req);
            
            // 查询总数
            Long total = teacherClassStudentEnrollmentService.getClassStudentEnrollmentsCount(req);
            
            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(enrollments.stream().map(enrollment -> (Object) enrollment).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setPages((int) Math.ceil((double) total / result.getSize()));
            
            log.info("返回班级学生关系列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取班级学生关系列表失败", e);
            return Result.error("获取班级学生关系列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加班级学生关系
     *
     * @param req 班级学生关系信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addClassStudentEnrollment(@RequestBody TeacherClassStudentEnrollmentAddDto req) {
        try {
            boolean success = teacherClassStudentEnrollmentService.addClassStudentEnrollment(req);
            if (success) {
                return Result.success("班级学生关系创建成功");
            } else {
                return Result.error("班级学生关系创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建班级学生关系时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新班级学生关系
     *
     * @param req 班级学生关系信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<String> updateClassStudentEnrollment(@RequestBody TeacherClassStudentEnrollmentUpdateDto req) {
        try {
            boolean success = teacherClassStudentEnrollmentService.updateClassStudentEnrollment(req);
            if (success) {
                return Result.success("班级学生关系更新成功");
            } else {
                return Result.error("班级学生关系更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新班级学生关系时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除班级学生关系
     *
     * @param id 班级学生关系ID
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<String> deleteClassStudentEnrollment(@PathVariable Long id) {
        try {
            boolean success = teacherClassStudentEnrollmentService.deleteClassStudentEnrollmentById(id);
            if (success) {
                return Result.success("班级学生关系删除成功");
            } else {
                return Result.error("班级学生关系删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除班级学生关系时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取班级学生关系详情
     *
     * @param id 班级学生关系ID
     * @return 班级学生关系详情
     */
    @GetMapping("/detail/{id}")
    public Result<ClassStudentEnrollment> getClassStudentEnrollmentDetail(@PathVariable Long id) {
        try {
            ClassStudentEnrollment enrollment = teacherClassStudentEnrollmentService.getClassStudentEnrollmentById(id);
            if (enrollment != null) {
                return Result.success(enrollment);
            } else {
                return Result.error("班级学生关系不存在");
            }
        } catch (Exception e) {
            return Result.error("获取班级学生关系详情时发生错误: " + e.getMessage());
        }
    }
}