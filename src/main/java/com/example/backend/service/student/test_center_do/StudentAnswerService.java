package com.example.backend.service.student.test_center_do;

import com.example.backend.entity.StudentAnswer;

public interface StudentAnswerService {
    /**
     * 根据user_key、paper_id、item_key查询对应数据，
     * 如果有数据，则更新答案，如果没数据则插入新数据
     *
     * @param studentAnswer 学生答案对象
     * @return 保存后的学生答案对象
     */
    StudentAnswer saveOrUpdateAnswer(StudentAnswer studentAnswer);

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
     * 完成考试，将指定用户和试卷的所有答题记录标记为已完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     */
    void completeExam(String userKey, Long paperId);
    
    /**
     * 检查考试是否已完成
     *
     * @param userKey 用户标识
     * @param paperId 试卷ID
     * @return true表示已完成，false表示未完成
     */
    boolean isExamCompleted(String userKey, Long paperId);
}