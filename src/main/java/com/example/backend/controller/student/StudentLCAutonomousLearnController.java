package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnListDto;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnRecodeDto;
import com.example.backend.service.student.lc_autonomous_learn.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/list")
    public Result<QueryListVo> getTagList(@RequestBody StudentLCAutonomousLearnListDto req) {




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
