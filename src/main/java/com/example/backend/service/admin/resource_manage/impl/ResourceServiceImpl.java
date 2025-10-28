package com.example.backend.service.admin.resource_manage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.entity.Plugin;
import com.example.backend.entity.MediaAssets;
import com.example.backend.entity.Item;
import com.example.backend.entity.Knowledge;
import com.example.backend.entity.TagResource;
import com.example.backend.entity.KnowledgeResources;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.ResourceForm;
import com.example.backend.entity.ChapterResources;
import com.example.backend.entity.UserPreference;
import com.example.backend.entity.Tag;
import com.example.backend.mapper.PluginMapper;
import com.example.backend.mapper.KnowledgesMapper;
import com.example.backend.mapper.ItemsMapper;
import com.example.backend.mapper.MediaAssetsMapper;
import com.example.backend.mapper.TagResourceMapper;
import com.example.backend.mapper.KnowledgeResourcesMapper;
import com.example.backend.mapper.ChapterMapper;
import com.example.backend.mapper.ResourceFormMapper;
import com.example.backend.mapper.ChapterResourcesMapper;
import com.example.backend.mapper.UserPreferenceMapper;
import com.example.backend.mapper.TagsMapper;
import com.example.backend.service.admin.resource_manage.ResourceService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.admin.resource_manage.DataExportService;
import com.example.backend.service.admin.resource_manage.ZipService;
import com.example.backend.controller.admin.dto.FileChunk;
import com.example.backend.controller.admin.dto.UploadProgress;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import com.example.backend.controller.admin.dto.AdminPluginsQueryListDto;

@Service
public class ResourceServiceImpl implements ResourceService, PluginService {

    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final Path resourceStorageLocation;

    @Autowired
    private PluginMapper pluginMapper;

    @Autowired
    private KnowledgesMapper knowledgesMapper;

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private MediaAssetsMapper mediaAssetsMapper;

    @Autowired
    private TagResourceMapper tagResourceMapper;

    @Autowired
    private KnowledgeResourcesMapper knowledgeResourcesMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private ResourceFormMapper resourceFormMapper;

    @Autowired
    private ChapterResourcesMapper chapterResourcesMapper;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataExportService dataExportService;

    @Autowired
    private ZipService zipService;

    // 分块上传相关字段
    private final ConcurrentHashMap<String, UploadProgress> uploadProgressMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Path> chunkStorageMap = new ConcurrentHashMap<>();
    
    // 分块文件时间戳存储：fileId -> 创建时间
    private final ConcurrentHashMap<String, LocalDateTime> chunkTimestamps = new ConcurrentHashMap<>();
    
    // 分块文件过期时间（分钟）
    private static final long CHUNK_EXPIRE_MINUTES = 60;

