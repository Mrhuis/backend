package com.example.backend.service.admin.la_media_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.controller.admin.dto.AdminLAMediaAddDto;
import com.example.backend.controller.admin.dto.AdminLAMediaQueryListDto;
import com.example.backend.controller.admin.dto.AdminLAMediaUpdateDto;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.mapper.TagResourceMapper;
import com.example.backend.service.admin.la_media_manage.AdminLAMediaService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.tool.DirectoryTool;
import com.example.backend.tool.media.MultipartFileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.service.admin.la_media_manage.AdminLAMediaChapterResourcesService;
import com.example.backend.service.admin.la_media_manage.AdminLAMediaKnowledgeResourcesService;
import com.example.backend.service.admin.la_media_manage.AdminLAMediaTagResourceService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: AdminLAMediaServiceImpl
 * Package: com.example.backend.service.admin.la_media_manage.impl
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 11:42
 * @Version 1.0
 */
@Service
public class AdminLAMediaServiceImpl implements AdminLAMediaService {

    private static final Logger log = LoggerFactory.getLogger(AdminLAMediaServiceImpl.class);
    @Value("${upload.resource.path}")
    private String baseResourcePath;
    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private AdminLAMediaChapterResourcesService chapterResourcesService;

    @Autowired
    private AdminLAMediaKnowledgeResourcesService knowledgeResourcesService;

    @Autowired
    private AdminLAMediaTagResourceService resourceTagService;

    // 添加Mapper注入
    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private TagResourceMapper tagResourceMapper;

    @Override
    public List<MediaAssets> getLAMediaList(AdminLAMediaQueryListDto req) {
        //分页查询，获取对应查找条件数据，req中并不是每个字段都有值，所以要判断
        //先调用PluginService.getEnabledPlugin(), 获取插件对象，获取插件对象中的plugin_key
        //当前方法返回的knowledge数据的plugin_key必须是插件对象中的plugin_key

        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取视频列表 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回空列表");
                return new ArrayList<>(); // 如果没有启用的插件，返回空列表
            }

            log.info("使用插件过滤条件: plugin_key = {}", enabledPlugin.getPluginKey());

            // 检查是否需要根据关联表进行筛选
            boolean hasChapterFilter = req != null && req.getChapter_key() != null && !req.getChapter_key().isEmpty();
            boolean hasKnowledgeFilter = req != null && req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty();
            boolean hasTagFilter = req != null && req.getTagId() != null && !req.getTagId().isEmpty();

            List<MediaAssets> result;

