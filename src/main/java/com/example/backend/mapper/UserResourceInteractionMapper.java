package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.UserResourceInteraction;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserResourceInteractionMapper
 * Package: com.example.backend.mapper
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/29 16:12
 * @Version 1.0
 */
@Mapper
public interface UserResourceInteractionMapper extends BaseMapper< UserResourceInteraction> {

}
