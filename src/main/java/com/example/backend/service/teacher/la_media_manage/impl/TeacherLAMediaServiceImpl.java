package com.example.backend.service.teacher.la_media_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.controller.teacher.dto.TeacherLAMediaAddDto;
import com.example.backend.controller.teacher.dto.TeacherLAMediaQueryListDto;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.Plugin;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaChapterResourcesService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaKnowledgeResourcesService;
import com.example.backend.service.teacher.la_media_manage.TeacherLAMediaTagResourceService;
import com.example.backend.service.teacher.resource.ResourceAuditNotifier;
import com.example.backend.tool.DirectoryTool;
import com.example.backend.tool.media.MultipartFileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherLAMediaServiceImpl implements TeacherLAMediaService {
    
    private static final Logger log = LoggerFactory.getLogger(TeacherLAMediaServiceImpl.class);

    @Value("${upload.resource.path}")
    private String baseResourcePath;

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private TeacherLAMediaChapterResourcesService chapterResourcesService;
    
    @Autowired
    private TeacherLAMediaKnowledgeResourcesService knowledgeResourcesService;
    
    @Autowired
    private TeacherLAMediaTagResourceService resourceTagService;
    
    @Autowired
    private ResourceAuditNotifier resourceAuditNotifier;

    @Override
    public List<MediaAssets> getLAMediaList(TeacherLAMediaQueryListDto req) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        
        if (req.getMediaKey() != null && !req.getMediaKey().isEmpty()) {
            queryWrapper.like("media_key", req.getMediaKey());
        }
        
        if (req.getFileName() != null && !req.getFileName().isEmpty()) {
            queryWrapper.like("file_name", req.getFileName());
        }
        
        // 过滤条件：只显示状态为ENABLED或创建者为当前用户的数据
        if (req.getUserKey() != null && !req.getUserKey().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .eq("status", "ENABLED")
                .or()
                .eq("uploaded_by", req.getUserKey())
            );
        } else {
            // 如果没有提供userKey，只显示ENABLED状态的数据
            queryWrapper.eq("status", "ENABLED");
        }
        
        // 默认按ID降序排列
        queryWrapper.orderByDesc("id");
        
        Page<MediaAssets> page = new Page<>(req.getPageIndex() != null ? req.getPageIndex() : 1,
                req.getPageSize() != null ? req.getPageSize() : 100);
        
        return mediaAssetsMapper.selectPage(page, queryWrapper).getRecords();
    }

    @Override
    public Long getLAMediasCount(TeacherLAMediaQueryListDto req) {
        QueryWrapper<MediaAssets> queryWrapper = new QueryWrapper<>();
        
        if (req.getMediaKey() != null && !req.getMediaKey().isEmpty()) {
            queryWrapper.like("media_key", req.getMediaKey());
        }
        
        if (req.getFileName() != null && !req.getFileName().isEmpty()) {
            queryWrapper.like("file_name", req.getFileName());
        }
        
        // 过滤条件：只显示状态为ENABLED或创建者为当前用户的数据
        if (req.getUserKey() != null && !req.getUserKey().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .eq("status", "ENABLED")
                .or()
                .eq("uploaded_by", req.getUserKey())
            );
        } else {
            // 如果没有提供userKey，只显示ENABLED状态的数据
            queryWrapper.eq("status", "ENABLED");
        }
        
        return mediaAssetsMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean addLAMedia(TeacherLAMediaAddDto req) {
        try {
            log.info("开始添加媒体资源，mediaKey: {}, fileName: {}, videoUrl: {}", req.getMediaKey(), req.getFileName(), req.getVideoUrl());
            
            // 1. 获取启用的插件
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin == null) {
                throw new RuntimeException("没有启用的插件，无法添加媒体资源");
            }
            String pluginKey = enabledPlugin.getPluginKey();
            
            String relativeUrl;
            int duration = 0;
            
            // 2. 判断是使用视频URL还是上传文件
            if (req.getVideoUrl() != null && !req.getVideoUrl().trim().isEmpty()) {
                // 使用提供的视频URL路径
                log.info("使用提供的视频URL: {}", req.getVideoUrl());
                relativeUrl = req.getVideoUrl().trim();
                // 如果URL是相对路径，确保格式正确
                if (!relativeUrl.startsWith("http://") && !relativeUrl.startsWith("https://") && !relativeUrl.startsWith("/")) {
                    // 相对路径，确保以media/开头或直接使用
                    if (!relativeUrl.startsWith("media/")) {
                        relativeUrl = "media/" + relativeUrl;
                    }
                }
                // 注意：使用URL时无法获取视频时长，duration保持为0
                log.info("使用视频URL路径，不获取视频时长");
            } else if (req.getVideoFile() != null && !req.getVideoFile().isEmpty()) {
                // 上传文件模式
                log.info("使用文件上传模式");
                
                // 2.1 规范化基础路径
                Path baseResourcePathNormalized = Paths.get(baseResourcePath).toAbsolutePath().normalize();
                
                // 2.2 查找或创建media目录（在基础路径下直接查找，避免路径重复）
                DirectoryTool media = new DirectoryTool("media");
                String mediaPackagePath = media.findOrCreateTargetDirectory(baseResourcePathNormalized.toString());
                
                // 2.3 校验视频文件
                MultipartFile videoFile = req.getVideoFile();
                String originalFileName = StringUtils.cleanPath(videoFile.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(originalFileName).toLowerCase();
                
                // 2.4 使用baseResourcePath作为基础路径进行文件校验
                ArrayList<String> allowedExtensions = new ArrayList<>();
                allowedExtensions.add("mp4");
                MultipartFileTool multipartFileTool = new MultipartFileTool(baseResourcePathNormalized.toString(), allowedExtensions, 2L * 1024 * 1024 * 1024, "video/mp4");
                multipartFileTool.validateFile(videoFile);
                String uniqueFileName = multipartFileTool.generateUniqueFileName(fileExtension);
                
                // 2.5 构建目标文件路径
                Path targetPath = Paths.get(mediaPackagePath, uniqueFileName);
                
                // 2.6 保存文件
                multipartFileTool.saveFile(videoFile, targetPath);
                
                // 2.7 计算相对URL
                relativeUrl = "media/" + uniqueFileName;
                
                // 2.8 获取视频时长
                duration = MultipartFileTool.getVideoDuration(targetPath.toFile());
                log.info("视频时长: {} 秒", duration);
            } else {
                throw new RuntimeException("必须提供视频文件(videoFile)或视频路径(videoUrl)");
            }
            
            // 3. 保存到数据库
            MediaAssets mediaAssets = new MediaAssets();
            mediaAssets.setMediaKey(req.getMediaKey());
            mediaAssets.setPluginKey(pluginKey);
            mediaAssets.setFormKey("video");
            mediaAssets.setFileName(req.getFileName());
            mediaAssets.setUrl(relativeUrl);
            mediaAssets.setDuration(duration);
            mediaAssets.setUploadedBy(req.getUploadedBy());
            mediaAssets.setStatus("PENDING");
            mediaAssets.setCreatedAt(LocalDateTime.now());
            
            int result = mediaAssetsMapper.insert(mediaAssets);
            
            if (result > 0) {
                log.info("媒体资源添加成功，id: {}, mediaKey: {}, url: {}", mediaAssets.getId(), req.getMediaKey(), relativeUrl);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean addLAMediaWithAllRelations(TeacherLAMediaAddDto req) {
        try {
            // 1. 添加媒体资源到 media_assets 表
            boolean mediaResult = addLAMedia(req);
            if (!mediaResult) {
                throw new RuntimeException("添加媒体资源失败");
            }

            // 2. 添加章节关联到 chapter_resources 表
            boolean chapterResult = chapterResourcesService.addChapterResources(req);
            if (!chapterResult) {
                throw new RuntimeException("添加章节关联失败");
            }

            // 3. 添加知识点关联到 knowledge_resources 表
            boolean knowledgeResult = knowledgeResourcesService.addKnowledgeResources(req);
            if (!knowledgeResult) {
                throw new RuntimeException("添加知识点关联失败");
            }

            // 4. 添加标签关联到 resource_tag 表
            boolean tagResult = resourceTagService.addResourceTag(req);
            if (!tagResult) {
                throw new RuntimeException("添加标签关联失败");
            }
            
            resourceAuditNotifier.notifyPendingAudit(
                    req.getUploadedBy(),
                    "学习活动-媒体",
                    req.getFileName()
            );
            return true;
        } catch (Exception e) {
            throw e; // 重新抛出异常以触发事务回滚
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLAMediaById(Integer id) {
        try {
            boolean mainMediaDeleted = false;
            boolean chapterResourcesDeleted = false;
            boolean knowledgeResourcesDeleted = false;
            boolean resourceTagDeleted = false;
            StringBuilder errorMsg = new StringBuilder();

            // 删除章节资源关联
            try {
                chapterResourcesDeleted = chapterResourcesService.deleteChapterResources(id);
                if (!chapterResourcesDeleted) {
                    log.warn("章节资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("章节资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除章节资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除章节资源失败，id: {}", id, e);
            }

            // 删除知识点资源关联
            try {
                knowledgeResourcesDeleted = knowledgeResourcesService.deleteKnowledgeResources(id);
                if (!knowledgeResourcesDeleted) {
                    log.warn("知识点资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("知识点资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除知识点资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除知识点资源失败，id: {}", id, e);
            }

            // 删除标签资源关联
            try {
                resourceTagDeleted = resourceTagService.deleteResourceTag(id);
                if (!resourceTagDeleted) {
                    log.warn("标签资源关联不存在或已删除，id: {}", id);
                } else {
                    log.info("标签资源关联删除成功，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除标签资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除标签资源失败，id: {}", id, e);
            }

            // 删除媒体文件和主资源记录
            try {
                // 先根据ID查询现有的媒体资源记录
                MediaAssets mediaAssets = mediaAssetsMapper.selectById(id);
                if (mediaAssets == null) {
                    log.warn("未找到ID为 {} 的媒体资源", id);
                    // 媒体资源不存在，但其他关联数据可能已删除，所以返回true
                    return true;
                }

                // 只有当媒体资源存在且有URL时才尝试删除文件
                if (mediaAssets.getUrl() != null && !mediaAssets.getUrl().isEmpty()) {
                    try {
                        // 构建文件的完整路径并删除文件
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
                    } catch (Exception e) {
                        log.warn("处理文件删除时发生异常: {}", e.getMessage());
                    }
                }

                // 从数据库中删除记录
                int result = mediaAssetsMapper.deleteById(id);
                mainMediaDeleted = result > 0;
                if (mainMediaDeleted) {
                    log.info("数据库记录删除成功，id: {}", id);
                } else {
                    log.warn("数据库记录删除失败，id: {}", id);
                }
            } catch (Exception e) {
                errorMsg.append("删除主媒体资源失败: ").append(e.getMessage()).append("; ");
                log.error("删除主媒体资源失败，id: {}", id, e);
            }

            // 检查是否有错误信息
            if (errorMsg.length() > 0) {
                throw new RuntimeException(errorMsg.toString());
            }

            // 所有操作都成功完成
            return mainMediaDeleted || chapterResourcesDeleted || knowledgeResourcesDeleted || resourceTagDeleted;
        } catch (Exception e) {
            log.error("删除媒体资源失败，id: {}, 错误: {}", id, e.getMessage(), e);
            throw new RuntimeException("删除媒体资源失败: " + e.getMessage(), e);
        }
    }
}