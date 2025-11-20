package com.example.backend.service.teacher.test_grading.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.controller.student.dto.StudentLCAutonomousLearnRecodeDto;
import com.example.backend.controller.teacher.vo.TeacherTestGradingUserVo;
import com.example.backend.entity.*;
import com.example.backend.entity.Class;
import com.example.backend.mapper.*;
import com.example.backend.service.student.lc_autonomous_learn.StudentLCALKnowledgeResourcesService;
import com.example.backend.service.student.lc_autonomous_learn.StudentLCALUserKnowledgeStats20dService;
import com.example.backend.service.student.test_center_do.ExamPaperQuestionService;
import com.example.backend.service.teacher.test_grading.TeacherTestGradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师端试卷批改服务实现类
 */
@Service
public class TeacherTestGradingServiceImpl extends ServiceImpl<StudentAnswerMapper, StudentAnswer> implements TeacherTestGradingService {

    private static final Logger log = LoggerFactory.getLogger(TeacherTestGradingServiceImpl.class);

    private final ExamPaperDistributionMapper examPaperDistributionMapper;
    private final ClassMapper classMapper;
    private final ClassStudentEnrollmentMapper classStudentEnrollmentMapper;
    private final UserMapper userMapper;
    private final ExamPaperQuestionService examPaperQuestionService;
    private final StudentAnswerMapper studentAnswerMapper;
    private final ItemsMapper itemsMapper;
    private final StudentLCALKnowledgeResourcesService studentLCALKnowledgeResourcesService;
    private final StudentLCALUserKnowledgeStats20dService studentLCALUserKnowledgeStats20dService;
    private final ExamPaperQuestionMapper examPaperQuestionMapper;

    public TeacherTestGradingServiceImpl(
            ExamPaperDistributionMapper examPaperDistributionMapper,
            ClassMapper classMapper,
            ClassStudentEnrollmentMapper classStudentEnrollmentMapper,
            UserMapper userMapper,
            ExamPaperQuestionService examPaperQuestionService,
            StudentAnswerMapper studentAnswerMapper,
            ItemsMapper itemsMapper,
            StudentLCALKnowledgeResourcesService studentLCALKnowledgeResourcesService,
            StudentLCALUserKnowledgeStats20dService studentLCALUserKnowledgeStats20dService,
            ExamPaperQuestionMapper examPaperQuestionMapper) {
        this.examPaperDistributionMapper = examPaperDistributionMapper;
        this.classMapper = classMapper;
        this.classStudentEnrollmentMapper = classStudentEnrollmentMapper;
        this.userMapper = userMapper;
        this.examPaperQuestionService = examPaperQuestionService;
        this.studentAnswerMapper = studentAnswerMapper;
        this.itemsMapper = itemsMapper;
        this.studentLCALKnowledgeResourcesService = studentLCALKnowledgeResourcesService;
        this.studentLCALUserKnowledgeStats20dService = studentLCALUserKnowledgeStats20dService;
        this.examPaperQuestionMapper = examPaperQuestionMapper;
    }

    @Override
    public List<Class> getClassesByPaperId(Long paperId) {
        try {
            log.info("开始查询试卷ID为{}的班级列表", paperId);

            // 查询试卷分配信息
            QueryWrapper<ExamPaperDistribution> distributionQuery = new QueryWrapper<>();
            distributionQuery.eq("paper_id", paperId);
            List<ExamPaperDistribution> distributions = examPaperDistributionMapper.selectList(distributionQuery);

            // 提取班级key列表
            List<String> classKeys = distributions.stream()
                    .map(ExamPaperDistribution::getClassKey)
                    .collect(Collectors.toList());

            if (classKeys.isEmpty()) {
                log.info("未找到试卷ID为{}的班级分配信息", paperId);
                return new ArrayList<>();
            }

            // 查询班级信息
            QueryWrapper<Class> classQuery = new QueryWrapper<>();
            classQuery.in("class_key", classKeys);
            List<Class> classes = classMapper.selectList(classQuery);

            log.info("查询到{}个班级", classes.size());
            return classes;
        } catch (Exception e) {
            log.error("查询试卷ID为{}的班级列表失败", paperId, e);
            throw new RuntimeException("查询班级列表失败", e);
        }
    }

    @Override
    public List<TeacherTestGradingUserVo> getStudentsByClassKey(String classKey) {
        try {
            log.info("开始查询班级{}的学生列表", classKey);

            // 查询班级学生关系
            QueryWrapper<ClassStudentEnrollment> enrollmentQuery = new QueryWrapper<>();
            enrollmentQuery.eq("class_key", classKey);
            List<ClassStudentEnrollment> enrollments = classStudentEnrollmentMapper.selectList(enrollmentQuery);

            // 提取学生userKey列表
            List<String> userKeys = enrollments.stream()
                    .map(ClassStudentEnrollment::getUserKey)
                    .collect(Collectors.toList());

            if (userKeys.isEmpty()) {
                log.info("班级{}中未找到学生", classKey);
                return new ArrayList<>();
            }

            // 查询学生信息
            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.in("user_key", userKeys);
            List<User> users = userMapper.selectList(userQuery);

            // 转换为VO对象
            List<TeacherTestGradingUserVo> studentVos = users.stream().map(user -> {
                TeacherTestGradingUserVo vo = new TeacherTestGradingUserVo();
                vo.setUserKey(user.getUserKey());
                vo.setNickname(user.getNickname());
                vo.setRole(user.getRole());
                return vo;
            }).collect(Collectors.toList());

            log.info("查询到{}个学生", studentVos.size());
            return studentVos;
        } catch (Exception e) {
            log.error("查询班级{}的学生列表失败", classKey, e);
            throw new RuntimeException("查询学生列表失败", e);
        }
    }

