package com.example.backend.controller.teacher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.common.Result;
import com.example.backend.controller.teacher.dto.TeacherUserKnowledgeAccuracyDto;
import com.example.backend.entity.Knowledge;
import com.example.backend.entity.UserKnowledgeStats20d;
import com.example.backend.mapper.KnowledgesMapper;
import com.example.backend.mapper.UserKnowledgeStats20dMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 教师端 - 学生知识点正确率查看接口
 */
@RestController
@RequestMapping("/api/teacher/user-knowledge-stats")
public class TeacherUserKnowledgeStatsController {

    private static final Logger log = LoggerFactory.getLogger(TeacherUserKnowledgeStatsController.class);

    @Autowired
    private UserKnowledgeStats20dMapper userKnowledgeStats20dMapper;

    @Autowired
    private KnowledgesMapper knowledgesMapper;

    /**
     * 根据学生 userKey 查询其在近 20 天内的知识点正确率
     *
     * @param userKey 学生标识
     * @return 知识点正确率列表
     */
    @GetMapping("/{userKey}")
    public Result<List<TeacherUserKnowledgeAccuracyDto>> getUserKnowledgeAccuracy(@PathVariable String userKey) {
        try {
            log.info("教师端查询学生知识点正确率, userKey={}", userKey);

            QueryWrapper<UserKnowledgeStats20d> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_key", userKey);
            List<UserKnowledgeStats20d> statsList = userKnowledgeStats20dMapper.selectList(queryWrapper);

            if (statsList == null || statsList.isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            // 按知识点聚合近 20 天的正确次数和总次数
            Map<String, UserKnowledgeStats20d> aggregatedMap = new HashMap<>();
            for (UserKnowledgeStats20d stats : statsList) {
                String knowledgeKey = stats.getKnowledgeKey();
                if (knowledgeKey == null) {
                    continue;
                }
                UserKnowledgeStats20d agg = aggregatedMap.get(knowledgeKey);
                if (agg == null) {
                    agg = new UserKnowledgeStats20d();
                    agg.setUserKey(userKey);
                    agg.setKnowledgeKey(knowledgeKey);
                    agg.setCorrectCount(Optional.ofNullable(stats.getCorrectCount()).orElse(0));
                    agg.setTotalCount(Optional.ofNullable(stats.getTotalCount()).orElse(0));
                    aggregatedMap.put(knowledgeKey, agg);
                } else {
                    int correct = Optional.ofNullable(agg.getCorrectCount()).orElse(0)
                            + Optional.ofNullable(stats.getCorrectCount()).orElse(0);
                    int total = Optional.ofNullable(agg.getTotalCount()).orElse(0)
                            + Optional.ofNullable(stats.getTotalCount()).orElse(0);
                    agg.setCorrectCount(correct);
                    agg.setTotalCount(total);
                }
            }

            // 批量查询知识点名称
            List<String> knowledgeKeys = new ArrayList<>(aggregatedMap.keySet());
            Map<String, String> knowledgeNameMap = new HashMap<>();
            if (!knowledgeKeys.isEmpty()) {
                QueryWrapper<Knowledge> knowledgeQuery = new QueryWrapper<>();
                knowledgeQuery.in("knowledge_key", knowledgeKeys);
                List<Knowledge> knowledgeList = knowledgesMapper.selectList(knowledgeQuery);
                if (knowledgeList != null) {
                    knowledgeNameMap = knowledgeList.stream()
                            .filter(k -> k.getKnowledgeKey() != null)
                            .collect(Collectors.toMap(
                                    Knowledge::getKnowledgeKey,
                                    k -> Optional.ofNullable(k.getName()).orElse(""),
                                    (a, b) -> a
                            ));
                }
            }

            List<TeacherUserKnowledgeAccuracyDto> resultList = new ArrayList<>();
            for (UserKnowledgeStats20d agg : aggregatedMap.values()) {
                int total = Optional.ofNullable(agg.getTotalCount()).orElse(0);
                int correct = Optional.ofNullable(agg.getCorrectCount()).orElse(0);

                double accuracy = 0.0;
                if (total > 0) {
                    accuracy = BigDecimal.valueOf(correct * 100.0 / total)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                }

                TeacherUserKnowledgeAccuracyDto dto = new TeacherUserKnowledgeAccuracyDto();
                dto.setUserKey(userKey);
                dto.setKnowledgeKey(agg.getKnowledgeKey());
                dto.setKnowledgeName(knowledgeNameMap.getOrDefault(agg.getKnowledgeKey(), agg.getKnowledgeKey()));
                dto.setCorrectCount(correct);
                dto.setTotalCount(total);
                dto.setAccuracy(accuracy);
                resultList.add(dto);
            }

            // 按正确率从高到低排序
            resultList.sort(Comparator.comparing(TeacherUserKnowledgeAccuracyDto::getAccuracy).reversed());

            return Result.success(resultList);
        } catch (Exception e) {
            log.error("查询学生知识点正确率失败, userKey={}", userKey, e);
            return Result.error("查询学生知识点正确率失败: " + e.getMessage());
        }
    }
}


