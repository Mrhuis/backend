package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.MediaAssets;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MediaAssetsMapper extends BaseMapper<MediaAssets> {

    /**
     * 根据媒体key和表单key查询媒体资源
     * @param mediaKey 媒体key
     * @param formKey 表单key
     * @return 媒体资源对象
     */
    @Select("SELECT * FROM media_assets WHERE media_key = #{mediaKey} AND form_key = #{formKey} AND status = 'ENABLED'")
    MediaAssets selectByMediaKeyAndFormKey(@Param("mediaKey") String mediaKey, @Param("formKey") String formKey);

}