    @Override
    public List<ExamPaperQuestion> getQuestionsByPaperId(Long paperId) {
        try {
            log.info("开始查询试卷ID为{}的题目列表", paperId);
            List<ExamPaperQuestion> questions = examPaperQuestionService.getQuestionsByPaperId(paperId);
            log.info("查询到{}个题目", questions.size());
            return questions;
        } catch (Exception e) {
            log.error("查询试卷ID为{}的题目列表失败", paperId, e);
            throw new RuntimeException("查询试卷题目失败", e);
        }
    }

    @Override
    public StudentAnswer getAnswerByUserKeyPaperIdItemKey(String userKey, Long paperId, String itemKey) {
        try {
            log.info("开始查询学生答案，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey);
            StudentAnswer result = studentAnswerMapper.selectByUserKeyPaperIdItemKey(userKey, paperId, itemKey);
            log.info("查询学生答案{}成功", result != null ? "成功" : "未找到");
            return result;
        } catch (Exception e) {
            log.error("查询学生答案失败，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey, e);
            throw new RuntimeException("查询学生答案失败", e);
        }
    }

    @Override
    public boolean setAnswerScore(String userKey, Long paperId, String itemKey, BigDecimal score) {
        try {
            log.info("开始设置学生答案得分，userKey={}, paperId={}, itemKey={}, score={}", userKey, paperId, itemKey, score);

            // 查找对应的学生答案记录
            QueryWrapper<StudentAnswer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_key", userKey)
                    .eq("paper_id", paperId)
                    .eq("item_key", itemKey);

            StudentAnswer studentAnswer = studentAnswerMapper.selectOne(queryWrapper);
            if (studentAnswer == null) {
                log.warn("未找到对应的学生答案记录，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey);
                throw new RuntimeException("未找到对应的学生答案记录");
            }

            // 更新得分
            studentAnswer.setScore(score);
            int result = studentAnswerMapper.updateById(studentAnswer);

            // 更新用户画像
            updateStudentKnowledgeProfile(userKey, paperId, itemKey, score, studentAnswer);

            boolean success = result > 0;
            if (success) {
                log.info("设置学生答案得分成功");
            } else {
                log.warn("设置学生答案得分失败");
            }
            return success;
        } catch (Exception e) {
            log.error("设置学生答案得分失败，userKey={}, paperId={}, itemKey={}, score={}", userKey, paperId, itemKey, score, e);
            throw new RuntimeException("设置得分失败", e);
        }
    }

    /**
     * 更新学生知识点掌握情况画像
     *
     * @param userKey         用户标识
     * @param paperId         试卷ID
     * @param itemKey         题目标识
     * @param score           得分
     * @param studentAnswer   学生答题记录
     */
    private void updateStudentKnowledgeProfile(String userKey, Long paperId, String itemKey, BigDecimal score, StudentAnswer studentAnswer) {
        try {
            log.info("开始更新学生知识点掌握情况画像，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey);

            // 获取题目信息
            Item item = itemsMapper.selectByItemKey(itemKey);
            if (item == null) {
                log.warn("未找到题目信息，itemKey={}", itemKey);
                return;
            }

            // 查找试卷题目关联信息，获取题目在试卷中的实际分值
            QueryWrapper<ExamPaperQuestion> examPaperQuestionQuery = new QueryWrapper<>();
            examPaperQuestionQuery.eq("paper_id", paperId).eq("item_key", itemKey);
            ExamPaperQuestion examPaperQuestion = examPaperQuestionMapper.selectOne(examPaperQuestionQuery);
            
            if (examPaperQuestion == null) {
                log.warn("未找到试卷题目关联信息，paperId={}, itemKey={}", paperId, itemKey);
                return;
            }

            // 查找题目关联的知识点
            List<String> knowledgeKeys = studentLCALKnowledgeResourcesService.getKnowledgeKeys("item", itemKey);
            if (knowledgeKeys.isEmpty()) {
                log.info("题目未关联知识点，itemKey={}", itemKey);
                return;
            }

            // 构造更新用户画像所需的DTO
            StudentLCAutonomousLearnRecodeDto recodeDto = new StudentLCAutonomousLearnRecodeDto();
            recodeDto.setUserKey(userKey);
            recodeDto.setFormKey(item.getFormKey());
            recodeDto.setResourceKey(itemKey);
            recodeDto.setIsComplete(true);
            // 判断答题是否正确（通过比较教师评分和题目实际分值）
            recodeDto.setIsCorrect(score != null && 
                                   examPaperQuestion.getActualScore() != null && 
                                   score.compareTo(BigDecimal.valueOf(examPaperQuestion.getActualScore())) == 0);

            // 更新用户知识点掌握情况
            studentLCALUserKnowledgeStats20dService.updateUserKnowledgeStats(recodeDto, knowledgeKeys);

            log.info("更新学生知识点掌握情况画像成功");
        } catch (Exception e) {
            log.error("更新学生知识点掌握情况画像失败，userKey={}, paperId={}, itemKey={}", userKey, paperId, itemKey, e);
        }
    }
}