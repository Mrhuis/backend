package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.*;
import com.example.backend.entity.Tag;
import com.example.backend.service.admin.ks_tag_manage.AdminKSTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: AdminKSTagController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 11:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/admin/kstag")
public class AdminKSTagController {

    private static final Logger log = LoggerFactory.getLogger(AdminKSTagController.class);

    @Autowired
    private AdminKSTagService adminKSTagService;

    /**
     * 获取标签列表（支持分页和查询条件）
     * @param req 查询条件
     * @return 标签列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getTagList(@RequestBody AdminKSTagQueryListDto req) {
        try {
            log.info("收到获取标签列表请求，参数: {}", req);

            // 获取知识点列表
            List<Tag> tags = adminKSTagService.getTagList(req);
            log.info("获取到标签列表，数量: {}", tags.size());

            // 获取总数
            Long total = adminKSTagService.getTagsCount(req);
            log.info("获取到标签总数: {}", total);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(tags.stream().map(tag -> (Object) tag).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

//            log.info("返回标签列表结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return Result.error("获取标签列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")
    public Result addTag(@RequestBody AdminKSTagAddDto req) {
        try {
            boolean success = adminKSTagService.addTag(req);
            if (success) {
                return Result.success("标签创建成功");
            } else {
                return Result.error("标签创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建标签时发生错误: " + e.getMessage());
        }
    }



    @PostMapping("update")
    public Result<String> updateTag(@RequestBody AdminKSTagUpdateDto req) {
        try {
            boolean success = adminKSTagService.updateTag(req);
            if (success) {
                return Result.success("标签更新成功");
            } else {
                return Result.error("标签更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新标签时发生错误: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public Result deleteTag(@PathVariable Integer id) {
        try {
            boolean success = adminKSTagService.deleteTagById(id);
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