    public ResourceServiceImpl(@Value("${upload.resource.path}") String uploadPath) {
        this.resourceStorageLocation = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.resourceStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * 存储上传的资源文件，包括解压、验证和保存插件信息
     * 
     * @param file 上传的文件
     * @param uploaderId 上传者ID
     * @throws IOException 文件处理异常
     */
    @Override
    public void storeResource(MultipartFile file, String uploaderId) throws IOException {
        log.info("开始存储资源，上传者ID: {}, 文件大小: {} bytes", uploaderId, file.getSize());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IOException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        Path tempZip = null;
        Path tempUnzipDir = null;
        
        try {
            // 1. 使用流式处理保存临时zip文件，避免内存溢出
            tempZip = Files.createTempFile("plugin_upload_", ".zip");
            log.info("创建临时文件: {}", tempZip);
            
            // 使用缓冲流提高性能
            try (var inputStream = file.getInputStream();
                 var outputStream = new FileOutputStream(tempZip.toFile())) {
                byte[] buffer = new byte[8192]; // 8KB缓冲区
                int bytesRead;
                long totalBytesRead = 0;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    
                    // 每处理1MB记录一次日志
                    if (totalBytesRead % (1024 * 1024) == 0) {
                        log.info("已处理 {} MB", totalBytesRead / (1024 * 1024));
                    }
                }
                outputStream.flush();
            }
            
            log.info("文件保存完成，大小: {} bytes", Files.size(tempZip));
            
            // 2. 创建临时解压目录
            tempUnzipDir = Files.createTempDirectory("plugin_unzip_");
            log.info("创建临时解压目录: {}", tempUnzipDir);
            
            // 3. 解压文件
        unzipTo(tempZip, tempUnzipDir);
            log.info("文件解压完成");
            
            // 4. 读取plugin.json或plugins.json
        Path pluginJsonPath = Files.walk(tempUnzipDir)
            .filter(p -> {
                String jsonFileName = p.getFileName().toString();
                return jsonFileName.equals("plugin.json") || jsonFileName.equals("plugins.json");
            })
            .findFirst().orElse(null);
        if (pluginJsonPath == null) {
            throw new IOException("plugin.json or plugins.json not found in zip");
        }
            
        log.info("找到插件配置文件: {}", pluginJsonPath);
            
        // 使用Spring注入的ObjectMapper，它已经配置了自定义的日期时间反序列化器
        
        // 读取JSON文件内容
        String jsonContent = Files.readString(pluginJsonPath);
        log.info("读取到JSON内容: {}", jsonContent);
        
        // 解析JSON结构
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        Plugin plugin = null;
        
        // 检查是否是plugins.json格式（包含plugins数组）
        if (rootNode.has("plugins") && rootNode.get("plugins").isArray()) {
            JsonNode pluginsArray = rootNode.get("plugins");
            if (pluginsArray.size() > 0) {
                // 取第一个插件
                JsonNode firstPlugin = pluginsArray.get(0);
                plugin = objectMapper.treeToValue(firstPlugin, Plugin.class);
                log.info("从plugins.json数组中提取插件信息");
            } else {
                throw new IOException("plugins.json中的plugins数组为空");
            }
        } else {
            // 尝试直接解析为单个Plugin对象（兼容plugin.json格式）
            plugin = objectMapper.readValue(pluginJsonPath.toFile(), Plugin.class);
            log.info("直接解析为Plugin对象");
        }
        
        // 验证插件信息的完整性
        if (plugin == null) {
            throw new IOException("Failed to parse plugin configuration file");
        }
        
        String pluginKey = plugin.getPluginKey();
        log.info("读取到插件信息，pluginKey: {}", pluginKey);
        
        // 验证pluginKey不能为空
        if (pluginKey == null || pluginKey.trim().isEmpty()) {
            log.error("插件配置文件中pluginKey为空或无效");
            log.error("插件对象内容: {}", objectMapper.writeValueAsString(plugin));
            throw new IOException("Plugin key cannot be null or empty in configuration file");
        }
        
        // 5. 创建插件目录
        Path pluginDir = this.resourceStorageLocation.resolve(pluginKey);
        Files.createDirectories(pluginDir);
            log.info("创建插件目录: {}", pluginDir);
            
            // 6. 移动zip文件到最终位置
        Path pluginZipPath = pluginDir.resolve(pluginKey + ".zip");
        Files.move(tempZip, pluginZipPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("移动zip文件到: {}", pluginZipPath);
            
            // 7. 解压内容到最终位置
        Path destUnzipDir = pluginDir.resolve(pluginKey);
        if (Files.exists(destUnzipDir)) {
            deleteDirectory(destUnzipDir.toFile());
                log.info("清理旧内容: {}", destUnzipDir);
        }
        Files.createDirectories(destUnzipDir);
        copyDirectory(tempUnzipDir, destUnzipDir);
            log.info("复制解压内容到: {}", destUnzipDir);
            
            // 8. 保存插件信息到数据库
        plugin.setStoragePath(pluginKey);
        plugin.setUploadedBy(uploaderId);
        plugin.setStatus("uninitialized"); // 新上传的插件默认为未初始化状态，需要管理员审核
            
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        Plugin existingPlugin = pluginMapper.selectOne(queryWrapper);
            
        if (existingPlugin != null) {
            plugin.setId(existingPlugin.getId());
            pluginMapper.updateById(plugin);
                log.info("更新现有插件: {}", pluginKey);
        } else {
            pluginMapper.insert(plugin);
                log.info("插入新插件: {}", pluginKey);
            }
            
            log.info("资源存储完成: {}", pluginKey);
            
        } catch (Exception e) {
            log.error("存储资源失败", e);
            throw new IOException("Failed to store resource: " + e.getMessage(), e);
        } finally {
            // 清理临时文件
            try {
                if (tempZip != null && Files.exists(tempZip)) {
                    Files.deleteIfExists(tempZip);
                    log.info("清理临时zip文件: {}", tempZip);
                }
                if (tempUnzipDir != null && Files.exists(tempUnzipDir)) {
                    deleteDirectory(tempUnzipDir.toFile());
                    log.info("清理临时解压目录: {}", tempUnzipDir);
                }
            } catch (Exception e) {
                log.warn("清理临时文件失败", e);
            }
        }
    }

    /**
     * 删除指定插件的所有相关资源，包括文件系统中的文件和数据库记录
     * 按照外键依赖关系顺序删除，避免外键约束错误
     * 
     * @param pluginKey 插件唯一标识
     * @throws IOException 删除过程中可能发生的IO异常
     */
    @Override
    public void deletePluginResources(String pluginKey) throws IOException {
        log.info("开始删除插件资源: {}", pluginKey);
        
        try {
            // 1. 删除文件系统中的插件包
            deletePluginFiles(pluginKey);
            
            // 2. 删除数据库中的相关记录（按依赖关系顺序删除）
            // 2.1 删除用户偏好表（依赖resource_form）
            int userPreferenceDeleted = userPreferenceMapper.delete(
                new LambdaQueryWrapper<UserPreference>()
                    .inSql(UserPreference::getFormId, 
                        "SELECT form_id FROM resource_form WHERE plugin_key = '" + pluginKey + "'")
            );
            
            // 2.2 删除知识点-资源关联表（依赖knowledge、media/items）
            int knowledgeResourcesDeleted = knowledgeResourcesMapper.delete(
                new LambdaQueryWrapper<KnowledgeResources>()
                    .inSql(KnowledgeResources::getKnowledgeKey, 
                        "SELECT knowledge_key FROM knowledge WHERE plugin_key = '" + pluginKey + "'")
            );
            
            // 2.3 删除章节-资源关联表（依赖chapters）
            int chapterResourcesDeleted = chapterResourcesMapper.delete(
                new LambdaQueryWrapper<ChapterResources>()
                    .inSql(ChapterResources::getChapterKey, 
                        "SELECT chapter_key FROM chapters WHERE plugin_key = '" + pluginKey + "'")
            );
            
            // 2.4 删除资源-标签关联表（依赖tag表）
            int resourceTagDeleted = tagResourceMapper.delete(
                new LambdaQueryWrapper<TagResource>()
                    .inSql(TagResource::getTagId,
                        "SELECT id FROM tag WHERE plugin_key = '" + pluginKey + "'")
            );
            
            // 2.5 删除标签表（依赖plugin）
            int tagDeleted = tagsMapper.delete(
                new LambdaQueryWrapper<Tag>()
                    .eq(Tag::getPluginKey, pluginKey)
            );
            
            // 2.6 删除媒体资源表（依赖knowledge）
            int mediaAssetsDeleted = mediaAssetsMapper.delete(
                new LambdaQueryWrapper<MediaAssets>()
                    .eq(MediaAssets::getPluginKey, pluginKey)
            );
            
            // 2.7 删除习题表（依赖knowledge）
            int itemsDeleted = itemsMapper.delete(
                new LambdaQueryWrapper<Item>()
                    .eq(Item::getPluginKey, pluginKey)
            );
            
            // 2.8 删除章节表（可能依赖自身或无外键）
            int chaptersDeleted = chapterMapper.delete(
                new LambdaQueryWrapper<Chapter>()
                    .eq(Chapter::getPluginKey, pluginKey)
            );
            
            // 2.9 删除知识点表（无外键）
            int knowledgeDeleted = knowledgesMapper.delete(
                new LambdaQueryWrapper<Knowledge>()
                    .eq(Knowledge::getPluginKey, pluginKey)
            );
            
            // 2.10 删除资源形式字典表（无外键）
            int resourceFormDeleted = resourceFormMapper.delete(
                new LambdaQueryWrapper<ResourceForm>()
                    .eq(ResourceForm::getPluginKey, pluginKey)
            );
            
            log.info("数据库记录删除完成 - 用户偏好: {}, 知识点资源关联: {}, 章节资源关联: {}, 资源标签关联: {}, 标签: {}, 媒体资源: {}, 习题: {}, 章节: {}, 知识点: {}, 资源形式: {}", 
                userPreferenceDeleted, knowledgeResourcesDeleted, chapterResourcesDeleted, resourceTagDeleted, tagDeleted,
                mediaAssetsDeleted, itemsDeleted, chaptersDeleted, knowledgeDeleted, resourceFormDeleted);
            
            log.info("插件资源删除完成: {}", pluginKey);
        } catch (Exception e) {
            log.error("删除插件资源失败: {}", pluginKey, e);
            throw new IOException("删除插件资源失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除插件文件系统中的资源
     */
    private void deletePluginFiles(String pluginKey) throws IOException {
        // 删除插件目录：uploads/resources/{pluginKey}/
        Path pluginDir = this.resourceStorageLocation.resolve(pluginKey);
        if (Files.exists(pluginDir)) {
            deleteDirectory(pluginDir.toFile());
            log.info("已删除插件目录: {}", pluginDir);
        } else {
            log.warn("插件目录不存在: {}", pluginDir);
        }
    }



    // 解压到指定目录
    private void unzipTo(Path zipFilePath, Path destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir.toFile(), zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    // 递归复制目录
    private void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Path target = dest.resolve(src.relativize(source));
                if (Files.isDirectory(source)) {
                    if (!Files.exists(target)) Files.createDirectory(target);
                } else {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 递归删除目录
    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        dir.delete();
    }

    /**
     * 安全地创建新文件，防止Zip Slip漏洞
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


    /**
     * 上传完整的资源ZIP文件并处理
     * 
     * @param file 上传的ZIP文件
     * @param uploaderId 上传者ID
     * @return 处理后的插件信息
     * @throws IOException 文件处理异常
     */
    @Override
    public Plugin uploadResourceZip(MultipartFile file, String uploaderId) throws IOException {
        // 直接调用现有的storeResource方法，然后返回插件信息
        storeResource(file, uploaderId);
        
        // 从解压后的plugin.json中读取插件信息，而不是从文件名中提取
        // 因为storeResource已经处理了文件，我们需要重新查询数据库获取最新信息
        
        // 获取文件名（用于日志记录）
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("处理上传文件: {}", fileName);
        
        // 由于storeResource已经将插件信息保存到数据库，
        // 我们需要通过其他方式获取插件信息
        // 这里我们通过上传者ID和最近时间来找对应的插件
        
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uploaded_by", uploaderId);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 1");
        
        Plugin plugin = pluginMapper.selectOne(queryWrapper);
        
        if (plugin == null) {
            // 如果找不到，尝试通过文件名（去掉.zip后缀）查找
            String fileNameWithoutExt = fileName.replace(".zip", "");
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("plugin_key", fileNameWithoutExt);
            plugin = pluginMapper.selectOne(queryWrapper);
            
            if (plugin == null) {
                throw new IOException("插件信息未找到，请检查文件内容是否正确");
            }
        }
        
        log.info("成功获取插件信息: pluginKey={}, pluginName={}", plugin.getPluginKey(), plugin.getName());
        return plugin;
    }

    /**
     * 上传文件分块
     * 
     * @param chunkInfo 分块信息
     * @param file 分块文件数据
     * @return 上传是否成功
     * @throws IOException 文件处理异常
     */
    @Override
    public boolean uploadChunk(FileChunk chunkInfo, MultipartFile file) throws IOException {
        try {
            String fileId = chunkInfo.getFileId();
            int chunkNumber = chunkInfo.getChunkNumber();
            int totalChunks = chunkInfo.getTotalChunks();
            
            // 创建分块存储目录
            Path chunkDir = resourceStorageLocation.resolve("chunks").resolve(fileId);
            Files.createDirectories(chunkDir);
            
            // 保存分块文件 - 使用缓冲流提高性能和稳定性
            Path chunkFile = chunkDir.resolve("chunk_" + chunkNumber);
            
            // 使用缓冲流进行文件复制，避免直接流复制可能出现的问题
            try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream(), 8192);
                 BufferedOutputStream bos = new BufferedOutputStream(
                     Files.newOutputStream(chunkFile), 8192)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;
                
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                
                bos.flush();
                
                // 验证分块文件
                if (!Files.exists(chunkFile) || Files.size(chunkFile) == 0) {
                    throw new IOException("分块文件保存后验证失败");
                }
                
                log.debug("分块 {} 保存成功，大小: {} bytes", chunkNumber, totalBytes);
            }
            
            // 更新上传进度
            UploadProgress progress = uploadProgressMap.computeIfAbsent(fileId, 
                k -> {
                    UploadProgress p = new UploadProgress();
                    p.setFileId(fileId);
                    p.setTotalChunks(totalChunks);
                    p.setStatus(UploadProgress.STATUS_UPLOADING);
                    p.setUploadedChunks(new java.util.HashSet<>());
                    return p;
                });
            
            // 记录已上传的分块
            progress.getUploadedChunks().add(chunkNumber);
            progress.setCurrentChunk(progress.getUploadedChunks().size());
            progress.calculateProgress();
            
            // 记录分块存储路径
            chunkStorageMap.put(fileId, chunkDir);
            
            log.info("文件块上传成功: fileId={}, chunkNumber={}/{}, 进度={}%", 
                fileId, chunkNumber, totalChunks, (int)progress.getProgress());
            
            return true;
        } catch (Exception e) {
            log.error("文件块上传失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 合并已上传的文件分块为完整文件
     * 
     * @param fileId 文件唯一标识
     * @param fileName 原始文件名
     * @param uploaderId 上传者ID
     * @return 合并后的插件信息
     * @throws IOException 文件处理异常
     */
    @Override
    public Plugin mergeChunks(String fileId, String fileName, String uploaderId) throws IOException {
        try {
            Path chunkDir = chunkStorageMap.get(fileId);
            if (chunkDir == null || !Files.exists(chunkDir)) {
                throw new IOException("分块文件目录不存在: " + fileId);
            }
            
            // 创建合并后的文件
            Path mergedFile = resourceStorageLocation.resolve("temp_" + fileId + ".zip");
            
            // 获取上传进度信息
            UploadProgress progress = uploadProgressMap.get(fileId);
            if (progress == null) {
                throw new IOException("上传进度信息不存在: " + fileId);
            }
            
            // 智能验证分块文件：只检查实际存在的分块
            java.util.Set<Integer> uploadedChunks = progress.getUploadedChunks();
            if (uploadedChunks == null || uploadedChunks.isEmpty()) {
                throw new IOException("没有找到已上传的分块文件");
            }
            
            // 获取所有实际存在的分块文件
            java.util.List<Integer> existingChunks = new java.util.ArrayList<>();
            try (var stream = Files.list(chunkDir)) {
                stream.filter(path -> path.getFileName().toString().startsWith("chunk_"))
                      .forEach(path -> {
                          try {
                              String chunkName = path.getFileName().toString();
                              int chunkNumber = Integer.parseInt(chunkName.substring(6)); // 去掉"chunk_"前缀
                              existingChunks.add(chunkNumber);
                          } catch (NumberFormatException e) {
                              log.warn("无法解析分块文件名: {}", path.getFileName());
                          }
                      });
            }
            
            // 验证所有已记录的分块都存在
            for (Integer chunkNumber : uploadedChunks) {
                if (!existingChunks.contains(chunkNumber)) {
                    throw new IOException("分块文件缺失: chunk_" + chunkNumber);
                }
            }
            
            log.info("分块验证通过，找到 {} 个分块文件", existingChunks.size());
            
            // 按顺序合并分块文件 - 使用缓冲流提高性能和稳定性
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    Files.newOutputStream(mergedFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), 8192)) {
                
                // 按分块序号排序
                java.util.List<Integer> sortedChunks = new java.util.ArrayList<>(existingChunks);
                java.util.Collections.sort(sortedChunks);
                
                long totalBytesWritten = 0;
                
                for (Integer chunkNumber : sortedChunks) {
                    Path chunkFile = chunkDir.resolve("chunk_" + chunkNumber);
                    try {
                        long chunkSize = Files.size(chunkFile);
                        log.info("合并分块 {}: 大小={} bytes", chunkNumber, chunkSize);
                        
                        // 使用缓冲读取和写入，避免内存问题
                        try (BufferedInputStream bis = new BufferedInputStream(
                                Files.newInputStream(chunkFile), 8192)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            long chunkBytesWritten = 0;
                            
                            while ((bytesRead = bis.read(buffer)) != -1) {
                                bos.write(buffer, 0, bytesRead);
                                chunkBytesWritten += bytesRead;
                            }
                            
                            // 验证传输的完整性
                            if (chunkBytesWritten != chunkSize) {
                                throw new IOException(String.format("分块 %d 传输不完整: 预期=%d, 实际=%d", 
                                    chunkNumber, chunkSize, chunkBytesWritten));
                            }
                            
                            totalBytesWritten += chunkBytesWritten;
                        }
                        
                        bos.flush(); // 确保数据写入磁盘
                        log.debug("分块 {} 合并完成，累计写入: {} bytes", chunkNumber, totalBytesWritten);
                        
                    } catch (IOException e) {
                        throw new IOException("复制分块文件失败: chunk_" + chunkNumber, e);
                    }
                }
                
                log.info("所有分块合并完成，总写入字节数: {} bytes", totalBytesWritten);
            }
            
            // 验证合并后的文件
            if (!Files.exists(mergedFile) || Files.size(mergedFile) == 0) {
                throw new IOException("合并后的文件无效或为空");
            }
            
            log.info("分块合并完成，文件大小: {} bytes", Files.size(mergedFile));
            
            // 基本文件完整性检查
            if (!isFileIntegrityValid(mergedFile)) {
                throw new IOException("合并后的文件基本完整性检查失败");
            }
            
            // 验证ZIP文件完整性
            if (!isValidZipFile(mergedFile)) {
                // 添加详细的ZIP文件诊断
                diagnoseZipFile(mergedFile);
                throw new IOException("合并后的ZIP文件损坏或无效");
            }
            
            log.info("ZIP文件完整性验证通过");
            
            // 创建MultipartFile包装器并调用现有的storeResource方法
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() { return "file"; }
                @Override
                public String getOriginalFilename() { return fileName; }
                @Override
                public String getContentType() { return "application/zip"; }
                @Override
                public boolean isEmpty() { return false; }
                @Override
                public long getSize() { 
                    try {
                        return Files.size(mergedFile);
                    } catch (IOException e) {
                        throw new RuntimeException("获取文件大小失败", e);
                    }
                }
                @Override
                public byte[] getBytes() throws IOException { return Files.readAllBytes(mergedFile); }
                @Override
                public java.io.InputStream getInputStream() throws IOException { return Files.newInputStream(mergedFile); }
                @Override
                public void transferTo(File dest) throws IOException { Files.copy(mergedFile, dest.toPath(), StandardCopyOption.REPLACE_EXISTING); }
            };
            
            // 处理合并后的文件
            Plugin plugin = uploadResourceZip(multipartFile, uploaderId.toString());
            
            // 清理临时文件
            cleanupChunkFiles(fileId);
            
            return plugin;
            
        } catch (Exception e) {
            log.error("文件合并失败: {}", e.getMessage(), e);
            
            // 详细的错误分析和恢复建议
            if (e instanceof IOException) {
                log.error("IO异常详情: {}", e.getMessage());
                if (e.getCause() != null) {
                    log.error("根本原因: {}", e.getCause().getMessage());
                }
            }
            
            // 清理可能损坏的临时文件
            try {
                Path tempFile = resourceStorageLocation.resolve("temp_" + fileId + ".zip");
                if (Files.exists(tempFile)) {
                    long tempFileSize = Files.size(tempFile);
                    log.warn("清理损坏的临时文件: {} (大小: {} bytes)", tempFile, tempFileSize);
                    Files.delete(tempFile);
                }
            } catch (Exception cleanupEx) {
                log.warn("清理临时文件失败: {}", cleanupEx.getMessage());
            }
            
            // 检查分块文件状态，提供诊断信息
            try {
                Path chunkDir = chunkStorageMap.get(fileId);
                if (chunkDir != null && Files.exists(chunkDir)) {
                    long chunkDirSize = Files.walk(chunkDir)
                        .filter(Files::isRegularFile)
                        .mapToLong(path -> {
                            try { return Files.size(path); }
                            catch (IOException ex) { return 0; }
                        })
                        .sum();
                    log.warn("分块目录状态: {} (总大小: {} bytes)", chunkDir, chunkDirSize);
                }
            } catch (Exception diagnosticEx) {
                log.warn("诊断分块文件状态失败: {}", diagnosticEx.getMessage());
            }
            
            throw e;
        }
    }



    /**
     * 清理分块上传的临时文件
     */
    private void cleanupChunkFiles(String fileId) {
        try {
            // 删除分块文件
            Path chunkDir = chunkStorageMap.remove(fileId);
            if (chunkDir != null && Files.exists(chunkDir)) {
                deleteDirectory(chunkDir.toFile());
                log.info("已清理分块文件: {}", chunkDir);
            }
            
            // 删除进度信息
            uploadProgressMap.remove(fileId);
            // 删除时间戳记录 - 添加这一行！
            chunkTimestamps.remove(fileId);
            // 删除临时合并文件
            Path tempFile = resourceStorageLocation.resolve("temp_" + fileId + ".zip");
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
                log.info("已删除临时文件: {}", tempFile);
            }
        } catch (Exception e) {
            log.error("清理临时文件失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 基本文件完整性检查
     */
    private boolean isFileIntegrityValid(Path file) {
        try {
            // 检查文件是否存在且可读
            if (!Files.exists(file) || !Files.isReadable(file)) {
                log.error("文件不存在或不可读: {}", file);
                return false;
            }
            
            // 检查文件大小
            long fileSize = Files.size(file);
            if (fileSize == 0) {
                log.error("文件大小为0: {}", file);
                return false;
            }
            
            // 检查文件头是否为ZIP格式 (ZIP文件以PK开头)
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file), 4)) {
                byte[] header = new byte[4];
                int bytesRead = bis.read(header);
                if (bytesRead < 4) {
                    log.error("无法读取文件头: {}", file);
                    return false;
                }
                
                // ZIP文件头应该是 PK\x03\x04
                if (header[0] != 0x50 || header[1] != 0x4B || header[2] != 0x03 || header[3] != 0x04) {
                    log.error("文件头不是有效的ZIP格式: {}", file);
                    return false;
                }
                
                log.debug("文件头验证通过: {}", file);
                return true;
            }
            
        } catch (Exception e) {
            log.error("文件完整性检查失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 验证ZIP文件完整性 - 改进版本，更健壮的错误处理
     */
    private boolean isValidZipFile(Path zipFile) {
        try {
            // 首先进行基本文件头验证
            if (!isFileIntegrityValid(zipFile)) {
                log.error("ZIP文件头验证失败: {}", zipFile);
                return false;
            }
            
            // 使用更安全的ZIP验证方法
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFile), 8192)) {
                // 跳过ZIP文件头（前4字节）
                bis.skip(4);
                
                // 尝试读取ZIP文件的中央目录结构
                byte[] buffer = new byte[8192];
                int bytesRead;
                boolean foundCentralDirectory = false;
                long totalBytesRead = 0;
                
                while ((bytesRead = bis.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    
                    // 查找ZIP中央目录标记 (0x02014b50)
                    for (int i = 0; i < bytesRead - 3; i++) {
                        if (buffer[i] == 0x50 && buffer[i + 1] == 0x4B && 
                            buffer[i + 2] == 0x01 && buffer[i + 3] == 0x02) {
                            foundCentralDirectory = true;
                            log.debug("找到ZIP中央目录标记，位置: {}", totalBytesRead - bytesRead + i);
                            break;
                        }
                    }
                    
                    if (foundCentralDirectory) {
                        break;
                    }
                    
                    // 限制读取大小，避免处理过大的文件
                    if (totalBytesRead > 1024 * 1024) { // 1MB
                        log.warn("ZIP文件验证：已读取1MB但未找到中央目录标记");
                        break;
                    }
                }
                
                if (foundCentralDirectory) {
                    log.info("ZIP文件结构验证通过: {}", zipFile);
                    return true;
                } else {
                    // 如果找不到中央目录，尝试使用备用验证方法
                    log.warn("ZIP文件结构验证失败：未找到中央目录标记，尝试备用验证方法");
                    return validateZipFileAlternative(zipFile);
                }
            }
            
        } catch (Exception e) {
            log.error("ZIP文件验证失败: {}", e.getMessage(), e);
            // 记录更详细的错误信息
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            return false;
        }
    }
    
    /**
     * 备用ZIP文件验证方法 - 当中央目录验证失败时使用
     */
    private boolean validateZipFileAlternative(Path zipFile) {
        try {
            // 检查文件大小是否合理
            long fileSize = Files.size(zipFile);
            if (fileSize < 100) { // 文件太小，不可能是有效的ZIP
                log.warn("ZIP文件太小: {} bytes", fileSize);
                return false;
            }
            
            // 检查文件末尾是否有ZIP结束标记 - 搜索更大的范围
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFile))) {
                // 搜索最后64KB，因为ZIP结束标记可能在文件末尾的某个位置
                long searchStart = Math.max(0, fileSize - 64 * 1024);
                bis.skip(searchStart);
                
                byte[] searchBuffer = new byte[(int) (fileSize - searchStart)];
                int bytesRead = bis.read(searchBuffer);
                
                if (bytesRead >= 4) {
                    // 从后往前搜索ZIP结束标记 (0x06054b50)
                    for (int i = bytesRead - 4; i >= 0; i--) {
                        if (searchBuffer[i] == 0x50 && 
                            searchBuffer[i + 1] == 0x4B && 
                            searchBuffer[i + 2] == 0x05 && 
                            searchBuffer[i + 3] == 0x06) {
                            
                            long markerPosition = searchStart + i;
                            log.info("备用验证通过：找到ZIP结束标记，位置: {} bytes，文件大小: {} bytes", 
                                   markerPosition, fileSize);
                            return true;
                        }
                    }
                }
            }
            
            log.warn("备用验证失败：在最后64KB中未找到ZIP结束标记");
            return false;
            
        } catch (Exception e) {
            log.error("备用ZIP验证失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 初始化分块上传
     * 
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param totalChunks 总分块数
     * @return 文件唯一标识
     */
    @Override
    public String initChunkUpload(String fileName, long fileSize, int totalChunks) {
        String fileId = UUID.randomUUID().toString();
        
        // 创建分块存储目录
        Path chunkDir = resourceStorageLocation.resolve("chunks").resolve(fileId);
        try {
            Files.createDirectories(chunkDir);
            chunkStorageMap.put(fileId, chunkDir);
            
            // 记录分块文件创建时间
            chunkTimestamps.put(fileId, LocalDateTime.now());
            
            // 创建上传进度记录
            UploadProgress progress = new UploadProgress(fileId, fileName, "unknown");
            progress.setTotalChunks(totalChunks);
            progress.setTotalBytes(fileSize);
            uploadProgressMap.put(fileId, progress);
            
            log.info("分块上传初始化成功: fileId={}, fileName={}, fileSize={}, totalChunks={}", 
                    fileId, fileName, fileSize, totalChunks);
            
            return fileId;
        } catch (IOException e) {
            log.error("创建分块存储目录失败: {}", e.getMessage(), e);
            throw new RuntimeException("分块上传初始化失败", e);
        }
    }
    
    /**
     * 清理过期的分块文件
     * 
     * @return 清理的文件数量
     */
    @Override
    public int cleanupExpiredChunks() {
        log.info("开始清理过期分块文件，过期时间: {} 分钟", CHUNK_EXPIRE_MINUTES);
        
        final int[] cleanedCount = {0};
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(CHUNK_EXPIRE_MINUTES);
        
        // 1. 清理内存中过期的分块文件记录
        chunkTimestamps.entrySet().removeIf(entry -> {
            String fileId = entry.getKey();
            LocalDateTime createTime = entry.getValue();
            
            if (createTime.isBefore(expireTime)) {
                try {
                    cleanupChunkFiles(fileId);
                    cleanedCount[0]++;
                    log.info("已清理过期分块文件: fileId={}, 创建时间={}", fileId, createTime);
                    return true; // 移除这个条目
                } catch (Exception e) {
                    log.error("清理过期分块文件失败: fileId={}, error={}", fileId, e.getMessage());
                    return false; // 保留这个条目，下次再试
                }
            }
            return false; // 未过期，保留
        });
        
        // 2. 扫描物理目录，清理可能遗漏的过期分块文件
        try {
            Path chunksDir = resourceStorageLocation.resolve("chunks");
            if (Files.exists(chunksDir)) {
                Files.list(chunksDir)
                    .filter(Files::isDirectory)
                    .forEach(chunkDir -> {
                        String dirName = chunkDir.getFileName().toString();
                        
                        // 检查目录是否在内存中有记录
                        if (!chunkTimestamps.containsKey(dirName)) {
                            // 没有时间戳记录，检查目录创建时间
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(chunkDir, BasicFileAttributes.class);
                                LocalDateTime dirCreateTime = attrs.creationTime().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
                                
                                if (dirCreateTime.isBefore(expireTime)) {
                                    // 目录创建时间已过期，清理
                                    deleteDirectory(chunkDir.toFile());
                                    log.info("清理过期的分块目录: dir={}, 创建时间={}", chunkDir, dirCreateTime);
                                    cleanedCount[0]++;
                                } else {
                                    log.debug("分块目录未过期，保留: dir={}, 创建时间={}", chunkDir, dirCreateTime);
                                }
                            } catch (Exception e) {
                                log.error("检查分块目录时间失败: dir={}, error={}", chunkDir, e.getMessage());
                                // 如果无法检查时间，为了安全起见，也清理掉
                                try {
                                    deleteDirectory(chunkDir.toFile());
                                    log.info("清理无法检查时间的分块目录: {}", chunkDir);
                                    cleanedCount[0]++;
                                } catch (Exception cleanupEx) {
                                    log.error("清理分块目录失败: dir={}, error={}", chunkDir, cleanupEx.getMessage());
                                }
                            }
                        }
                    });
            }
        } catch (Exception e) {
            log.error("扫描物理目录失败: {}", e.getMessage(), e);
        }
        
        log.info("过期分块文件清理完成，共清理 {} 个文件", cleanedCount[0]);
        return cleanedCount[0];
    }
    
        /**
     * 定时清理任务：每分钟执行一次，清理过期的分块文件（测试用）
     */
//    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // 12小时执行一次
    @Scheduled(fixedRate = 60 * 1000) // 每分钟执行一次（测试用）
    public void scheduledCleanupExpiredChunks() {
        log.debug("定时清理任务开始执行");
        try {
            int cleanedCount = cleanupExpiredChunks();
            if (cleanedCount > 0) {
                log.info("定时清理任务完成，清理了 {} 个过期文件", cleanedCount);
            }
        } catch (Exception e) {
            log.error("定时清理任务执行失败: {}", e.getMessage(), e);
        }
    }



    //插件状态更新
    @Override
    public void updatePluginStatus(Long id, String status) {
        Plugin plugin = new Plugin();
        plugin.setId(id);
        plugin.setStatus(status);
        pluginMapper.updateById(plugin);
        log.info("插件状态更新完成 - ID: {}, 状态: {}", id, status);
    }

    //启用插件同时协调其他插件状态
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePluginStatusSmart(Long id, String status) {
        if ("enabled".equals(status)) {
            // 如果要启用插件，先禁用所有其他插件
            QueryWrapper<Plugin> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("status", "enabled");
            Plugin disablePlugin = new Plugin();
            disablePlugin.setStatus("disabled");
            pluginMapper.update(disablePlugin, updateWrapper);
            
            // 然后启用指定的插件
            Plugin enablePlugin = new Plugin();
            enablePlugin.setId(id);
            enablePlugin.setStatus("enabled");
            pluginMapper.updateById(enablePlugin);
            
            log.info("插件状态更新完成 - 启用插件ID: {}, 其他插件已自动禁用", id);
        } else {
            // 如果要禁用插件，直接更新状态
            Plugin plugin = new Plugin();
            plugin.setId(id);
            plugin.setStatus(status);
            pluginMapper.updateById(plugin);
            
            log.info("插件状态更新完成 - 禁用插件ID: {}", id);
        }
    }

    @Override
    public Plugin getEnabledPlugin() {
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "enabled");
        return pluginMapper.selectOne(queryWrapper);
    }

