package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.*;
import com.example.backend.entity.ResourceForm;
import com.example.backend.service.admin.ks_resourceForm_manage.AdminKSResourceFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: AdminKSResourceFormController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/2 17:16
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/admin/ksresourceform")
public class AdminKSResourceFormController {
    private static final Logger log = LoggerFactory.getLogger(AdminKSResourceFormController.class);

    @Autowired
    private AdminKSResourceFormService adminKSResourceFormService;

    @PostMapping("/list")
    public Result<QueryListVo> getResourceFormList(@RequestBody AdminKSResourceFormQueryListDto req) {
        try {

            // 获取列表
            List<ResourceForm> resourceForms = adminKSResourceFormService.getResourceFormList(req);

            // 获取总数
            Long total = adminKSResourceFormService.getResourceFormCount(req);

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(resourceForms.stream().map(resourceForm -> (Object) resourceForm).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));

//            log.info("返回结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取资源类型列表失败", e);
            return Result.error("获取资源类型列表失败: " + e.getMessage());
        }
    }


    @PostMapping("/add")
    public Result<String> addResourceForm(@RequestBody AdminKSResourceFormAddDto req) {
        try {
            boolean success = adminKSResourceFormService.addResourceForm(req);
            if (success) {
                return Result.success("创建成功");
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建时发生错误: " + e.getMessage());
        }
    }



    @PostMapping("update")
    public Result<String> updateResourceForm(@RequestBody AdminKSResourceFormUpdateDto req) {
        try {
            boolean success = adminKSResourceFormService.updateResourceForm(req);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新时发生错误: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public Result<String> deleteResourceForm(@PathVariable Integer id) {
        try {
            boolean success = adminKSResourceFormService.deleteResourceFormById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除时发生错误: " + e.getMessage());
        }
    }
}
