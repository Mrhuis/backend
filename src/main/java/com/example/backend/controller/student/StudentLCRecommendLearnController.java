package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCPyModelApiDto;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnResourceDto;
import com.example.backend.service.student.lc_recommend_learn.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: StudentRecommendLearnController
 * Package: com.example.backend.controller.student
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 14:34
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/student/recommend")
public class StudentLCRecommendLearnController {

    @Autowired
    private StudentLCPyModelApiService studentLCPyModelApiService;

    @Autowired
    private StudentLCRecommendLearnService studentLCRecommendLearnService;

    @Autowired
    private StudentLCUserKnowledgeStats20dService studentLCUserKnowledgeStats20dService;

    @Autowired
    private StudentLCUserResourcePreference7dService studentLCUserResourcePreference7dService;

    @Autowired
    private StudentLCKnowledgeResourcesService studentLCKnowledgeResourcesService;

    @Autowired
    private StudentLCResourceFormService studentLCResourceFormService;

    @GetMapping("get")
    public Result get(@ModelAttribute StudentLCPyModelApiDto req) {

        Result result = studentLCPyModelApiService.getRecommendations(req.getUserKey());

        List<StudentLCRecommendLearnResourceDto> data = (List<StudentLCRecommendLearnResourceDto>) result.getData();

        return studentLCRecommendLearnService.getRecommendLearnResource(data);
    }

    @PostMapping("reuse")
    public Result getReuse(@RequestParam(required = true) String userKey) {

        return studentLCPyModelApiService.resetUserResources(userKey);
    }

    @PostMapping("recode")
    public Result recode(@RequestBody StudentLCRecommendLearnRecodeDto req) {

        System.out.println(req);

        //TODO :更新用户画像
        //获取资源所属大类
        String resourceType = studentLCResourceFormService.getResourceType(req.getFormKey());
        //查找资源涉及知识点
        List<String> knowledgeKeys = studentLCKnowledgeResourcesService.getKnowledgeKeys(resourceType,req.getResourceKey());
        //更新用户知识点掌握程度画像
        studentLCUserKnowledgeStats20dService.updateUserKnowledgeStats(req, knowledgeKeys);

        //使得推荐的资源不重复
        if(req.getIsComplete()==true&&req.getIsCorrect()==true){
            studentLCPyModelApiService.markResourceAsRight(req.getUserKey(), req.getResourceKey(), req.getFormKey());
        }
        if(req.getWatchRate()!=null && req.getWatchRate()==1.0){
            studentLCPyModelApiService.markResourceAsRight(req.getUserKey(), req.getResourceKey(), req.getFormKey());
        }
        return studentLCRecommendLearnService.recode(req);
    }

    @PostMapping("recodeclick")
    public Result recodeClick(@RequestParam(required = true) String userKey, @RequestParam(required = true)String formKey) {
        //更新用户资源偏好画像

        return studentLCUserResourcePreference7dService.updateUserResourcePreference(userKey,formKey);
    }


}