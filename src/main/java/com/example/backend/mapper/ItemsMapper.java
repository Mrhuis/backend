package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ItemsMapper extends BaseMapper<Item> {

    /**
     * 根据习题key和表单key查询习题资源
     * @param itemKey 习题key
     * @param formKey 表单key
     * @return 习题资源对象
     */
    @Select("SELECT * FROM items WHERE item_key = #{itemKey} AND form_key = #{formKey} AND status = 'ENABLED'")
    Item selectByItemKeyAndFormKey(@Param("itemKey") String itemKey, @Param("formKey") String formKey);

}