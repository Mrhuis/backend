package com.example.backend.service.student.lc_recommend_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnRecodeDto;
import com.example.backend.entity.UserKnowledgeStats20d;
import com.example.backend.mapper.UserKnowledgeStats20dMapper;
import com.example.backend.service.student.lc_recommend_learn.StudentLCRLUserKnowledgeStats20dService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: StudentLCUserKnowledgeStats20dServiceImpl
 * Package: com.example.backend.service.student.lc_recommend_learn.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/30 17:56
 * @Version 1.0
 */
@Service
public class StudentLCRLUserKnowledgeStats20dServiceImpl implements StudentLCRLUserKnowledgeStats20dService {
    
    @Autowired
    private UserKnowledgeStats20dMapper userKnowledgeStats20dMapper;
    
    @Override
    public Result updateUserKnowledgeStats(StudentLCRLRecommendLearnRecodeDto req, List<String> knowledgeKeys) {

        if(req.getIsComplete()==true){
            for(String knowledgeKey : knowledgeKeys){
                // 根据userKey和knowledgeKey查找出对应的userKnowledgeStats20d，如果没有则创建一个插入到表中
                // 添加record_time条件，确保在当前日期的时间范围内
                QueryWrapper<UserKnowledgeStats20d> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_key", req.getUserKey())
                        .eq("knowledge_key", knowledgeKey);
                
                // 获取今天的开始和结束时间
                LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
                queryWrapper.ge("record_time", startOfDay).lt("record_time", endOfDay);
                
                UserKnowledgeStats20d userKnowledgeStats20d = userKnowledgeStats20dMapper.selectOne(queryWrapper);
                
                if (userKnowledgeStats20d == null) {
                    // 如果没有找到记录，则创建一个新的记录
                    userKnowledgeStats20d = new UserKnowledgeStats20d();
                    userKnowledgeStats20d.setUserKey(req.getUserKey());
                    userKnowledgeStats20d.setKnowledgeKey(knowledgeKey);
                    userKnowledgeStats20d.setCorrectCount(0);
                    userKnowledgeStats20d.setTotalCount(0);
                    userKnowledgeStats20d.setRecordTime(LocalDateTime.now());
                }

                // 因为req.getIsComplete()==true，所以totalCount加1
                userKnowledgeStats20d.setTotalCount(userKnowledgeStats20d.getTotalCount() + 1);
                
                // 如果req.getIsCorrect()==true，则correctCount加1
                if (req.getIsCorrect() == true) {
                    userKnowledgeStats20d.setCorrectCount(userKnowledgeStats20d.getCorrectCount() + 1);
                }

                
                // 保存到数据库
                if (userKnowledgeStats20d.getId() == null) {
                    userKnowledgeStats20dMapper.insert(userKnowledgeStats20d);
                } else {
                    userKnowledgeStats20dMapper.updateById(userKnowledgeStats20d);
                }
            }
            return Result.success();
        }
        return Result.success();
    }
}