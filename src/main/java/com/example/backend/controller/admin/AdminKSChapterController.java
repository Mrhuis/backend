package com.example.backend.controller.admin;

/**
 * ClassName: AdminKSChaptersController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/1 17:05
 * @Version 1.0
 */


import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.*;

import com.example.backend.entity.Chapter;
import com.example.backend.service.admin.ks_chapter_manage.AdminKSChapterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员知识体系管理控制器
 * 负责章节的增删改查功能
 */
@RestController
@RequestMapping("/api/admin/kschapter")
public class AdminKSChapterController {

    private static final Logger log = LoggerFactory.getLogger(AdminKSChapterController.class);

    @Autowired
    private AdminKSChapterService adminKSChapterService;

    //TODO 章节被删除时，资源的变化，（限制？只有一个章节底下及其子章节没有资源的时候才能被删除）
    // TODO 章节Key唯一保证
    /**
     * 获取章节列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 章节列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getKnowledgeList(@RequestBody AdminKSChapterQueryListDto req) {
        try {
            log.info("收到获取章节列表请求，参数: {}", req);

            // 获取列表
            List<Chapter> chapters = adminKSChapterService.getChapterList(req);
            log.info("获取到章节列表，数量: {}", chapters.size());

            // 获取总数
            Long total = adminKSChapterService.getChaptersCount(req);
            log.info("获取到章节总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(chapters.stream().map(chapter -> (Object) chapter).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

//            log.info("返回章节列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取章节列表失败", e);
            return Result.error("获取章节列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")
    public Result addKnowledge(@RequestBody AdminKSChapterAddDto req) {
        try {
            boolean success = adminKSChapterService.addChapter(req);
            if (success) {
                return Result.success("章节创建成功");
            } else {
                return Result.error("章节创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建章节时发生错误: " + e.getMessage());
        }
    }



    @PostMapping("update")
    public Result<String> updateKnowledge(@RequestBody AdminKSChapterUpdateDto req) {
        try {
            boolean success = adminKSChapterService.updateChapter(req);
            if (success) {
                return Result.success("章节更新成功");
            } else {
                return Result.error("章节更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新章节时发生错误: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public Result deleteKnowledge(@PathVariable Integer id) {
        try {
            boolean success = adminKSChapterService.deleteChapterById(id);
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
