package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.ChapterResources;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ChapterResourcesMapper extends BaseMapper<ChapterResources> {
    @Select("SELECT " +
            "  CASE WHEN c.status = 'DISABLED' THEN 'other' ELSE cr.chapter_key END AS chapterKey, " +
            "  CASE WHEN c.status = 'DISABLED' THEN '其他' ELSE c.name END AS name " +
            "FROM chapter_resources cr " +
            "JOIN chapters c ON cr.chapter_key = c.chapter_key " +
            "WHERE cr.resource_key = #{resourceKey} " +
            "AND cr.resource_type = #{resourceType} " +
            "AND c.status IN ('ENABLED', 'DISABLED')")
    List<HashMap<String, String>> selectChapterKeyAndChapterNameByResourceKey(String resourceKey,String resourceType);

    @Select("<script>" +
            "SELECT DISTINCT cr.resource_key " +
            "FROM chapter_resources cr " +
            "JOIN chapters c ON cr.chapter_key = c.chapter_key " +  // 关联chapters表以判断status
            "WHERE cr.resource_type = #{resourceType} " +
            "<if test='chapterKey == \"other\"'>" +  // 当chapterKey为other时
            "  AND c.status = 'DISABLED'" +
            "</if>" +
            "<if test='chapterKey != \"other\"'>" +  // 当chapterKey为普通值时
            "  AND cr.chapter_key = #{chapterKey} " +
            "  AND c.status = 'ENABLED'" +  // 添加章节状态为ENABLED的条件
            "</if>" +
            "</script>")
    List<String> selectResourceKeysByChapterKey(String chapterKey, String resourceType);


}