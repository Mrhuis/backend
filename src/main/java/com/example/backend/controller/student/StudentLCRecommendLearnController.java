package com.example.backend.controller.student;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRLPyModelApiDto;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnResourceDto;
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
    private StudentLCRLPyModelApiService studentLCRLPyModelApiService;

    @Autowired
    private StudentLCRLRecommendLearnService studentLCRLRecommendLearnService;

    @Autowired
    private StudentLCRLUserKnowledgeStats20dService studentLCRLUserKnowledgeStats20dService;

    @Autowired
    private StudentLCRLUserResourcePreference7dService studentLCRLUserResourcePreference7dService;

    @Autowired
    private StudentLCRLKnowledgeResourcesService studentLCRLKnowledgeResourcesService;

    @Autowired
    private StudentLCRLResourceFormService studentLCRLResourceFormService;

    @GetMapping("get")
    public Result get(@ModelAttribute StudentLCRLPyModelApiDto req) {

        Result result = studentLCRLPyModelApiService.getRecommendations(req.getUserKey());

        List<StudentLCRLRecommendLearnResourceDto> data = (List<StudentLCRLRecommendLearnResourceDto>) result.getData();

        return studentLCRLRecommendLearnService.getRecommendLearnResource(data);
    }

    @PostMapping("reuse")
    public Result getReuse(@RequestParam(required = true) String userKey) {

        return studentLCRLPyModelApiService.resetUserResources(userKey);
    }

    @PostMapping("recode")
    public Result recode(@RequestBody StudentLCRLRecommendLearnRecodeDto req) {

        //获取资源所属大类
        String resourceType = studentLCRLResourceFormService.getResourceType(req.getFormKey());
        //查找资源涉及知识点
        List<String> knowledgeKeys = studentLCRLKnowledgeResourcesService.getKnowledgeKeys(resourceType,req.getResourceKey());
        //更新用户知识点掌握程度画像
        studentLCRLUserKnowledgeStats20dService.updateUserKnowledgeStats(req, knowledgeKeys);

        //使得推荐的资源不重复
        //做对了不推荐
        if(req.getIsComplete()==true&&req.getIsCorrect()==true){
            studentLCRLPyModelApiService.markResourceAsRight(req.getUserKey(), req.getResourceKey(), req.getFormKey());
        }
        //视频看完了不推荐
        if(req.getWatchRate()!=null && req.getWatchRate()==1.0){
            studentLCRLPyModelApiService.markResourceAsRight(req.getUserKey(), req.getResourceKey(), req.getFormKey());
        }
        return studentLCRLRecommendLearnService.recode(req);
    }

    @PostMapping("recodeclick")
    public Result recodeClick(@RequestParam(required = true) String userKey, @RequestParam(required = true)String formKey) {
        //更新用户资源偏好画像
        return studentLCRLUserResourcePreference7dService.updateUserResourcePreference(userKey,formKey);
    }


}