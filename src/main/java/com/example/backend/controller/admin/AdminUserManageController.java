package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.AdminUserAddDto;
import com.example.backend.controller.admin.dto.AdminUserQueryListDto;
import com.example.backend.controller.admin.dto.AdminUserUpdateDto;
import com.example.backend.controller.admin.vo.AdminSimpleUserVo;
import com.example.backend.controller.admin.vo.AdminUserBasicInfoVo;
import com.example.backend.controller.admin.vo.AdminUserQueryDetailVo;
import com.example.backend.entity.User;
import com.example.backend.service.admin.user_manage.AdminUserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminUserManageController
 * Package: com.example.backend.controller.admin
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserManageController {
    
    @Autowired
    private AdminUserManageService adminUserManageService;
    
    /**
     * 获取用户列表
     * @param req 查询条件
     * @return 用户列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getUserList(@RequestBody AdminUserQueryListDto req) {
        try {
            log.info("收到获取用户列表请求，参数: {}", req);
            
            // 获取用户列表
            List<User> users = adminUserManageService.getUserList(req);
            log.info("获取到用户列表，数量: {}", users.size());
            
            // 获取总数
            Long total = adminUserManageService.getUserCount(req);
            log.info("获取到用户总数: {}", total);
            
            // 将User列表转换为AdminUserQueryDetailVo列表
            List<AdminUserQueryDetailVo> userVos = users.stream().map(user -> {
                AdminUserQueryDetailVo vo = new AdminUserQueryDetailVo();
                BeanUtils.copyProperties(user, vo);
                // 显示账号：username 与 account 都使用 account 字段
                vo.setUsername(user.getAccount());
                vo.setAccount(user.getAccount());
                return vo;
            }).collect(Collectors.toList());

            log.info("分页参数 pageIndex={}, pageSize={}", req.getPageIndex(), req.getPageSize());
            
            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(userVos.stream().map(userVo -> (Object) userVo).collect(Collectors.toList()));
            result.setTotal(total);
            // 使用请求中的分页参数，避免冗余的默认值设置
            result.setSize(req.getPageSize());
            result.setCurrent(req.getPageIndex());
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取简化版用户列表 (status为enabled的用户)
     * @return 简化版用户列表
     */
    @GetMapping("/simple-list")
    public Result<List<AdminSimpleUserVo>> getSimpleUserList() {
        try {
            log.info("收到获取简化版用户列表请求");
            
            // 获取简化版用户列表
            List<AdminSimpleUserVo> simpleUsers = adminUserManageService.getSimpleUserList();
            log.info("获取到简化版用户列表，数量: {}", simpleUsers.size());
            
            return Result.success(simpleUsers);
        } catch (Exception e) {
            log.error("获取简化版用户列表失败", e);
            return Result.error("获取简化版用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加用户
     * @param req 用户信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody AdminUserAddDto req) {
        try {
            boolean success = adminUserManageService.addUser(req);
            if (success) {
                return Result.success("用户创建成功");
            } else {
                return Result.error("用户创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建用户时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户
     * @param req 用户信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<String> updateUser(@RequestBody AdminUserUpdateDto req) {
        try {
            boolean success = adminUserManageService.updateUser(req);
            if (success) {
                return Result.success("用户更新成功");
            } else {
                return Result.error("用户更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @GetMapping("/delete/{id}")
    public Result<String> deleteItem(@PathVariable Long id) {
        try {
            boolean success = adminUserManageService.deleteUserById(id);
            if (success) {
                return Result.success("用户删除成功");
            } else {
                return Result.error("用户删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除用户时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/detail/{id}")
    public Result<AdminUserQueryDetailVo> getUserDetail(@PathVariable Long id) {
        try {
            AdminUserQueryDetailVo userDetail = adminUserManageService.getUserDetailById(id);
            if (userDetail != null) {
                return Result.success(userDetail);
            } else {
                return Result.error("未找到用户");
            }
        } catch (Exception e) {
            return Result.error("获取用户详情时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 通过user_key获取用户基本信息（用户名和昵称）
     * @param userKey 用户业务唯一标识
     * @return 用户基本信息
     */
    @GetMapping("/basic-info")
    public Result<AdminUserBasicInfoVo> getUserBasicInfo(@RequestParam String userKey) {
        try {
            log.info("收到获取用户基本信息请求，userKey: {}", userKey);
            
            AdminUserBasicInfoVo userInfo = adminUserManageService.getUserBasicInfoByUserKey(userKey);
            if (userInfo != null) {
                return Result.success(userInfo);
            } else {
                return Result.error("未找到用户");
            }
        } catch (Exception e) {
            log.error("获取用户基本信息失败", e);
            return Result.error("获取用户基本信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 通过user_key获取用户详细信息
     * @param userKey 用户业务唯一标识
     * @return 用户详细信息
     */
    @GetMapping("/detail-by-key")
    public Result<AdminUserQueryDetailVo> getUserDetailByKey(@RequestParam String userKey) {
        try {
            log.info("收到获取用户详细信息请求，userKey: {}", userKey);
            
            AdminUserQueryDetailVo userDetail = adminUserManageService.getUserDetailByKey(userKey);
            if (userDetail != null) {
                return Result.success(userDetail);
            } else {
                return Result.error("未找到用户");
            }
        } catch (Exception e) {
            log.error("获取用户详细信息失败", e);
            return Result.error("获取用户详细信息失败: " + e.getMessage());
        }
    }
}