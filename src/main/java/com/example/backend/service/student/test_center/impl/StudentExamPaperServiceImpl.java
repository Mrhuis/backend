package com.example.backend.service.student.test_center.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.controller.student.vo.StudentExamPaperListVo;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.ClassStudentEnrollment;
import com.example.backend.entity.ExamPaperDistribution;
import com.example.backend.mapper.ClassStudentEnrollmentMapper;
import com.example.backend.mapper.ExamPaperDistributionMapper;
import com.example.backend.mapper.ExamPaperMapper;
import com.example.backend.service.student.test_center.StudentExamPaperService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentExamPaperServiceImpl extends ServiceImpl<ClassStudentEnrollmentMapper, ClassStudentEnrollment> implements StudentExamPaperService {

    private final ClassStudentEnrollmentMapper classStudentEnrollmentMapper;
    private final ExamPaperDistributionMapper examPaperDistributionMapper;
    private final ExamPaperMapper examPaperMapper;

    public StudentExamPaperServiceImpl(ClassStudentEnrollmentMapper classStudentEnrollmentMapper,
                                       ExamPaperDistributionMapper examPaperDistributionMapper,
                                       ExamPaperMapper examPaperMapper) {
        this.classStudentEnrollmentMapper = classStudentEnrollmentMapper;
        this.examPaperDistributionMapper = examPaperDistributionMapper;
        this.examPaperMapper = examPaperMapper;
    }

    @Override
    public List<StudentExamPaperListVo> getExamPapersByStudentUserKey(String userKey) {
        // 1. 根据userKey查询学生所属的所有班级
        List<ClassStudentEnrollment> enrollments = classStudentEnrollmentMapper.selectByUserKey(userKey);
        
        // 如果没有班级，直接返回空列表
        if (enrollments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取班级classKey列表
        List<String> classKeys = enrollments.stream()
                .map(ClassStudentEnrollment::getClassKey)
                .collect(Collectors.toList());
        
        // 2. 根据班级classKey列表查询试卷分配信息
        List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectByClassKeys(classKeys);
        
        // 如果没有试卷分配信息，直接返回空列表
        if (distributions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取试卷ID列表
        List<Long> paperIds = distributions.stream()
                .map(ExamPaperDistribution::getPaperId)
                .collect(Collectors.toList());
        
        // 3. 根据试卷ID列表查询试卷信息
        List<ExamPaper> examPapers = examPaperMapper.selectBatchIds(paperIds);
        
        // 4. 组合数据
        List<StudentExamPaperListVo> result = new ArrayList<>();
        for (ExamPaperDistribution distribution : distributions) {
            // 查找对应的试卷信息
            ExamPaper examPaper = examPapers.stream()
                    .filter(paper -> paper.getId().equals(distribution.getPaperId()))
                    .findFirst()
                    .orElse(null);
            
            if (examPaper != null) {
                StudentExamPaperListVo vo = new StudentExamPaperListVo();
                // 复制试卷信息
                vo.setId(examPaper.getId());
                vo.setPaperName(examPaper.getPaperName());
                vo.setSubject(examPaper.getSubject());
                vo.setDifficulty(examPaper.getDifficulty());
                vo.setTotalScore(examPaper.getTotalScore());
                vo.setTimeLimit(examPaper.getTimeLimit());
                vo.setCreateUserId(examPaper.getCreateUserId());
                vo.setCreateTime(examPaper.getCreateTime());
                vo.setUpdateTime(examPaper.getUpdateTime());
                vo.setIsEnabled(examPaper.getIsEnabled());
                
                // 设置试卷分配信息
                vo.setDistributeTime(distribution.getDistributeTime());
                vo.setStartTime(distribution.getStartTime());
                vo.setDeadline(distribution.getDeadline());
                vo.setRemark(distribution.getRemark());
                vo.setDistributorId(distribution.getDistributorId());
                
                result.add(vo);
            }
        }
        
        return result;
    }
}