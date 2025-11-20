package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.ExamPaperQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamPaperQuestionMapper extends BaseMapper<ExamPaperQuestion> {
    
    /**
     * 根据试卷ID查询所有题目
     * @param paperId 试卷ID
     * @return 题目列表
     */
    @Select("SELECT * FROM exam_paper_question WHERE paper_id = #{paperId} ORDER BY sort_num ASC")
    List<ExamPaperQuestion> selectByPaperId(@Param("paperId") Long paperId);
}