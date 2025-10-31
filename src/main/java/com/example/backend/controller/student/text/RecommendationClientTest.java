package com.example.backend.controller.student.text;

import com.example.backend.common.Result;
import com.example.backend.controller.student.dto.StudentLCRecommendLearnResourceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试类用于调用Python后端的推荐接口
 */
public class RecommendationClientTest {

    // Python后端服务的基础URL
    private static final String PYTHON_BACKEND_URL = "http://localhost:5000"; // 默认Flask端口，请根据实际情况修改
    
    /**
     * 测试获取用户推荐资源列表的方法
     * @param userKey 用户标识
     * @return 封装后的Result对象
     */
    public Result<List<StudentLCRecommendLearnResourceDto>> getRecommendations(String userKey) {
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
                        List<StudentLCRecommendLearnResourceDto> resourceList = new ArrayList<>();
                        
                        // 转换为DTO对象列表
                        for (Map<String, Object> item : rawData) {
                            StudentLCRecommendLearnResourceDto resource = new StudentLCRecommendLearnResourceDto();
                            resource.setFormId((Integer) item.get("form_type"));
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
     * 主方法，用于简单测试
     */
    public static void main(String[] args) {
        RecommendationClientTest clientTest = new RecommendationClientTest();
        
        // 示例：测试用户"S0001"的推荐资源
        Result<List<StudentLCRecommendLearnResourceDto>> result = clientTest.getRecommendations("S0001");
        
        if (result.getSuccess()) {
            System.out.println("调用成功，获得资源数量: " + result.getData().size());
            for (StudentLCRecommendLearnResourceDto resource : result.getData()) {
                System.out.println("资源类型: " + resource.getFormId() + ", 资源标识: " + resource.getResourceKey());
            }
        } else {
            System.out.println("调用失败: " + result.getMessage());
        }
    }
}