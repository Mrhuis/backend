package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.KnowledgeResources;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface KnowledgeResourcesMapper extends BaseMapper<KnowledgeResources> {
    @Select("SELECT kr.knowledge_key as knowledgeKey, k.name as name " +
            "FROM knowledge_resources kr " +
            "JOIN knowledges k ON kr.knowledge_key = k.knowledge_key " +
            "WHERE kr.resource_key = #{resourceKey} " +
            "AND kr.resource_type = #{resourceType} " +
            "AND k.status = 'ENABLED'")
    List<HashMap<String, String>> selectKnowledgeKeyAndNameByResourceKey(String resourceKey,String resourceType);

    @Select("SELECT DISTINCT kr.resource_key " +
            "FROM knowledge_resources kr " +
            "JOIN knowledges k ON kr.knowledge_key = k.knowledge_key " +  // 关联knowledges表以判断status
            "WHERE kr.knowledge_key = #{knowledgeKey} " +
            "AND kr.resource_type = #{resourceType} " +
            "AND k.status = 'ENABLED'")  // 添加知识点状态为ENABLED的条件
    List<String> selectResourceKeysByKnowledgeKey(String knowledgeKey, String resourceType);
}