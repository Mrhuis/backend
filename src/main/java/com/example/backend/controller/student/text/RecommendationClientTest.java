package com.example.backend.controller.student.text;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
     */
    public void getRecommendations(String userKey) {
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
                        System.out.println("成功获取用户 " + userKey + " 的推荐资源:");
                        System.out.println("数据: " + responseBody.get("data"));
                        System.out.println("消息: " + responseBody.get("message"));
                    } else {
                        System.out.println("获取推荐资源失败: " + responseBody.get("message"));
                    }
                }
            } else {
                System.out.println("HTTP错误状态码: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.err.println("请求发生异常: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("处理响应时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 主方法，用于简单测试
     */
    public static void main(String[] args) {
        RecommendationClientTest clientTest = new RecommendationClientTest();
        
        // 示例：测试用户"testUser"的推荐资源
        // 请根据实际情况替换为真实的userKey
        clientTest.getRecommendations("S0001");
    }
}