package com.example.backend.service.student.test_center_do;

import com.example.backend.controller.student.vo.StudentExamPaperListVo;
import java.util.List;

public interface StudentExamPaperService {
    /**
     * 根据学生userKey查询该学生所属班级的试卷分配信息
     * @param userKey 学生userKey
     * @return 试卷分配信息列表
     */
    List<StudentExamPaperListVo> getExamPapersByStudentUserKey(String userKey);

    /**
     * 根据学生userKey和试卷ID检查试卷是否被回收
     * @param userKey 学生userKey
     * @param paperId 试卷ID
     * @return 是否已回收（true-已回收，false-未回收）
     */
    boolean isPaperRecycled(String userKey, Long paperId);
}