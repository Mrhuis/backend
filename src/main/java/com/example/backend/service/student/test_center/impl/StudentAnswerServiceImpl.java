package com.example.backend.service.student.test_center.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.StudentAnswer;
import com.example.backend.mapper.StudentAnswerMapper;
import com.example.backend.service.student.test_center.StudentAnswerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentAnswerServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer> implements StudentAnswerService {

    private final StudentAnswerMapper studentAnswerMapper;

    public StudentAnswerServiceImpl(StudentAnswerMapper studentAnswerMapper) {
        this.studentAnswerMapper = studentAnswerMapper;
    }

    @Override
    public StudentAnswer saveOrUpdateAnswer(StudentAnswer studentAnswer) {
        // 查询是否存在对应的记录
        StudentAnswer existingAnswer = studentAnswerMapper.selectByUserKeyPaperIdItemKey(
                studentAnswer.getUserKey(),
                studentAnswer.getPaperId(),
                studentAnswer.getItemKey()
        );

        if (existingAnswer != null) {
            // 如果存在，更新答案
            existingAnswer.setAnswer(studentAnswer.getAnswer());
            existingAnswer.setScore(studentAnswer.getScore());
            studentAnswerMapper.updateById(existingAnswer);
            return existingAnswer;
        } else {
            // 如果不存在，插入新数据
            studentAnswerMapper.insert(studentAnswer);
            return studentAnswer;
        }
    }

    @Override
    public StudentAnswer getAnswerByUserKeyPaperIdItemKey(String userKey, Long paperId, String itemKey) {
        return studentAnswerMapper.selectByUserKeyPaperIdItemKey(userKey, paperId, itemKey);
    }
    
    @Override
    public void completeExam(String userKey, Long paperId) {
        // 创建更新条件
        UpdateWrapper<StudentAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_key", userKey)
                .eq("paper_id", paperId);
        
        // 创建要更新的字段
        StudentAnswer updateData = new StudentAnswer();
        updateData.setIsComplete(1);
        updateData.setCompleteTime(LocalDateTime.now());
        
        // 执行批量更新
        studentAnswerMapper.update(updateData, updateWrapper);
    }
    
    @Override
    public boolean isExamCompleted(String userKey, Long paperId) {
        // 创建查询条件
        QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey)
                .eq("paper_id", paperId)
                .eq("is_complete", 1);
        
        // 查询是否存在已完成的记录
        return studentAnswerMapper.selectCount(queryWrapper) > 0;
    }
}