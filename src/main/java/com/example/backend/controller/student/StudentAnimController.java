package com.example.backend.controller.student;
//
//import com.example.backend.common.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 学生动画页面控制器
// */
//@RestController
//@RequestMapping("/api/student/anim")
//@CrossOrigin(originPatterns = "*")
//public class StudentAnimController {
//
//    @Autowired
//    private AnimationService animationService;
//
//    /**
//     * 获取动画列表
//     */
//    @GetMapping("/animations")
//    public Result<List<AnimationListDTO>> getAnimations() {
//        try {
//            List<AnimationListDTO> animations = animationService.getAnimationList();
//            return Result.success(animations);
//        } catch (Exception e) {
//            return Result.error("获取动画列表失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画详情
//     */
//    @GetMapping("/animations/{animationId}")
//    public Result<AnimationDetailDTO> getAnimationDetail(@PathVariable String animationId) {
//        try {
//            AnimationDetailDTO animation = animationService.getAnimationDetail(animationId);
//            if (animation != null) {
//                return Result.success(animation);
//            } else {
//                return Result.error("动画不存在");
//            }
//        } catch (Exception e) {
//            return Result.error("获取动画详情失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画播放历史
//     */
//    @GetMapping("/history")
//    public Result<Map<String, Object>> getPlayHistory(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String animationId) {
//        try {
//            Map<String, Object> history = animationService.getPlayHistory(page, size, animationId);
//            return Result.success(history);
//        } catch (Exception e) {
//            return Result.error("获取播放历史失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 保存播放记录
//     */
//    @PostMapping("/record")
//    public Result<String> savePlayRecord(@RequestBody PlayRecordDTO playRecord) {
//        try {
//            animationService.savePlayRecord(playRecord);
//            return Result.success("播放记录保存成功");
//        } catch (Exception e) {
//            return Result.error("保存播放记录失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画统计信息
//     */
//    @GetMapping("/stats")
//    public Result<Map<String, Object>> getAnimationStats() {
//        try {
//            Map<String, Object> stats = animationService.getAnimationStats();
//            return Result.success(stats);
//        } catch (Exception e) {
//            return Result.error("获取统计信息失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取推荐动画
//     */
//    @GetMapping("/recommended")
//    public Result<List<AnimationListDTO>> getRecommendedAnimations() {
//        try {
//            List<AnimationListDTO> recommended = animationService.getRecommendedAnimations();
//            return Result.success(recommended);
//        } catch (Exception e) {
//            return Result.error("获取推荐动画失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画分类
//     */
//    @GetMapping("/categories")
//    public Result<List<String>> getAnimationCategories() {
//        try {
//            List<String> categories = animationService.getAnimationCategories();
//            return Result.success(categories);
//        } catch (Exception e) {
//            return Result.error("获取动画分类失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 搜索动画
//     */
//    @GetMapping("/search")
//    public Result<Map<String, Object>> searchAnimations(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String category,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        try {
//            Map<String, Object> result = animationService.searchAnimations(keyword, category, page, size);
//            return Result.success(result);
//        } catch (Exception e) {
//            return Result.error("搜索动画失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画播放配置
//     */
//    @GetMapping("/config/{animationId}")
//    public Result<Map<String, Object>> getAnimationConfig(@PathVariable String animationId) {
//        try {
//            Map<String, Object> config = animationService.getAnimationConfig(animationId);
//            return Result.success(config);
//        } catch (Exception e) {
//            return Result.error("获取动画配置失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 更新动画播放配置
//     */
//    @PutMapping("/config/{animationId}")
//    public Result<String> updateAnimationConfig(
//            @PathVariable String animationId,
//            @RequestBody Map<String, Object> config) {
//        try {
//            animationService.updateAnimationConfig(animationId, config);
//            return Result.success("配置更新成功");
//        } catch (Exception e) {
//            return Result.error("更新配置失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画帧数据
//     */
//    @GetMapping("/frame/{animationId}/{frameIndex}")
//    public Result<Map<String, Object>> getAnimationFrame(
//            @PathVariable String animationId,
//            @PathVariable int frameIndex) {
//        try {
//            Map<String, Object> frame = animationService.getAnimationFrame(animationId, frameIndex);
//            if (frame != null) {
//                return Result.success(frame);
//            } else {
//                return Result.error("帧数据不存在");
//            }
//        } catch (Exception e) {
//            return Result.error("获取帧数据失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取动画缩略图
//     */
//    @GetMapping("/thumbnail/{animationId}")
//    public Result<String> getAnimationThumbnail(@PathVariable String animationId) {
//        try {
//            String thumbnailUrl = animationService.getAnimationThumbnail(animationId);
//            return Result.success(thumbnailUrl);
//        } catch (Exception e) {
//            return Result.error("获取缩略图失败: " + e.getMessage());
//        }
//    }
//}