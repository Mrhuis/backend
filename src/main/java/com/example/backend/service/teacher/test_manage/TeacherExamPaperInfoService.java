package com.example.backend.service.teacher.test_manage;

import com.example.backend.controller.teacher.dto.*;
import com.example.backend.entity.ExamPaper;
import com.example.backend.entity.ExamPaperQuestion;

import java.util.List;

/**
 * 教师端试卷信息管理服务接口
 */
public interface TeacherExamPaperInfoService {
    /**
     * 获取试卷列表
     *
     * @param req 查询条件
     * @return 试卷列表
     */
    List<ExamPaper> getExamPaperList(TeacherExamPaperQueryListDto req);

    /**
     * 获取试卷总数
     *
     * @param req 查询条件
     * @return 试卷总数
     */
    Long getExamPapersCount(TeacherExamPaperQueryListDto req);

    /**
     * 添加试卷
     *
     * @param req 试卷信息
     * @return 是否添加成功
     */
    boolean addExamPaper(TeacherExamPaperAddDto req);

    /**
     * 更新试卷
     *
     * @param req 试卷信息
     * @return 是否更新成功
     */
    boolean updateExamPaper(TeacherExamPaperUpdateDto req);

    /**
     * 删除试卷
     *
     * @param id 试卷ID
     * @return 是否删除成功
     */
    boolean deleteExamPaperById(Long id);

    /**
     * 根据ID获取试卷详情
     *
     * @param id 试卷ID
     * @return 试卷详情
     */
    ExamPaper getExamPaperById(Long id);
}