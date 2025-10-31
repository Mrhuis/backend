package com.example.backend.service.student.lc_recommend_learn;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnResourceDto;

import java.util.List;

/**
 * Python模型API服务接口
 */
public interface StudentLCPyModelApiService {
    Result<List<StudentLCRecommendLearnResourceDto>> getRecommendations(String userKey);
    
    /**
     * 标记用户推荐资源为已使用
     * @param userKey 用户标识
     * @param resourceKey 资源标识
     * @param formKey 资源类型标识
     * @return 操作结果
     */
    Result<String> markResourceAsRight(String userKey, String resourceKey, String formKey);
    
    /**
     * 重置用户所有推荐资源的使用状态
     * @param userKey 用户标识
     * @return 操作结果
     */
    Result<String> resetUserResources(String userKey);
}