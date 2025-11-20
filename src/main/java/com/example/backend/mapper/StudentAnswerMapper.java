package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentAnswerMapper extends BaseMapper<StudentAnswer> {
    
    @Select("SELECT * FROM student_answer WHERE user_key = #{userKey} AND paper_id = #{paperId} AND item_key = #{itemKey} LIMIT 1")
    StudentAnswer selectByUserKeyPaperIdItemKey(@Param("userKey") String userKey, @Param("paperId") Long paperId, @Param("itemKey") String itemKey);
}