package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserMapper
 * Package: com.example.backend.mapper
 * Description:
 *
 * @Author 
 * @Create 
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}