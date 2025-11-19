package com.example.backend.service.teacher.test_manage;

import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaperDistribution;

import java.util.List;

/**
 * 教师端试卷下发管理服务接口
 */
public interface TeacherExamPaperDistributionService {
    /**
     * 获取试卷下发列表
     *
     * @param req 查询条件
     * @return 试卷下发列表
     */
    List<ExamPaperDistribution> getExamPaperDistributionList(TeacherExamPaperDistributionQueryListDto req);

    /**
     * 获取试卷下发总数
     *
     * @param req 查询条件
     * @return 试卷下发总数
     */
    Long getExamPaperDistributionsCount(TeacherExamPaperDistributionQueryListDto req);

    /**
     * 添加试卷下发
     *
     * @param req 试卷下发信息
     * @return 是否添加成功
     */
    boolean addExamPaperDistribution(TeacherExamPaperDistributionAddDto req);

    /**
     * 更新试卷下发
     *
     * @param req 试卷下发信息
     * @return 是否更新成功
     */
    boolean updateExamPaperDistribution(TeacherExamPaperDistributionUpdateDto req);

    /**
     * 删除试卷下发
     *
     * @param id 试卷下发ID
     * @return 是否删除成功
     */
    boolean deleteExamPaperDistributionById(Long id);

    /**
     * 根据ID获取试卷下发详情
     *
     * @param id 试卷下发ID
     * @return 试卷下发详情
     */
    ExamPaperDistribution getExamPaperDistributionById(Long id);

    /**
     * 回收试卷
     *
     * @param req 回收信息
     * @return 是否回收成功
     */
    boolean recycleExamPaperDistribution(TeacherExamPaperDistributionRecycleDto req);
}