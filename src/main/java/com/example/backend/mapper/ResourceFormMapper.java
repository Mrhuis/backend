package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.ResourceForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ResourceFormMapper extends BaseMapper<ResourceForm> {

    @Select("SELECT * FROM resource_form WHERE form_key = #{formKey}")
    ResourceForm selectIdByKey(String formKey);
}