package com.example.backend.service.student.lc_recommend_learn.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.Result;
import com.example.backend.entity.UserResourcePreference7d;
import com.example.backend.mapper.UserResourcePreference7dMapper;
import com.example.backend.service.student.lc_recommend_learn.StudentLCRLUserResourcePreference7dService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ClassName: StudentLCUserResourcePreference7dImpl
 * Package: com.example.backend.service.student.lc_recommend_learn.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/10/30 17:58
 * @Version 1.0
 */
@Service
public class StudentLCRLUserResourcePreference7DServiceImpl implements StudentLCRLUserResourcePreference7dService {

    @Autowired
    UserResourcePreference7dMapper userResourcePreference7dMapper;
    @Override
    public Result updateUserResourcePreference(String userKey, String formKey) {

        UserResourcePreference7d userResourcePreference7d;

        //根据userKey和formKey查找出对应的userResourcePreference7d，如果没有则创建一个插入到表中
        // 添加record_time条件，确保在当前日期的时间范围内
        QueryWrapper<UserResourcePreference7d> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key", userKey);
        queryWrapper.eq("form_key", formKey);
        
        // 获取今天的开始和结束时间
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        queryWrapper.ge("record_time", startOfDay).lt("record_time", endOfDay);
        
        userResourcePreference7d = userResourcePreference7dMapper.selectOne(queryWrapper);
        if (userResourcePreference7d == null) {
            userResourcePreference7d = new UserResourcePreference7d();
            userResourcePreference7d.setUserKey(userKey);
            userResourcePreference7d.setFormKey(formKey);
            userResourcePreference7d.setClickCount(0);
            userResourcePreference7d.setRecordTime(LocalDateTime.now());

        }
        // 因为点击了一次资源，所以clickCount加1
        userResourcePreference7d.setClickCount(userResourcePreference7d.getClickCount() + 1);


        // 保存到数据库
        if (userResourcePreference7d.getId() == null) {
            userResourcePreference7dMapper.insert(userResourcePreference7d);
        } else {
            userResourcePreference7dMapper.updateById(userResourcePreference7d);
        }

        return Result.success();
    }
}
