package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: TagMapper
 * Package: com.example.backend.mapper
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/8 14:33
 * @Version 1.0
 */
@Mapper
public interface TagsMapper extends BaseMapper<Tag> {
}
