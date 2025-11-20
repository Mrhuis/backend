package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.ClassStudentEnrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassStudentEnrollmentMapper extends BaseMapper<ClassStudentEnrollment> {
    
    /**
     * 根据学生userKey查询所属班级
     * @param userKey 学生userKey
     * @return 班级列表
     */
    @Select("SELECT * FROM class_student_enrollments WHERE user_key = #{userKey}")
    List<ClassStudentEnrollment> selectByUserKey(@Param("userKey") String userKey);
}