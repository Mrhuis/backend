package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnRecodeDto;
import com.example.backend.controller.student.vo.StudentLCAutonomousLearnItemsListVo;
import com.example.backend.controller.student.vo.StudentLCAutonomousLearnVideosListVo;
import com.example.backend.entity.Item;
import com.example.backend.entity.MediaAssets;
import com.example.backend.service.student.lc_autonomous_learn.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: StudentLCAutonomousLearnController
 * Package: com.example.backend.controller.student
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/31 17:44
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/student/autoAomous")
public class StudentLCAutonomousLearnController {


    @Autowired
    private StudentLCALRecommendLearnService studentLCALRecommendLearnService;

    @Autowired
    private StudentLCALUserKnowledgeStats20dService studentLCALUserKnowledgeStats20dService;

    @Autowired
    private StudentLCALUserResourcePreference7dService studentLCALUserResourcePreference7dService;

    @Autowired
    private StudentLCALKnowledgeResourcesService studentLCALKnowledgeResourcesService;

    @Autowired
    private StudentLCALResourceFormService studentLCALResourceFormService;

    @Autowired
    private StudentLCALItemsService studentLCALItemsService;
    
    @Autowired
    private StudentLCALVideosService studentLCALVideosService;

    @PostMapping("/itemsList")
    public Result<QueryListVo> getItemsList(@RequestBody StudentLCAutonomousLearnListDto req) {
        try {
            // 获取习题列表
            List<Item> items = studentLCALItemsService.getItemsList(req);

            // 获取总数
            Long total = studentLCALItemsService.getItemsCount(req);

            // 转换为VO对象列表
            List<StudentLCAutonomousLearnItemsListVo> itemVos = items.stream().map(item -> {
                StudentLCAutonomousLearnItemsListVo vo = new StudentLCAutonomousLearnItemsListVo();
                BeanUtils.copyProperties(item, vo);
                return vo;
            }).collect(Collectors.toList());

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(itemVos.stream().map(itemVo -> (Object) itemVo).collect(Collectors.toList()));
            result.setTotal(total);
            int pageSize = req.getPageSize() != null ? req.getPageSize() : 100;
            result.setSize(pageSize);
            int currentPage = req.getPageIndex() != null ? req.getPageIndex() : 1;
            result.setCurrent(currentPage);
            result.setPages((int) Math.ceil((double) total / pageSize));

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取习题列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/videosList")
    public Result<QueryListVo> getVideosList(@RequestBody StudentLCAutonomousLearnListDto req) {
        try {
            // 获取视频列表
            List<MediaAssets> videos = studentLCALVideosService.getVideosList(req);

            // 获取总数
            Long total = studentLCALVideosService.getVideosCount(req);

            // 转换为VO对象列表
            List<StudentLCAutonomousLearnVideosListVo> videoVos = videos.stream().map(video -> {
                StudentLCAutonomousLearnVideosListVo vo = new StudentLCAutonomousLearnVideosListVo();
                BeanUtils.copyProperties(video, vo);
                return vo;
            }).collect(Collectors.toList());

            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(videoVos.stream().map(videoVo -> (Object) videoVo).collect(Collectors.toList()));
            result.setTotal(total);
            int pageSize = req.getPageSize() != null ? req.getPageSize() : 100;
            result.setSize(pageSize);
            int currentPage = req.getPageIndex() != null ? req.getPageIndex() : 1;
            result.setCurrent(currentPage);
            result.setPages((int) Math.ceil((double) total / pageSize));

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取视频列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("recode")
    public Result recode(@RequestBody StudentLCAutonomousLearnRecodeDto req) {

        //获取资源所属大类
        String resourceType = studentLCALResourceFormService.getResourceType(req.getFormKey());
        //查找资源涉及知识点
        List<String> knowledgeKeys = studentLCALKnowledgeResourcesService.getKnowledgeKeys(resourceType,req.getResourceKey());
        //更新用户知识点掌握程度画像
        studentLCALUserKnowledgeStats20dService.updateUserKnowledgeStats(req, knowledgeKeys);

        return studentLCALRecommendLearnService.recode(req);
    }

    @PostMapping("recodeclick")
    public Result recodeClick(@RequestParam(required = true) String userKey, @RequestParam(required = true)String formKey) {
        //更新用户资源偏好画像
        return studentLCALUserResourcePreference7dService.updateUserResourcePreference(userKey,formKey);
    }

}