            if (hasChapterFilter || hasKnowledgeFilter || hasTagFilter) {
                // 需要根据关联表进行筛选
                result = getLAMediaListWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                result = getLAMediaListWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取媒体信息列表失败", e);
            throw new RuntimeException("获取媒体信息列表失败", e);
        }
    }

    /**
     * 不带关联表筛选的查询
     */
    private List<MediaAssets> getLAMediaListWithoutAssociationFilter(AdminLAMediaQueryListDto req, String pluginKey) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();

        // 必须限制为当前启用的插件
        queryWrapper.eq("plugin_key", pluginKey);

        // 添加查询条件
        if (req != null) {
            // 媒体标识模糊查询
            if (StringUtils.hasText(req.getMediaKey())) {
                queryWrapper.like("media_key", req.getMediaKey());
            }

            // 媒体名称模糊查询
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }

            // 状态精确查询
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }

            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("media_key", searchValue)
                        .or()
                        .like("file_name", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                        .or()
                        .like("asset_type", searchValue)
                );
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");

        // 分页查询
        if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
            queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
        }

        log.info("执行查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectList(queryWrapper);
    }

    /**
     * 带关联表筛选的查询
     */
    private List<MediaAssets> getLAMediaListWithAssociationFilter(AdminLAMediaQueryListDto req, String pluginKey) {
        // 先获取所有符合条件的media_key
        List<String> filteredMediaKeys = getFilteredMediaKeys(req);
        
        if (filteredMediaKeys.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据筛选后的media_key查询MediaAssets
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("media_key", filteredMediaKeys);

        // 添加其他查询条件
        if (req != null) {
            if (StringUtils.hasText(req.getMediaKey())) {
                queryWrapper.like("media_key", req.getMediaKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("media_key", searchValue)
                        .or()
                        .like("file_name", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                        .or()
                        .like("asset_type", searchValue)
                );
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");

        // 分页查询
        if (req != null && req.getPageSize() != null && req.getPageSize() > 0) {
            queryWrapper.last("LIMIT " + req.getOffset() + ", " + req.getPageSize());
        }

        log.info("执行关联表筛选查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectList(queryWrapper);
    }

    /**
     * 根据关联表筛选条件获取符合条件的media_key列表
     */
    private List<String> getFilteredMediaKeys(AdminLAMediaQueryListDto req) {
        List<String> chapterMediaKeys = new ArrayList<>();
        List<String> knowledgeMediaKeys = new ArrayList<>();
        List<String> tagMediaKeys = new ArrayList<>();

        // 根据章节筛选
        if (req.getChapter_key() != null && !req.getChapter_key().isEmpty()) {
            for (String chapterKey : req.getChapter_key()) {
                List<String> mediaKeys = chapterResourcesMapper.selectResourceKeysByChapterKey(chapterKey, "media");
                chapterMediaKeys.addAll(mediaKeys);
            }
        }

        // 根据知识点筛选
        if (req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty()) {
            for (String knowledgeKey : req.getKnowledge_key()) {
                List<String> mediaKeys = knowledgeResourcesMapper.selectResourceKeysByKnowledgeKey(knowledgeKey, "media");
                knowledgeMediaKeys.addAll(mediaKeys);
            }
        }

        // 根据标签筛选
        if (req.getTagId() != null && !req.getTagId().isEmpty()) {
            for (Long tagId : req.getTagId()) {
                List<String> mediaKeys = tagResourceMapper.selectResourceKeysByTagId(tagId, "media");
                tagMediaKeys.addAll(mediaKeys);
            }
        }

        // 计算交集
        List<String> result = new ArrayList<>();
        
        if (!chapterMediaKeys.isEmpty() && !knowledgeMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 三个条件都有，取交集
            result = chapterMediaKeys.stream()
                    .filter(knowledgeMediaKeys::contains)
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty() && !knowledgeMediaKeys.isEmpty()) {
            // 章节和知识点
            result = chapterMediaKeys.stream()
                    .filter(knowledgeMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 章节和标签
            result = chapterMediaKeys.stream()
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!knowledgeMediaKeys.isEmpty() && !tagMediaKeys.isEmpty()) {
            // 知识点和标签
            result = knowledgeMediaKeys.stream()
                    .filter(tagMediaKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (!chapterMediaKeys.isEmpty()) {
            // 只有章节
            result = chapterMediaKeys.stream().distinct().collect(Collectors.toList());
        } else if (!knowledgeMediaKeys.isEmpty()) {
            // 只有知识点
            result = knowledgeMediaKeys.stream().distinct().collect(Collectors.toList());
        } else if (!tagMediaKeys.isEmpty()) {
            // 只有标签
            result = tagMediaKeys.stream().distinct().collect(Collectors.toList());
        }

        log.info("关联表筛选结果 - 章节筛选: {}, 知识点筛选: {}, 标签筛选: {}, 最终结果: {}", 
                chapterMediaKeys.size(), knowledgeMediaKeys.size(), tagMediaKeys.size(), result.size());
        
        return result;
    }

    @Override
    public Long getLAMediasCount(AdminLAMediaQueryListDto req) {
        try {
            // 获取当前启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            log.info("获取视频总数 - 当前启用的插件: {}", enabledPlugin);

            if (enabledPlugin == null) {
                log.warn("没有启用的插件，返回0");
                return 0L;
            }

            // 检查是否需要根据关联表进行筛选
            boolean hasChapterFilter = req != null && req.getChapter_key() != null && !req.getChapter_key().isEmpty();
            boolean hasKnowledgeFilter = req != null && req.getKnowledge_key() != null && !req.getKnowledge_key().isEmpty();
            boolean hasTagFilter = req != null && req.getTagId() != null && !req.getTagId().isEmpty();

            Long count;

            if (hasChapterFilter || hasKnowledgeFilter || hasTagFilter) {
                // 需要根据关联表进行筛选
                count = getLAMediasCountWithAssociationFilter(req, enabledPlugin.getPluginKey());
            } else {
                // 使用原有的查询逻辑
                count = getLAMediasCountWithoutAssociationFilter(req, enabledPlugin.getPluginKey());
            }

            log.info("查询结果总数: {}", count);
            return count;
        } catch (Exception e) {
            log.error("获取媒体信息总数失败", e);
            throw new RuntimeException("获取媒体信息总数失败", e);
        }
    }

    /**
     * 不带关联表筛选的计数查询
     */
    private Long getLAMediasCountWithoutAssociationFilter(AdminLAMediaQueryListDto req, String pluginKey) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);

        if (req != null) {
            if (StringUtils.hasText(req.getMediaKey())) {
                queryWrapper.like("media_key", req.getMediaKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("media_key", searchValue)
                        .or()
                        .like("file_name", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                        .or()
                        .like("asset_type", searchValue)
                );
            }
        }

        log.info("执行计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectCount(queryWrapper);
    }

    /**
     * 带关联表筛选的计数查询
     */
    private Long getLAMediasCountWithAssociationFilter(AdminLAMediaQueryListDto req, String pluginKey) {
        // 先获取所有符合条件的media_key
        List<String> filteredMediaKeys = getFilteredMediaKeys(req);
        
        if (filteredMediaKeys.isEmpty()) {
            return 0L;
        }

        // 根据筛选后的media_key查询MediaAssets数量
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        queryWrapper.in("media_key", filteredMediaKeys);

        if (req != null) {
            if (StringUtils.hasText(req.getMediaKey())) {
                queryWrapper.like("media_key", req.getMediaKey());
            }
            if (StringUtils.hasText(req.getFileName())) {
                queryWrapper.like("file_name", req.getFileName());
            }
            if (StringUtils.hasText(req.getStatus())) {
                queryWrapper.eq("status", req.getStatus());
            }
            if (StringUtils.hasText(req.getSearchValue())) {
                String searchValue = req.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                        .like("media_key", searchValue)
                        .or()
                        .like("file_name", searchValue)
                        .or()
                        .like("uploaded_by", searchValue)
                        .or()
                        .like("asset_type", searchValue)
                );
            }
        }

        log.info("执行关联表筛选计数查询，SQL条件: {}", queryWrapper.getTargetSql());
        return mediaAssetsMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addLAMedia(AdminLAMediaAddDto req) {
        try {
            log.info("开始添加媒体资源，mediaKey: {}, fileName: {}", req.getMediaKey(), req.getFileName());
            
            // 1. 获取启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加媒体资源");
            }
            String pluginKey = enabledPlugin.getPluginKey();
            
            // 2. 规范化基础路径
            Path baseResourcePathNormalized = Paths.get(baseResourcePath).toAbsolutePath().normalize();
            
            // 3. 查找或创建media目录（在基础路径下直接查找，避免路径重复）
            DirectoryTool media = new DirectoryTool("media");
            String mediaPackagePath = media.findOrCreateTargetDirectory(baseResourcePathNormalized.toString());
            
            // 4. 校验视频文件
            MultipartFile videoFile = req.getVideoFile();
            String originalFileName = StringUtils.cleanPath(videoFile.getOriginalFilename());
            String fileExtension = StringUtils.getFilenameExtension(originalFileName).toLowerCase();
            
            // 6. 使用baseResourcePath作为基础路径进行文件校验
            ArrayList<String> allowedExtensions = new ArrayList<>();
            allowedExtensions.add("mp4");
            MultipartFileTool multipartFileTool = new MultipartFileTool(baseResourcePathNormalized.toString(), allowedExtensions, 2L * 1024 * 1024 * 1024, "video/mp4");
            multipartFileTool.validateFile(videoFile);
            String uniqueFileName = multipartFileTool.generateUniqueFileName(fileExtension);
            
            // 7. 构建目标文件路径
            Path targetPath = Paths.get(mediaPackagePath, uniqueFileName);
            
            // 8. 保存文件
            multipartFileTool.saveFile(videoFile, targetPath);
            
            // 9. 计算相对URL
            // 修复相对URL计算逻辑，确保正确生成URL
            String relativeUrl = "media/" + uniqueFileName;
            
            // 10. 获取视频时长
            int duration = multipartFileTool.getVideoDuration(targetPath.toFile());
            log.info("视频时长: {} 秒", duration);
            
            // 11. 保存到数据库
            MediaAssets mediaAssets = new MediaAssets();
            mediaAssets.setMediaKey(req.getMediaKey());
            mediaAssets.setPluginKey(pluginKey);
            mediaAssets.setFormKey("video");
            mediaAssets.setFileName(req.getFileName());
            mediaAssets.setUrl(relativeUrl);
            mediaAssets.setDuration(duration);
            mediaAssets.setUploadedBy(req.getUploadedBy());
            mediaAssets.setStatus("DISABLED");
            mediaAssets.setCreatedAt(LocalDateTime.now());
            
            int result = mediaAssetsMapper.insert(mediaAssets);
            
            if (result > 0) {
                log.info("媒体资源添加成功，id: {}, mediaKey: {}", mediaAssets.getId(), req.getMediaKey());
            } else {
                log.error("媒体资源添加失败，mediaKey: {}", req.getMediaKey());
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("添加媒体资源失败，mediaKey: {}, 错误: {}", req.getMediaKey(), e.getMessage(), e);
            throw new RuntimeException("添加媒体资源失败: " + e.getMessage(), e);
        }
    }



    @Override
    public boolean updateLAMedia(AdminLAMediaUpdateDto req) {
        //更新时，先根据id查询出对应的mediaAssets对象，再根据req更新mediaAssets对象，req中有非空/0的字段，进行对应字段的更新。
        //只有当req.getVideoFile()不为空时，才处理视频文件替换
        try {
            // 1. 根据ID查询现有的媒体资源记录
            MediaAssets existingMediaAssets = mediaAssetsMapper.selectById(req.getId());
            if (existingMediaAssets == null) {
                throw new RuntimeException("未找到ID为 " + req.getId() + " 的媒体资源");
            }

            // 2. 获取启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法更新媒体资源");
            }
            String pluginKey = enabledPlugin.getPluginKey();

            // 3. 只有当有新的视频文件上传时，才处理文件替换
            if (req.getVideoFile() != null && !req.getVideoFile().isEmpty()) {
                log.info("检测到新的视频文件，开始处理文件替换，id: {}", req.getId());
                
                // 3.1 规范化基础路径
                Path baseResourcePathNormalized = Paths.get(baseResourcePath).toAbsolutePath().normalize();

                MultipartFile newVideoFile = req.getVideoFile();
                String originalFileName = StringUtils.cleanPath(newVideoFile.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(originalFileName).toLowerCase();

                // 3.2 查找或创建media目录（在基础路径下直接查找，避免路径重复）
                DirectoryTool media = new DirectoryTool("media");
                String mediaPackagePath = media.findOrCreateTargetDirectory(baseResourcePathNormalized.toString());
                
                // 3.3 校验新视频文件，使用找到的media目录的父目录作为基础路径
                String mediaBasePath = Paths.get(mediaPackagePath).getParent().toString();
                ArrayList<String> allowedExtensions = new ArrayList<>();
                allowedExtensions.add("mp4");
                MultipartFileTool multipartFileTool = new MultipartFileTool(mediaBasePath, allowedExtensions, 2L * 1024 * 1024 * 1024, "video/mp4");
                multipartFileTool.validateFile(newVideoFile);
                String uniqueFileName = multipartFileTool.generateUniqueFileName(fileExtension);

                // 3.4 构建目标文件路径
                Path targetPath = Paths.get(mediaPackagePath, uniqueFileName);

                // 3.5 保存新文件
                multipartFileTool.saveFile(newVideoFile, targetPath);

                // 3.6 计算相对URL
                // 修复相对URL计算逻辑，确保正确生成URL
                String relativeUrl = "media/" + uniqueFileName;

                // 3.7 获取视频时长
                int duration = multipartFileTool.getVideoDuration(targetPath.toFile());

                // 3.8 更新媒体资源记录中的文件相关信息
                if (req.getFileName() != null && !req.getFileName().isEmpty()) {
                    existingMediaAssets.setFileName(req.getFileName());
                }
                existingMediaAssets.setUrl(relativeUrl);
                existingMediaAssets.setDuration(duration);
            }

            // 4. 更新其他非空字段
            if (req.getMediaKey() != null && !req.getMediaKey().isEmpty()) {
                existingMediaAssets.setMediaKey(req.getMediaKey());
            }
            
            if (req.getStatus() != null && !req.getStatus().isEmpty()) {
                existingMediaAssets.setStatus(req.getStatus());
            }

            // 5. 更新数据库记录
            int result = mediaAssetsMapper.updateById(existingMediaAssets);
            log.info("媒体资源更新完成，id: {}, 结果: {}", req.getId(), result > 0 ? "成功" : "失败");
            return result > 0;
        } catch (Exception e) {
            log.error("更新媒体资源失败，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            throw new RuntimeException("更新媒体资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteLAMediaById(Integer id) {
        //删除时，先根据id查询出对应的mediaAssets对象，再根据mediaAssets对象中的url字段，删除掉对应路径下的对应的视频文件。
        //最后再删除数据库中的mediaAssets对象。
        try {
            // 1. 根据ID查询现有的媒体资源记录
            MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
            if (mediaAssets == null) {
                log.warn("未找到ID为 {} 的媒体资源", id);
                return false; // 媒体资源不存在，返回false
            }

            // 2. 构建文件的完整路径并删除文件
            // 使用 Paths.get() 正确构建路径
            Path basePath = Paths.get(baseResourcePath).toAbsolutePath().normalize();
            // 使用 DirectoryTool 查找 media 目录
            DirectoryTool mediaDirTool = new DirectoryTool("media");
            String mediaDirPath = mediaDirTool.findOrCreateTargetDirectory(basePath.toString());
            
            // 构建完整文件路径
            Path mediaDir = Paths.get(mediaDirPath);
            Path filePath = mediaDir.resolve(Paths.get(mediaAssets.getUrl()).getFileName().toString());
            
            log.info("尝试删除文件，构建的路径: {}", filePath);
            
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                    log.info("文件删除成功: {}", filePath);
                } catch (Exception e) {
                    log.warn("文件删除失败: {}，错误: {}", filePath, e.getMessage());
                }
            } else {
                log.warn("文件不存在: {}，无需删除", filePath);
            }

            // 3. 从数据库中删除记录
            int result = mediaAssetsMapper.deleteById(id);
            if (result > 0) {
                log.info("数据库记录删除成功，id: {}", id);
            } else {
                log.warn("数据库记录删除失败，id: {}", id);
            }
            return result > 0;
        } catch (Exception e) {
            log.error("删除媒体资源失败，id: {}, 错误: {}", id, e.getMessage(), e);
            return false; // 发生异常，返回false
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addLAMediaWithAllRelations(AdminLAMediaAddDto req) {
        try {
            log.info("开始事务性添加视频资源，mediaKey: {}", req.getMediaKey());
            
            // 1. 添加媒体资源到 media_assets 表
            boolean mediaResult = addLAMedia(req);
            if (!mediaResult) {
                log.error("添加媒体资源失败，mediaKey: {}", req.getMediaKey());
                throw new RuntimeException("添加媒体资源失败");
            }
            log.info("媒体资源添加成功，mediaKey: {}", req.getMediaKey());
            
            // 2. 添加章节关联到 chapter_resources 表
            boolean chapterResult = chapterResourcesService.addChapterResources(req);
            if (!chapterResult) {
                log.error("添加章节关联失败，mediaKey: {}", req.getMediaKey());
                throw new RuntimeException("添加章节关联失败");
            }
            log.info("章节关联添加成功，mediaKey: {}", req.getMediaKey());
            
            // 3. 添加知识点关联到 knowledge_resources 表
            boolean knowledgeResult = knowledgeResourcesService.addKnowledgeResources(req);
            if (!knowledgeResult) {
                log.error("添加知识点关联失败，mediaKey: {}", req.getMediaKey());
                throw new RuntimeException("添加知识点关联失败");
            }
            log.info("知识点关联添加成功，mediaKey: {}", req.getMediaKey());
            
            // 4. 添加标签关联到 resource_tag 表
            boolean tagResult = resourceTagService.addResourceTag(req);
            if (!tagResult) {
                log.error("添加标签关联失败，mediaKey: {}", req.getMediaKey());
                throw new RuntimeException("添加标签关联失败");
            }
            log.info("标签关联添加成功，mediaKey: {}", req.getMediaKey());
            
            log.info("所有操作成功完成，mediaKey: {}", req.getMediaKey());
            return true;
            
        } catch (Exception e) {
            log.error("事务性添加视频资源失败，mediaKey: {}, 错误: {}", req.getMediaKey(), e.getMessage(), e);
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLAMediaWithAllRelations(AdminLAMediaUpdateDto req) {
        try {
            log.info("开始事务性更新视频资源，id: {}", req.getId());
            
            // 1. 更新媒体资源
            boolean mediaResult = updateLAMedia(req);
            if (!mediaResult) {
                log.error("更新媒体资源失败，id: {}", req.getId());
                throw new RuntimeException("更新媒体资源失败");
            }
            log.info("媒体资源更新成功，id: {}", req.getId());
            
            // 2. 更新章节关联
            boolean chapterResult = chapterResourcesService.updateChapterResources(req);
            if (!chapterResult) {
                log.error("更新章节关联失败，id: {}", req.getId());
                throw new RuntimeException("更新章节关联失败");
            }
            log.info("章节关联更新成功，id: {}", req.getId());
            
            // 3. 更新知识点关联
            boolean knowledgeResult = knowledgeResourcesService.updateKnowledgeResources(req);
            if (!knowledgeResult) {
                log.error("更新知识点关联失败，id: {}", req.getId());
                throw new RuntimeException("更新知识点关联失败");
            }
            log.info("知识点关联更新成功，id: {}", req.getId());
            
            // 4. 更新标签关联
            boolean tagResult = resourceTagService.updateResourceTag(req);
            if (!tagResult) {
                log.error("更新标签关联失败，id: {}", req.getId());
                throw new RuntimeException("更新标签关联失败");
            }
            log.info("标签关联更新成功，id: {}", req.getId());
            
            log.info("所有更新操作成功完成，id: {}", req.getId());
            return true;
            
        } catch (Exception e) {
            log.error("事务性更新视频资源失败，id: {}, 错误: {}", req.getId(), e.getMessage(), e);
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    @Override
    public boolean updateLAMediaStatus(Long id, String status) {
        try {
            MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
            mediaAssets.setStatus(status);
            mediaAssetsMapper.updateById(mediaAssets);
            return true;

        } catch (Exception e) {
            log.error("更新媒体资源状态失败，id: {}, 错误: {}", id, e.getMessage(), e);
        }
        return false;
    }
}
