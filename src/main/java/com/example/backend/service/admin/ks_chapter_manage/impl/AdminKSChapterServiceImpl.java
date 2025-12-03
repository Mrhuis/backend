package com.example.backend.service.admin.ks_chapter_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.backend.controller.admin.dto.*;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ChapterMapper;
import com.example.backend.service.admin.ks_chapter_manage.AdminKSChapterService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminKSChapterServiceImpl
 * Package: com.example.backend.service.admin.ks_chapter_manage.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/1 17:16
 * @Version 1.0
 */
@Service
public class AdminKSChapterServiceImpl implements AdminKSChapterService {

    private static final Logger log = LoggerFactory.getLogger(AdminKSChapterServiceImpl.class);

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;

    @Override
    public List<Chapter> getChapterList(AdminKSChapterQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取章节列表 - 当前启用的插件: {}", enabledPlugin);
            
            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>();
            }
            
            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());
            
            QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
            
            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());
            
            // 添加查询条件
            if (req != null) {
                // 章节标识模糊查询
                if (StringUtils.hasText(req.getChapterKey())) {
                    queryWrapper.like("chapter_key", req.getChapterKey());
                }
                
                // 章节名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }
                
                // 层级精确查询
                if (req.getLevel() != null) {
                    queryWrapper.eq("level", req.getLevel());
                }
                
                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }

                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                            .like("chapter_key", searchValue)
                            .or()
                            .like("name", searchValue)
                            .or()
                            .like("parent_chapter_key", searchValue)
                            .or()
                            .like("description", searchValue)
                            .or()
                            .like("uploaded_by", searchValue)
                    );
                }
            }
            
            // 按层级、排序字段排序
            queryWrapper.orderByAsc("level").orderByAsc("sort_order");
            
            // 分页查询
            if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
                queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
            }
            
            log.info("执行查询，SQL条件: {}", queryWrapper.getSqlSegment());
            List<Chapter> chapters = chapterMapper.selectList(queryWrapper);
            log.info("查询完成，返回章节数量: {}", chapters.size());
            
            return chapters;
        } catch (Exception e) {
            log.error("获取章节列表失败", e);
            throw new RuntimeException("获取章节列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Long getChaptersCount(AdminKSChapterQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取章节总数 - 当前启用的插件: {}", enabledPlugin);
            
            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回0");
                return 0L;
            }
            
            QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
            
            // 必须限制为当前启用的插件
            queryWrapper.eq("plugin_key", enabledPlugin.getPluginKey());
            
            // 添加查询条件（与getChapterList保持一致）
            if (req != null) {
                // 章节标识模糊查询
                if (StringUtils.hasText(req.getChapterKey())) {
                    queryWrapper.like("chapter_key", req.getChapterKey());
                }
                
                // 章节名称模糊查询
                if (StringUtils.hasText(req.getName())) {
                    queryWrapper.like("name", req.getName());
                }
                
                // 层级精确查询
                if (req.getLevel() != null) {
                    queryWrapper.eq("level", req.getLevel());
                }
                
                // 状态精确查询
                if (StringUtils.hasText(req.getStatus())) {
                    queryWrapper.eq("status", req.getStatus());
                }
                
                // 通用搜索值（在所有文本字段中搜索）
                if (StringUtils.hasText(req.getSearchValue())) {
                    String searchValue = req.getSearchValue();
                    queryWrapper.and(wrapper -> wrapper
                        .like("chapter_key", searchValue)
                        .or()
                        .like("name", searchValue)
                        .or()
                        .like("parent_chapter_key", searchValue)
                        .or()
                        .like("description", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                    );
                }
            }
            
            Long count = chapterMapper.selectCount(queryWrapper);
            log.info("章节总数查询完成，总数: {}", count);
            
            return count;
        } catch (Exception e) {
            log.error("获取章节总数失败", e);
            throw new RuntimeException("获取章节总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addChapter(AdminKSChapterAddDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加章节");
            }
            
            log.info("添加章节 - 当前启用的插件: {}", enabledPlugin.getPluginKey());
            
            // 调整同层级章节的排序
            adjustSortOrderForInsert(req.getLevel(), req.getParentChapterKey(), req.getSortOrder(), enabledPlugin.getPluginKey());
            
            // 创建章节对象
            Chapter chapter = new Chapter();
            chapter.setChapterKey(req.getChapterKey());
            chapter.setPluginKey(enabledPlugin.getPluginKey());
            chapter.setName(req.getName());
            chapter.setLevel(req.getLevel());
            chapter.setSortOrder(req.getSortOrder());
            chapter.setParentChapterKey(
                    StringUtils.isEmpty(req.getParentChapterKey()) ? null : req.getParentChapterKey()
            );
            chapter.setDescription(req.getDescription());
            chapter.setUploadedBy(req.getUploadedBy());
            // 添加时默认状态为DISABLED
            chapter.setStatus("disabled");
            chapter.setCreatedAt(LocalDateTime.now());
            
            int result = chapterMapper.insert(chapter);
            log.info("章节添加完成，影响行数: {}", result);
            
            return result > 0;
        } catch (Exception e) {
            log.error("添加章节失败", e);
            throw new RuntimeException("添加章节失败: " + e.getMessage(), e);
        }
    }

    /**
     * 插入新元素时，调整同组元素的排序值（类似在"abcde"中插入"g"到指定位置，让后续元素自动后移）
     * 举例：
     * - 原序列：a(1), b(2), c(3), d(4), e(5)（sortOrder对应1-5）
     * - 插入g到位置0（对应sortOrder=1）→ 调整后：g(1), a(2), b(3), c(4), d(5), e(6)
     * - 插入g到位置1（对应sortOrder=2）→ 调整后：a(1), g(2), b(3), c(4), d(5), e(6)
     *
     * @param level 层级（类似"同一组字符"的标识，确保只调整同层级元素）
     * @param parentChapterKey 父标识（类似"嵌套组"的标识，如子序列归属于某个父序列）
     * @param insertSortOrder 新元素要插入的排序值（对应插入位置，如位置0→1，位置1→2）
     * @param pluginKey 插件标识（类似"不同组"的区分，如"abcde"和"xyz"是不同插件的序列）
     */
    private void adjustSortOrderForInsert(Integer level, String parentChapterKey, Integer insertSortOrder, String pluginKey) {
        // 1. 创建查询条件构造器（用于筛选需要调整的元素）
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();

        // 2. 筛选条件1：只处理"同一插件"的元素（如只调整"abcde"，不影响"xyz"）
        queryWrapper.eq("plugin_key", pluginKey)
                // 筛选条件2：只处理"同一层级"的元素（如只调整平级的字符，不涉及嵌套的子字符）
                .eq("level", level);

        // 3. 筛选条件3：根据"父标识"定位具体组（处理嵌套场景）
        if (parentChapterKey != null) {
            // 若有父标识：只处理"同一父元素下的子元素"（如父序列是"a"，只调整其下的子序列）
            queryWrapper.eq("parent_chapter_key", parentChapterKey);
        } else {
            // 若无父标识：只处理"顶级元素"（如"abcde"本身是顶级序列，无父序列）
            queryWrapper.isNull("parent_chapter_key");
        }

        // 4. 筛选条件4：只处理"排序值≥插入位置"的元素（这些元素需要后移）
        // 例如：插入位置是1（insertSortOrder=1），则原sortOrder≥1的a(1)、b(2)...都需要后移
        queryWrapper.ge("sort_order", insertSortOrder)
                // 按排序值升序排列（确保从插入位置开始依次后移，避免顺序错乱）
                // 例如：先处理a(1)→2，再处理b(2)→3，避免b先变成3后a变成2导致顺序反了
                .orderByAsc("sort_order");

        // 5. 执行查询，获取所有需要后移的元素（如插入位置1时，获取a(1)、b(2)、c(3)、d(4)、e(5)）
        List<Chapter> chapters = chapterMapper.selectList(queryWrapper);

        // 6. 遍历需要调整的章节，直接从插入位置开始赋值连续递增的新排序值
        // 逻辑：插入位置是insertSortOrder（新章节占这个位置），后续章节从insertSortOrder+1开始依次+1
        // 举例：插入位置=1（新章节g占1），则第一个调整的章节→2，第二个→3，以此类推
        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            // 计算当前章节的新排序值：插入位置 + 1（跳过新章节的位置） + 循环索引（依次递增）
            int newSortOrder = insertSortOrder + 1 + i;
            // 直接赋值新排序值（不再依赖原排序值+1）
            chapter.setSortOrder(newSortOrder);
            // 更新到数据库（保存调整后的连续排序值）
            chapterMapper.updateById(chapter);
            // 可选：打印调试日志，查看原排序→新排序的映射关系
            // log.debug("章节[{}]排序调整：原{} → 新{}", chapter.getChapterKey(), chapter.getSortOrder(), newSortOrder);
        }
        // 7. 记录日志，说明本次调整了多少个元素（方便排查问题，如插入位置0时应调整5个元素）
        log.info("调整排序完成，共调整 {} 个章节", chapters.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateChapter(AdminKSChapterUpdateDto req) {
        try {
            // 根据id查询章节
            Chapter existingChapter = chapterMapper.selectById(req.getId());
            if (existingChapter == null) {
                throw new RuntimeException("章节不存在，ID: " + req.getId());
            }
            
            log.info("更新章节，ID: {}, 原章节: {}", req.getId(), existingChapter);
            
            // 更新非空字段
            Chapter updateChapter = new Chapter();
            updateChapter.setId(req.getId());

            
            if (StringUtils.hasText(req.getName())) {
                updateChapter.setName(req.getName());
            }
            
            if (req.getLevel() != null) {
                updateChapter.setLevel(req.getLevel());
            }
            
            if (req.getSortOrder() != null) {
                updateChapter.setSortOrder(req.getSortOrder());
            }
            
            if (req.getParentChapterKey() != null) {
                updateChapter.setParentChapterKey(req.getParentChapterKey());
            }
            
            if (StringUtils.hasText(req.getDescription())) {
                updateChapter.setDescription(req.getDescription());
            }
            
            if (StringUtils.hasText(req.getUploadedBy())) {
                updateChapter.setUploadedBy(req.getUploadedBy());
            }
            
            if (StringUtils.hasText(req.getStatus())) {
                updateChapter.setStatus(req.getStatus());
            }
            
            int result = chapterMapper.updateById(updateChapter);
            log.info("章节更新完成，影响行数: {}", result);

            // 审核结果通知上传者（章节资源），仅在状态发生变更且存在上传者时发送
            if (result > 0 && updateChapter.getId() != null) {
                Chapter latest = chapterMapper.selectById(updateChapter.getId());
                if (latest != null
                        && StringUtils.hasText(latest.getUploadedBy())
                        && StringUtils.hasText(latest.getStatus())) {
                    resourceAuditNotifier.notifyAuditResult(
                            latest.getUploadedBy(),
                            "章节资源",
                            latest.getName(),
                            latest.getStatus(),
                            null  // 暂无管理员user_key，使用system作为发送者
                    );
                }
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("更新章节失败", e);
            throw new RuntimeException("更新章节失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteChapterById(Integer id) {
        try {
            // 1. 查询要删除的父章节（获取核心信息，用于后续级联删除和排序调整）
            Chapter parentChapter = chapterMapper.selectById(id);
            if (parentChapter == null) {
                log.warn("删除失败：父章节不存在，ID: {}", id);
                return false;
            }

            // 提取父章节关键信息
            String parentChapterKey = parentChapter.getChapterKey(); // 父章节唯一标识（用于查子章节）
            String pluginKey = parentChapter.getPluginKey(); // 所属插件（排序调整用）
            Integer parentLevel = parentChapter.getLevel(); // 父章节层级（排序调整用）
            Integer deletedSortOrder = parentChapter.getSortOrder(); // 父章节排序值（排序调整用）
            String parentParentKey = parentChapter.getParentChapterKey(); // 父章节的父标识（排序调整用）

            log.info("开始处理章节删除 - 父章节ID: {}, chapterKey: {}, 层级: {}, 排序值: {}",
                    id, parentChapterKey, parentLevel, deletedSortOrder);

            // 2. 核心：级联删除所有直接子章节（父章节key = 当前父章节的chapterKey）
            List<Chapter> childChapters = getChildChaptersByParentKey(parentChapterKey);
            if (!CollectionUtils.isEmpty(childChapters)) {
                // 关键：将 Long 转为 Integer（注意数据范围，见下方提醒）
                List<Integer> childIntegerIds = childChapters.stream()
                        .map(chapter -> chapter.getId().intValue()) // Long → Integer
                        // 或简写：.map(Long::intValue)
                        .collect(Collectors.toList());
                // 此时传入的是 List<Integer>，类型匹配
                int deleteChildCount = chapterMapper.deleteBatchIds(childIntegerIds);
                log.info("级联删除子章节完成：共删除 {} 个子章节", deleteChildCount);
            }

            // 3. 删除目标父章节
            int deleteParentCount = chapterMapper.deleteById(id);
            if (deleteParentCount <= 0) {
                log.error("删除失败：父章节数据库删除无影响行数，ID: {}", id);
                return false;
            }
            log.info("父章节删除完成：ID: {}, chapterKey: {}", id, parentChapterKey);

            // 4. 关键：调整父章节同层级后续章节的排序（直接赋值连续递增排序值，而非单纯减1）
            adjustSortOrderForDelete(pluginKey, parentLevel, parentParentKey, deletedSortOrder);

            return true;
        } catch (Exception e) {
            log.error("删除章节（含级联删除）失败，ID: {}", id, e);
            throw new RuntimeException("删除章节失败: " + e.getMessage(), e);
        }
    }


    /**
     * 按父章节key查询所有直接子章节（用于级联删除）
     * @param parentChapterKey 父章节唯一标识
     * @return 直接子章节列表（不含孙子章节，如需递归删可扩展）
     */
    private List<Chapter> getChildChaptersByParentKey(String parentChapterKey) {
        if (StringUtils.isEmpty(parentChapterKey)) {
            log.warn("查询子章节失败：父章节key为空");
            return null;
        }

        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        // 只查直接子章节：parent_chapter_key 等于目标父章节的chapterKey
        queryWrapper.eq("parent_chapter_key", parentChapterKey);
        // 按排序值升序，便于删除日志展示
        queryWrapper.orderByAsc("sort_order");

        return chapterMapper.selectList(queryWrapper);
    }


    /**
     * 删除父章节后，调整其同层级后续章节的排序（直接赋值连续排序值，确保无断层）
     * 逻辑：筛选出符合条件的后续章节 → 按原排序升序 → 从「被删章节的sortOrder」开始依次赋值连续值
     * 举例：原序列a(1),b(2),c(3),d(4) → 删除b(2) → 后续章节c、d按顺序赋值2、3 → 最终a(1),c(2),d(3)
     *
     * @param pluginKey         所属插件（同插件才调整）
     * @param level             父章节层级（同层级才调整）
     * @param parentChapterKey  父章节的父标识（同父层级才调整，一级章节为null）
     * @param deletedSortOrder  被删父章节的排序值（后续章节从这个值开始重新赋值）
     */
    private void adjustSortOrderForDelete(String pluginKey, Integer level, String parentChapterKey, Integer deletedSortOrder) {
        // 1. 筛选需要调整的后续章节：同插件、同层级、同父标识、排序值>被删章节排序值
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey)
                .eq("level", level);

        // 同父标识（一级章节查null，子章节查对应parentChapterKey）
        if (StringUtils.isEmpty(parentChapterKey)) {
            queryWrapper.isNull("parent_chapter_key");
        } else {
            queryWrapper.eq("parent_chapter_key", parentChapterKey);
        }

        // 只调整被删章节之后的章节（排序值大于被删值）
        queryWrapper.gt("sort_order", deletedSortOrder)
                .orderByAsc("sort_order"); // 按原排序升序，确保赋值顺序正确

        List<Chapter> chaptersToAdjust = chapterMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(chaptersToAdjust)) {
            log.info("无需调整排序：被删章节之后无同层级后续章节（被删排序值: {}）", deletedSortOrder);
            return;
        }
        log.info("待调整排序的后续章节数量：{} 个（被删排序值: {}）", chaptersToAdjust.size(), deletedSortOrder);

        // 2. 直接赋值连续排序值：从「被删章节的sortOrder」开始，依次+1
        for (int i = 0; i < chaptersToAdjust.size(); i++) {
            Chapter chapter = chaptersToAdjust.get(i);
            int oldSortOrder = chapter.getSortOrder();
            // 新排序值 = 被删章节排序值 + 索引（i从0开始，确保连续）
            int newSortOrder = deletedSortOrder + i;
            chapter.setSortOrder(newSortOrder);
            // 更新到数据库
            chapterMapper.updateById(chapter);
            log.debug("章节排序调整：chapterKey={}，原排序={} → 新排序={}（连续赋值）",
                    chapter.getChapterKey(), oldSortOrder, newSortOrder);
        }

        log.info("排序调整完成：共 {} 个后续章节赋值连续排序值", chaptersToAdjust.size());
    }



}
