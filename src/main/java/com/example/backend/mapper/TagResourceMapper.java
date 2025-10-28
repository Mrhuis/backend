package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.TagResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface TagResourceMapper extends BaseMapper<TagResource> {
    @Select("SELECT t.id as id, t.tag_content as tagContent " +
            "FROM tag_resource rt " +
            "JOIN tags t ON rt.tag_id = t.id " +
            "WHERE rt.resource_key = #{resourceKey} " +
            "AND rt.resource_type = #{resourceType} " +
            "AND t.status = 'ENABLED'")
    List<HashMap<String, String>> selectTagIdAndTagContentByResourceKey(String resourceKey,String resourceType);

    @Select("SELECT DISTINCT rt.resource_key " +
            "FROM tag_resource rt " +
            "JOIN tags t ON rt.tag_id = t.id " +  // 关联tags表以判断status
            "WHERE rt.tag_id = #{tagId} " +
            "AND rt.resource_type = #{resourceType} " +
            "AND t.status = 'ENABLED'")  // 添加标签状态为ENABLED的条件
    List<String> selectResourceKeysByTagId(Long tagId, String resourceType);
}