package com.example.backend.service.student.lc_recommend_learn.impl;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRLRecommendLearnResourceDto;
import com.example.backend.entity.ResourceForm;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.service.student.lc_recommend_learn.StudentLCRLPyModelApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Python模型API服务实现类
 */
@Service
public class StudentLCRLPyModelApiServiceImpl implements StudentLCRLPyModelApiService {

    @Autowired
    private ResourceFormMapper resourceFormMapper;

    // Python后端服务的基础URL
    private static final String PYTHON_BACKEND_URL = "http://localhost:5000"; // 默认Flask端口，请根据实际情况修改

    /**
     * 获取用户推荐资源列表的方法
     * @param userKey 用户标识
     * @return 封装后的Result对象
     */
    @Override
    public Result<List<StudentLCRLRecommendLearnResourceDto>> getRecommendations(String userKey) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 构建完整的请求URL
            String url = PYTHON_BACKEND_URL + "/recommendations/" + userKey;

            // 发送GET请求并接收响应
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    boolean success = (Boolean) responseBody.get("success");
                    if (success) {
                        // 解析数据部分
                        List<Map<String, Object>> rawData = (List<Map<String, Object>>) responseBody.get("data");
                        List<StudentLCRLRecommendLearnResourceDto> resourceList = new ArrayList<>();
                        
                        // 转换为DTO对象列表
                        for (Map<String, Object> item : rawData) {
                            StudentLCRLRecommendLearnResourceDto resource = new StudentLCRLRecommendLearnResourceDto();
                            resource.setFormId((Integer) item.get("form_id"));
                            resource.setResourceKey((String) item.get("resource_key"));
                            resourceList.add(resource);
                        }
                        
                        System.out.println("成功获取用户 " + userKey + " 的推荐资源:");
                        System.out.println("数据: " + resourceList);
                        System.out.println("消息: " + responseBody.get("message"));
                        
                        // 封装成Result对象
                        return Result.success(responseBody.get("message").toString(), resourceList);
                    } else {
                        String errorMessage = (String) responseBody.get("message");
                        System.out.println("获取推荐资源失败: " + errorMessage);
                        return Result.error(errorMessage, new ArrayList<>());
                    }
                }
            } else {
                System.out.println("HTTP错误状态码: " + response.getStatusCode());
                return Result.error("HTTP错误状态码: " + response.getStatusCode(), new ArrayList<>());
            }
        } catch (RestClientException e) {
            System.err.println("请求发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("请求发生异常: " + e.getMessage(), new ArrayList<>());
        } catch (Exception e) {
            System.err.println("处理响应时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("处理响应时发生异常: " + e.getMessage(), new ArrayList<>());
        }
        
        return Result.error("未知错误", new ArrayList<>());
    }
    
    /**
     * 标记用户推荐资源为已正确
     * @param userKey 用户标识
     * @param resourceKey 资源标识
     * @param formKey 资源类型标识
     * @return 操作结果
     */
    @Override
    public Result<String> markResourceAsRight(String userKey, String resourceKey, String formKey) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            System.out.println("准备标记资源为已使用: userKey=" + userKey + ", resourceKey=" + resourceKey + ", formKey=" + formKey);
            
            ResourceForm form = resourceFormMapper.selectIdByKey(formKey);
            
            // 检查ResourceForm对象是否存在
            if (form == null) {
                String errorMessage = "未找到formKey为 " + formKey + " 的资源类型";
                System.err.println(errorMessage);
                return Result.error(errorMessage);
            }
            
            System.out.println("找到ResourceForm: id=" + form.getId() + ", formKey=" + form.getFormKey() + ", formName=" + form.getFormName());
            
            // 检查ID是否存在
            if (form.getId() == null) {
                String errorMessage = "formKey为 " + formKey + " 的资源类型ID为空";
                System.err.println(errorMessage);
                return Result.error(errorMessage);
            }
            
            // 构建完整的请求URL，确保ID被正确转换为字符串
            String url = PYTHON_BACKEND_URL + "/recommendations/" + userKey + "/" + resourceKey + "/" + String.valueOf(form.getId()) + "/mark_used";
            System.out.println("构建的请求URL: " + url);
            
            // 发送POST请求并接收响应
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            System.out.println("收到Python服务响应: statusCode=" + response.getStatusCode() + ", body=" + response.getBody());
            
            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    boolean success = (Boolean) responseBody.get("success");
                    String message = (String) responseBody.get("message");
                    
                    if (success) {
                        System.out.println("成功标记资源为已使用: " + message);
                        return Result.success(message);
                    } else {
                        System.out.println("标记资源为已使用失败: " + message);
                        return Result.error(message);
                    }
                }
            } else {
                System.out.println("HTTP错误状态码: " + response.getStatusCode());
                return Result.error("HTTP错误状态码: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("请求发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("请求发生异常: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("处理响应时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("处理响应时发生异常: " + e.getMessage());
        }
        
        return Result.error("未知错误");
    }
    
    /**
     * 重置用户所有推荐资源的使用状态
     * @param userKey 用户标识
     * @return 操作结果
     */
    @Override
    public Result<String> resetUserResources(String userKey) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // 构建完整的请求URL
            String url = PYTHON_BACKEND_URL + "/recommendations/" + userKey + "/reset_usage";
            
            // 发送POST请求并接收响应
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    boolean success = (Boolean) responseBody.get("success");
                    String message = (String) responseBody.get("message");
                    
                    if (success) {
                        System.out.println("成功重置用户资源使用状态: " + message);
                        return Result.success(message);
                    } else {
                        System.out.println("重置用户资源使用状态失败: " + message);
                        return Result.error(message);
                    }
                }
            } else {
                System.out.println("HTTP错误状态码: " + response.getStatusCode());
                return Result.error("HTTP错误状态码: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("请求发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("请求发生异常: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("处理响应时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("处理响应时发生异常: " + e.getMessage());
        }
        
        return Result.error("未知错误");
    }
}