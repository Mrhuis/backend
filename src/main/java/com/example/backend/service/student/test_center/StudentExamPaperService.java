package com.example.backend.service.student.test_center;

import com.example.backend.controller.student.vo.StudentExamPaperListVo;
import java.util.List;

public interface StudentExamPaperService {
    /**
     * 根据学生userKey查询该学生所属班级的试卷分配信息
     * @param userKey 学生userKey
     * @return 试卷分配信息列表
     */
    List<StudentExamPaperListVo> getExamPapersByStudentUserKey(String userKey);
}