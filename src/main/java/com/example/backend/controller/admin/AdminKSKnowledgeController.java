package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeAddDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeQueryListDto;
import com.example.backend.controller.admin.dto.AdminKSKnowledgeUpdateDto;

import com.example.backend.entity.Knowledge;
import com.example.backend.service.admin.ks_knowledge_manage.AdminKSKnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



/**
 * 管理员知识体系管理控制器
 * 负责知识点的增删改查功能
 */
@RestController
@RequestMapping("/api/admin/ksknowledge")
public class AdminKSKnowledgeController {

    private static final Logger log = LoggerFactory.getLogger(AdminKSKnowledgeController.class);

    @Autowired
    private AdminKSKnowledgeService adminKSKnowledgeService;


    //TODO: 知识点被删除后，知识点—资源对应表中，删除对应的关系
    // TODO: 知识点Key唯一保证
    /**
     * 获取知识点列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 知识点列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getKnowledgeList(@RequestBody AdminKSKnowledgeQueryListDto req) {
        try {
            log.info("收到获取知识点列表请求，参数: {}", req);
            
            // 获取知识点列表
            List<Knowledge> knowledges = adminKSKnowledgeService.getKnowledgeList(req);
            log.info("获取到知识点列表，数量: {}", knowledges.size());

            // 获取总数
            Long total = adminKSKnowledgeService.getKnowledgesCount(req);
            log.info("获取到知识点总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(knowledges.stream().map(knowledge -> (Object) knowledge).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));
            
//            log.info("返回知识点列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取知识点列表失败", e);
            return Result.error("获取知识点列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")
    public Result addKnowledge(@RequestBody AdminKSKnowledgeAddDto req) {
        try {
            boolean success = adminKSKnowledgeService.addKnowledge(req);
            if (success) {
                return Result.success("知识点创建成功");
            } else {
                return Result.error("知识点创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建知识点时发生错误: " + e.getMessage());
        }
    }



    @PostMapping("update")
    public Result<String> updateKnowledge(@RequestBody AdminKSKnowledgeUpdateDto req) {
        try {
            boolean success = adminKSKnowledgeService.updateKnowledge(req);
            if (success) {
                return Result.success("知识点更新成功");
            } else {
                return Result.error("知识点更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新知识点时发生错误: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public Result deleteKnowledge(@PathVariable Integer id) {
        try {
            boolean success = adminKSKnowledgeService.deleteKnowledgeById(id);
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