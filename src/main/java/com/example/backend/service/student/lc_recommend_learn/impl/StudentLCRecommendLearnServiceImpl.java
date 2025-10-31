package com.example.backend.service.student.lc_recommend_learn.impl;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnRecodeDto;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnResourceDto;
import com.example.backend.controller.student.vo.StudentLCRecommendLearnResourceVo;
import com.example.backend.entity.Item;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.ResourceForm;
import com.example.backend.entity.UserResourceInteraction;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.mapper.UserResourceInteractionMapper;
import com.example.backend.service.student.lc_recommend_learn.StudentLCPyModelApiService;
import com.example.backend.service.student.lc_recommend_learn.StudentLCRecommendLearnService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: StudentRecommendLearnService
 * Package: com.example.backend.service.student
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/28 15:00
 * @Version 1.0
 */
@Service
public class StudentLCRecommendLearnServiceImpl implements StudentLCRecommendLearnService {
    @Autowired
    private ResourceFormMapper resourceFormMapper;

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;

    @Autowired
    private UserResourceInteractionMapper userResourceInteractionMapper;
    @Override
    public Result getRecommendLearnResource(List<StudentLCRecommendLearnResourceDto> datas) {
        List<StudentLCRecommendLearnResourceVo> studentLCRecommendLearnResourceVos = new ArrayList<>();
        StudentLCRecommendLearnResourceVo studentLCRecommendLearnResourceVo;
        for(StudentLCRecommendLearnResourceDto data:datas){
            studentLCRecommendLearnResourceVo = new StudentLCRecommendLearnResourceVo();
            ResourceForm resourceForm = resourceFormMapper.selectById(data.getFormId());
            if(resourceForm != null){
                studentLCRecommendLearnResourceVo.setFormType(resourceForm.getFormType());
                studentLCRecommendLearnResourceVo.setFormKey(resourceForm.getFormKey());
                studentLCRecommendLearnResourceVo.setResourceKey(data.getResourceKey());
                Item item = itemsMapper.selectByItemKeyAndFormKey(data.getResourceKey(),resourceForm.getFormKey());
                if(item != null){
                    studentLCRecommendLearnResourceVo.setDifficulty(item.getDifficulty());
                    studentLCRecommendLearnResourceVo.setContent(item.getContent());
                    studentLCRecommendLearnResourceVo.setOptions(item.getOptions());
                    studentLCRecommendLearnResourceVo.setAnswer(item.getAnswer());
                    studentLCRecommendLearnResourceVo.setSolution(item.getSolution());
                    studentLCRecommendLearnResourceVos.add(studentLCRecommendLearnResourceVo);
                    continue;
                }
                MediaAssets mediaAssets = mediaAssetsMapper.selectByMediaKeyAndFormKey(data.getResourceKey(),resourceForm.getFormKey());
                if(mediaAssets != null){
                    studentLCRecommendLearnResourceVo.setFileName(mediaAssets.getFileName());
                    studentLCRecommendLearnResourceVo.setUrl(mediaAssets.getUrl());
                    studentLCRecommendLearnResourceVo.setDuration(mediaAssets.getDuration());
                    studentLCRecommendLearnResourceVos.add(studentLCRecommendLearnResourceVo);
                    continue;
                }
            }
        }
        return Result.success(studentLCRecommendLearnResourceVos);
    }

    @Override
    public Result recode(StudentLCRecommendLearnRecodeDto req) {
        UserResourceInteraction interaction = new UserResourceInteraction(
                null,                           // id
                req.getUserKey(),               // userKey
                req.getFormKey(),               // formKey (StudentLCRecommendLearnRecodeDto中没有这个字段)
                req.getResourceKey(),           // resourceKey
                null,                           // post3dCorrectRate
                null,                           // postPracticeCount
                null,                           // isFirstSubmit24h
                null,                           // correctRateChange
                req.getIsComplete() != null ? (req.getIsComplete() ? 1 : 0) : null,  // isComplete
                req.getIsCorrect() != null ? (req.getIsCorrect() ? 1 : 0) : 0,    // isCorrect，默认false
                req.getIsViewAnalysis() != null ? (req.getIsViewAnalysis() ? 1 : 0) : null, // isViewAnalysis
                req.getWatchRate(),    // watchRate
                req.getIsPause() != null ? (req.getIsPause() ? 1 : 0) : null,        // isPause
                req.getIsReplay() != null ? (req.getIsReplay() ? 1 : 0) : null,      // isReplay
                req.getInteractionTime(), // interactionTime
                null                            // effectCalcTime
        );
        
        // TODO: 这里应该保存 interaction 到数据库
        userResourceInteractionMapper.insert( interaction);

        return Result.success("保存成功", null);
    }
}
