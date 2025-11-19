package com.example.backend.controller.teacher;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeAddDto;
import com.example.backend.controller.teacher.dto.TeacherKSKnowledgeQueryListDto;
import com.example.backend.entity.Knowledge;
import com.example.backend.service.teacher.ks_knowledge_manage.TeacherKSKnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 教师端知识体系管理控制器
 * 负责知识点的列表、添加、删除功能
 */
@RestController
@RequestMapping("/api/teacher/ksknowledge")
public class TeacherKSKnowledgeController {

    private static final Logger log = LoggerFactory.getLogger(TeacherKSKnowledgeController.class);

    @Autowired
    private TeacherKSKnowledgeService teacherKSKnowledgeService;

    /**
     * 获取知识点列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 知识点列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getKnowledgeList(@RequestBody TeacherKSKnowledgeQueryListDto req) {
        try {
            log.info("收到获取知识点列表请求，参数: {}", req);
            
            // 获取知识点列表
            List<Knowledge> knowledges = teacherKSKnowledgeService.getKnowledgeList(req);
            log.info("获取到知识点列表，数量: {}", knowledges.size());

            // 获取总数
            Long total = teacherKSKnowledgeService.getKnowledgesCount(req);
            log.info("获取到知识点总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(knowledges.stream().map(knowledge -> (Object) knowledge).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取知识点列表失败", e);
            return Result.error("获取知识点列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addKnowledge(@RequestBody TeacherKSKnowledgeAddDto req) {
        try {
            boolean success = teacherKSKnowledgeService.addKnowledge(req);
            if (success) {
                return Result.success("知识点创建成功");
            } else {
                return Result.error("知识点创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建知识点时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result deleteKnowledge(@PathVariable Integer id) {
        try {
            boolean success = teacherKSKnowledgeService.deleteKnowledgeById(id);
            if (success) {
                return Result.success("知识点删除成功");
            } else {
                return Result.error("知识点删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除知识点时发生错误: " + e.getMessage());
        }
    }
}