package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.ExamPaperDistribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamPaperDistributionMapper extends BaseMapper<ExamPaperDistribution> {
    
    /**
     * 根据班级classKey列表查询试卷分配信息
     * @param classKeys 班级classKey列表
     * @return 试卷分配列表
     */
    @Select("<script>" +
            "SELECT * FROM exam_paper_distribution WHERE class_key IN " +
            "<foreach item='classKey' collection='classKeys' open='(' separator=',' close=')'>" +
            "#{classKey}" +
            "</foreach>" +
            "</script>")
    List<ExamPaperDistribution> selectByClassKeys(@Param("classKeys") List<String> classKeys);
}