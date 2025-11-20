package com.example.backend.service.student.test_center_result;

import com.example.backend.common.PageResult;
import com.example.backend.controller.student.vo.StudentCompletedPaperVO;
import com.example.backend.entity.StudentAnswer;

import java.util.List;
import java.util.Optional;

/**
 * 学生测试中心结果-学生答案服务接口
 */
public interface StudentAnswerService {
    /**
     * 根据user_key、paper_id、item_key查询对应答案
     *
     * @param userKey  用户标识
     * @param paperId  试卷ID
     * @param itemKey  习题标识
     * @return 学生答案对象
     */
    StudentAnswer getAnswerByUserKeyPaperIdItemKey(String userKey, Long paperId, String itemKey);
    
    /**
     * 检查试卷是否已评分完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return true表示已评分完成，false表示未评分完成
     */
    boolean isPaperGraded(String userKey, Long paperId);
    
    /**
     * 根据用户key获取已完成的试卷列表
     *
     * @param userKey 用户key
     * @return 已完成的试卷列表
     */
    List<StudentAnswer> getCompletedPapersByUserKey(String userKey);

    /**
     * 分页查询已完成试卷
     *
     * @param userKey 用户标识
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    PageResult<StudentCompletedPaperVO> getCompletedPapersPage(String userKey, int page, int size);

    /**
     * 查询单份试卷的完成情况汇总
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return 汇总信息
     */
    Optional<StudentCompletedPaperVO> getCompletedPaperSummary(String userKey, Long paperId);
}