    @Override
    public String getEnabledPluginKey() {
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "enabled");
        Plugin plugin = pluginMapper.selectOne(queryWrapper);
        return plugin.getPluginKey();
    }

    @Override
    public Plugin getPluginByKey(String pluginKey) {
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plugin_key", pluginKey);
        return pluginMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Plugin> getPluginsList(AdminPluginsQueryListDto queryDto) {
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (queryDto != null) {
            // 插件标识模糊查询
            if (StringUtils.hasText(queryDto.getPluginKey())) {
                queryWrapper.like("plugin_key", queryDto.getPluginKey());
            }
            
            // 插件名称模糊查询
            if (StringUtils.hasText(queryDto.getName())) {
                queryWrapper.like("name", queryDto.getName());
            }
            
            // 版本精确查询
            if (StringUtils.hasText(queryDto.getVersion())) {
                queryWrapper.eq("version", queryDto.getVersion());
            }
            
            // 作者模糊查询
            if (StringUtils.hasText(queryDto.getAuthor())) {
                queryWrapper.like("author", queryDto.getAuthor());
            }
            
            // 描述模糊查询
            if (StringUtils.hasText(queryDto.getDescription())) {
                queryWrapper.like("description", queryDto.getDescription());
            }
            
            // 状态精确查询
            if (StringUtils.hasText(queryDto.getStatus())) {
                queryWrapper.eq("status", queryDto.getStatus());
            }
            
            // 上传者模糊查询
            if (StringUtils.hasText(queryDto.getUploadedBy())) {
                queryWrapper.like("uploaded_by", queryDto.getUploadedBy());
            }
            
            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(queryDto.getSearchValue())) {
                String searchValue = queryDto.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                    .like("plugin_key", searchValue)
                    .or()
                    .like("name", searchValue)
                    .or()
                    .like("author", searchValue)
                    .or()
                    .like("description", searchValue)
                );
            }
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_at");
        
        // 分页查询
        if (queryDto != null && queryDto.getPageSize() != null && queryDto.getPageSize() > 0) {
            queryWrapper.last("LIMIT " + queryDto.getOffset() + ", " + queryDto.getPageSize());
        }
        
        return pluginMapper.selectList(queryWrapper);
    }

    @Override
    public Long getPluginsCount(AdminPluginsQueryListDto queryDto) {
        QueryWrapper<Plugin> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件（与getAllPlugins保持一致，但不包含分页和排序）
        if (queryDto != null) {
            // 插件标识模糊查询
            if (StringUtils.hasText(queryDto.getPluginKey())) {
                queryWrapper.like("plugin_key", queryDto.getPluginKey());
            }
            
            // 插件名称模糊查询
            if (StringUtils.hasText(queryDto.getName())) {
                queryWrapper.like("name", queryDto.getName());
            }
            
            // 版本精确查询
            if (StringUtils.hasText(queryDto.getVersion())) {
                queryWrapper.eq("version", queryDto.getVersion());
            }
            
            // 作者模糊查询
            if (StringUtils.hasText(queryDto.getAuthor())) {
                queryWrapper.like("author", queryDto.getAuthor());
            }
            
            // 描述模糊查询
            if (StringUtils.hasText(queryDto.getDescription())) {
                queryWrapper.like("description", queryDto.getDescription());
            }
            
            // 状态精确查询
            if (StringUtils.hasText(queryDto.getStatus())) {
                queryWrapper.eq("status", queryDto.getStatus());
            }
            
            // 上传者模糊查询
            if (StringUtils.hasText(queryDto.getUploadedBy())) {
                queryWrapper.like("uploaded_by", queryDto.getUploadedBy());
            }
            
            // 通用搜索值（在所有文本字段中搜索）
            if (StringUtils.hasText(queryDto.getSearchValue())) {
                String searchValue = queryDto.getSearchValue();
                queryWrapper.and(wrapper -> wrapper
                    .like("plugin_key", searchValue)
                    .or()
                    .like("name", searchValue)
                    .or()
                    .like("author", searchValue)
                    .or()
                    .like("description", searchValue)
                );
            }
        }
        
        return pluginMapper.selectCount(queryWrapper);
    }

    @Override
    public Resource downloadPlugin(String pluginKey) {
        try {
            log.info("开始准备下载插件资源包: {}", pluginKey);
            
            // 查找插件信息
            Plugin plugin = getPluginByKey(pluginKey);
            if (plugin == null) {
                log.error("插件不存在: {}", pluginKey);
                return null;
            }
            
            // 构建插件目录路径
            Path pluginDir = this.resourceStorageLocation.resolve(pluginKey);
            Path jsonDir = pluginDir.resolve(pluginKey); // ${upload.resource.path}+pluginKey+pluginKey
            Path zipPath = pluginDir.resolve(pluginKey + ".zip");
            
            log.info("插件目录: {}", pluginDir);
            log.info("JSON文件目录: {}", jsonDir);
            log.info("ZIP文件路径: {}", zipPath);
            
            // 1. 导出MySQL表数据为JSON文件
            if (!Files.exists(jsonDir)) {
                Files.createDirectories(jsonDir);
                log.info("创建JSON文件目录: {}", jsonDir);
            }
            
            log.info("开始导出插件数据为JSON文件...");
            boolean exportSuccess = dataExportService.exportPluginDataToJson(pluginKey, jsonDir.toString());
            if (!exportSuccess) {
                log.error("导出插件数据失败: {}", pluginKey);
                return null;
            }
            log.info("插件数据导出完成");
            
            // 2. 压缩JSON文件目录为ZIP
            log.info("开始压缩JSON文件目录...");
            boolean compressSuccess = zipService.compressDirectory(jsonDir.toString(), zipPath.toString());
            if (!compressSuccess) {
                log.error("压缩插件目录失败: {}", pluginKey);
                return null;
            }
            log.info("插件目录压缩完成: {}", zipPath);
            
            // 3. 返回ZIP文件作为下载资源
            Resource resource = new UrlResource(zipPath.toUri());
            if (resource.exists() && resource.isReadable()) {
                log.info("插件资源包准备完成，文件大小: {} bytes", Files.size(zipPath));
                return resource;
            } else {
                log.error("ZIP文件不存在或不可读: {}", zipPath);
                return null;
            }
            
        } catch (Exception e) {
            log.error("下载插件失败: pluginKey={}, error={}", pluginKey, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Plugin getPluginById(Long id) {
        return pluginMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePluginCompletely(Long id) {
        try {
            // 1. 获取插件信息
            Plugin plugin = pluginMapper.selectById(id);
            if (plugin == null) {
                return false;
            }
            
            String pluginKey = plugin.getPluginKey();
            
            // 2. 删除文件系统中的插件包和数据库记录
            deletePluginResources(pluginKey);
            
            // 3. 最后删除插件本身
            int pluginDeleted = pluginMapper.deleteById(id);
            
            // 4. 记录删除结果
            log.info("插件删除完成 - 插件: {}, 插件记录: {}", pluginKey, pluginDeleted);
            
            return pluginDeleted > 0;
        } catch (Exception e) {
            log.error("删除插件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 诊断ZIP文件问题，提供详细的文件分析信息
     */
    private void diagnoseZipFile(Path zipFile) {
        try {
            log.error("=== ZIP文件诊断开始 ===");
            log.error("文件路径: {}", zipFile);
            log.error("文件大小: {} bytes", Files.size(zipFile));
            log.error("文件是否存在: {}", Files.exists(zipFile));
            log.error("文件是否可读: {}", Files.isReadable(zipFile));
            
            // 检查文件头
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFile))) {
                byte[] header = new byte[32]; // 读取前32字节进行分析
                int bytesRead = bis.read(header);
                
                if (bytesRead >= 4) {
                    log.error("文件头前4字节: 0x{:02X} 0x{:02X} 0x{:02X} 0x{:02X}", 
                        header[0] & 0xFF, header[1] & 0xFF, header[2] & 0xFF, header[3] & 0xFF);
                    
                    // 检查是否为ZIP格式
                    if (header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04) {
                        log.error("✓ ZIP文件头正确");
                    } else {
                        log.error("✗ ZIP文件头不正确，期望: PK\\x03\\x04");
                    }
                }
                
                if (bytesRead >= 32) {
                    log.error("文件头前32字节: {}", bytesToHex(header));
                }
            }
            
            // 检查文件末尾
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFile))) {
                long fileSize = Files.size(zipFile);
                if (fileSize > 32) {
                    bis.skip(fileSize - 32);
                    byte[] footer = new byte[32];
                    int bytesRead = bis.read(footer);
                    if (bytesRead > 0) {
                        log.error("文件末尾{}字节: {}", bytesRead, bytesToHex(footer, bytesRead));
                        
                        // 检查ZIP结束标记 - 在最后4字节中查找
                        if (bytesRead >= 4) {
                            boolean foundEndMarker = false;
                            for (int i = bytesRead - 4; i >= 0; i--) {
                                if (footer[i] == 0x50 && footer[i + 1] == 0x4B && 
                                    footer[i + 2] == 0x05 && footer[i + 3] == 0x06) {
                                    log.error("✓ 找到ZIP结束标记 (PK\\x05\\x06)，位置: 末尾{}字节", bytesRead - i);
                                    foundEndMarker = true;
                                    break;
                                }
                            }
                            if (!foundEndMarker) {
                                log.error("✗ 在最后32字节中未找到ZIP结束标记");
                            }
                        }
                    }
                }
            }
            
            // 尝试查找中央目录标记
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFile))) {
                log.error("搜索中央目录标记...");
                byte[] searchBuffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                boolean foundCentralDir = false;
                
                while ((bytesRead = bis.read(searchBuffer)) != -1 && !foundCentralDir) {
                    totalBytesRead += bytesRead;
                    
                    // 查找中央目录标记 (0x02014b50)
                    for (int i = 0; i < bytesRead - 3; i++) {
                        if (searchBuffer[i] == 0x50 && searchBuffer[i + 1] == 0x4B && 
                            searchBuffer[i + 2] == 0x01 && searchBuffer[i + 3] == 0x02) {
                            log.error("✓ 找到中央目录标记，位置: {} bytes", totalBytesRead - bytesRead + i);
                            foundCentralDir = true;
                            break;
                        }
                    }
                    
                    // 限制搜索范围
                    if (totalBytesRead > 1024 * 1024) { // 1MB
                        log.error("已搜索1MB，停止搜索");
                        break;
                    }
                }
                
                if (!foundCentralDir) {
                    log.error("✗ 未找到中央目录标记");
                }
            }
            
            log.error("=== ZIP文件诊断结束 ===");
            
        } catch (Exception e) {
            log.error("ZIP文件诊断失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, bytes.length);
    }
    
    /**
     * 将字节数组转换为十六进制字符串（指定长度）
     */
    private String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length && i < bytes.length; i++) {
            sb.append(String.format("%02X ", bytes[i] & 0xFF));
        }
        return sb.toString().trim();
    }

}
