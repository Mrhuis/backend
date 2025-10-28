package com.example.backend.controller.admin;

import com.example.backend.common.Result;
import com.example.backend.common.vo.QueryListVo;
import com.example.backend.controller.admin.dto.AdminPluginsQueryListDto;
import com.example.backend.entity.Plugin;
import com.example.backend.controller.admin.dto.DownloadTaskStatus;
import com.example.backend.controller.admin.dto.FileChunk;
import java.util.UUID;

import com.example.backend.service.admin.resource_manage.AsyncDownloadService;
import com.example.backend.service.admin.resource_manage.PluginInitializationService;
import com.example.backend.service.admin.resource_manage.PluginService;
import com.example.backend.service.admin.resource_manage.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/admin/plugins")
@Controller
public class AdminResourceManageController {

    private static final Logger log = LoggerFactory.getLogger(AdminResourceManageController.class);

    @Autowired
    private PluginService pluginService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PluginInitializationService pluginInitializationService;
    
    @Autowired
    private AsyncDownloadService asyncDownloadService;


    /**
     * 获取插件列表
     * @return 插件列表
     */
    @PostMapping("/list")
    public Result<QueryListVo> getPluginsList(@RequestBody AdminPluginsQueryListDto req) {
        try {
            // 获取插件列表
            List<Plugin> plugins = pluginService.getPluginsList(req);
            
            // 获取总数
            Long total = pluginService.getPluginsCount(req);
            
            // 构建分页结果
            QueryListVo result = new QueryListVo();
            result.setRecords(plugins.stream().map(plugin -> (Object) plugin).collect(java.util.stream.Collectors.toList()));
            result.setTotal(total);
            result.setCurrent(req.getPageIndex() != null ? req.getPageIndex() : 1);
            result.setSize(req.getPageSize() != null ? req.getPageSize() : 100);
            result.setPages((int) Math.ceil((double) total / (req.getPageSize() != null ? req.getPageSize() : 100)));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取插件列表失败", e);
            return Result.error("获取插件列表失败");
        }
    }





    /**
     * 初始化插件（解压资源包并导入JSON数据到MySQL表）
     * @param id 插件ID
     * @return 初始化结果
     */
    @PostMapping("/{id}/initialize")
    public ResponseEntity<String> initializePlugin(@PathVariable Long id) {
        try {
            // 获取插件信息
            Plugin plugin = pluginService.getPluginById(id);
            if (plugin == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 检查插件当前状态
            if (!"uninitialized".equals(plugin.getStatus())) {
                return ResponseEntity.badRequest().body("插件状态不是未初始化状态，无法进行初始化操作");
            }
            
            // 检查插件资源包是否存在
            if (plugin.getStoragePath() == null || plugin.getStoragePath().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("插件资源包路径不存在，无法进行初始化操作");
            }
            
            // 执行插件初始化（解压资源包并导入数据）
            boolean success = pluginInitializationService.initializePlugin(plugin);
            
            if (success) {
                return ResponseEntity.ok("插件初始化成功！资源包已解压，JSON数据已导入到对应表中，插件状态已更新为已禁用");
            } else {
                return ResponseEntity.internalServerError().body("插件初始化失败，请检查资源包格式和数据库连接");
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("初始化插件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 智能更新插件状态（启用时自动禁用其他插件）
     * @param id 插件ID
     * @param request 包含新状态的请求体
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updatePluginStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().body("状态参数不能为空");
            }
            
            // 验证状态值
            if (!"enabled".equals(status) && !"disabled".equals(status)) {
                return ResponseEntity.badRequest().body("状态值只能是 'enabled' 或 'disabled'");
            }
            
            pluginService.updatePluginStatusSmart(id, status);
            
            if ("enabled".equals(status)) {
                return ResponseEntity.ok("插件启用成功，其他插件已自动禁用");
            } else {
                return ResponseEntity.ok("插件禁用成功");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("更新插件状态时发生错误: " + e.getMessage());
        }
    }



    /**
     * 完全删除插件（包括文件和数据）
     * @param id 插件ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlugin(@PathVariable Long id) {
        try {
            boolean success = pluginService.deletePluginCompletely(id);
            if (success) {
                return ResponseEntity.ok("插件及相关资源删除成功");
            } else {
                return ResponseEntity.badRequest().body("插件删除失败");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除插件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 异步下载插件资源包
     * @param pluginKey 插件唯一标识
     * @return 下载任务信息
     */
    @GetMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadPluginAsync(@RequestParam String pluginKey) {
        try {
            // 检查插件状态，未初始化的资源包不能下载
            Plugin plugin = pluginService.getPluginByKey(pluginKey);
            if (plugin == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "status", "error",
                    "message", "插件不存在"
                ));
            }
            
            if ("uninitialized".equals(plugin.getStatus())) {
                return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", "资源包未初始化，无法下载。请先初始化资源包后再进行下载操作。"
                ));
            }
            
            // 生成唯一的任务ID
            String taskId = UUID.randomUUID().toString();
            
            // 异步启动下载任务
            asyncDownloadService.prepareDownloadAsync(pluginKey, taskId);
            
            log.info("启动异步下载任务: pluginKey={}, taskId={}", pluginKey, taskId);
            
            return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", "processing",
                "message", "正在准备下载文件，请稍候...",
                "pluginKey", pluginKey
            ));
            
        } catch (Exception e) {
            log.error("启动异步下载任务失败: pluginKey={}, error={}", pluginKey, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "启动下载任务失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 查询下载任务状态
     * @param taskId 任务ID
     * @return 任务状态信息
     */
    @GetMapping("/download/status/{taskId}")
    public ResponseEntity<DownloadTaskStatus> getDownloadStatus(@PathVariable String taskId) {
        try {
            DownloadTaskStatus taskStatus = asyncDownloadService.getTaskStatus(taskId);
            return ResponseEntity.ok(taskStatus);
        } catch (Exception e) {
            log.error("查询下载任务状态失败: taskId={}, error={}", taskId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 实际下载文件（当任务完成后调用）
     * @param pluginKey 插件标识
     * @param taskId 任务ID
     * @return 文件资源
     */
    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String pluginKey, @RequestParam String taskId) {
        try {
            // 检查任务状态
            DownloadTaskStatus taskStatus = asyncDownloadService.getTaskStatus(taskId);
            if (!"completed".equals(taskStatus.getStatus())) {
                return ResponseEntity.badRequest()
                    .header("X-Error-Message", "下载任务尚未完成，请稍候...")
                    .build();
            }
            
            // 再次检查插件状态，确保安全性
            Plugin plugin = pluginService.getPluginByKey(pluginKey);
            if (plugin == null) {
                return ResponseEntity.notFound().build();
            }
            
            if ("uninitialized".equals(plugin.getStatus())) {
                return ResponseEntity.badRequest()
                    .header("X-Error-Message", "资源包未初始化，无法下载")
                    .build();
            }
            
            // 获取文件资源
            Resource resource = pluginService.downloadPlugin(pluginKey);
            if (resource != null && resource.exists()) {
                String filename = pluginKey + ".zip";
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("下载文件失败: pluginKey={}, taskId={}, error={}", pluginKey, taskId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    //资源包上传：
    /**
     * 分块上传 - 上传文件块
     * @param chunk 文件块信息
     * @param file 文件块数据
     * @return 上传结果
     */
    @PostMapping("/upload-chunk")
    public ResponseEntity<Map<String, Object>> uploadChunk(
            @RequestParam("chunk") String chunk,
            @RequestParam("file") MultipartFile file) {
        try {
            // 解析chunk参数（JSON格式）
            FileChunk chunkInfo = objectMapper.readValue(chunk, FileChunk.class);
            
            // 调用服务层处理分块上传
            boolean success = resourceService.uploadChunk(chunkInfo, file);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "文件块上传成功",
                    "chunkNumber", chunkInfo.getChunkNumber(),
                    "totalChunks", chunkInfo.getTotalChunks()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "文件块上传失败"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "文件块上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 分块上传 - 合并文件块
     * @param request 合并请求信息
     * @return 合并结果
     */
    @PostMapping("/merge-chunks")
    public ResponseEntity<Map<String, Object>> mergeChunks(
            @RequestBody Map<String, Object> request) {
        try {
            log.info("收到合并请求: {}", request);
            
            String fileId = (String) request.get("fileId");
            String fileName = (String) request.get("fileName");
            String uploaderId = (String) request.get("uploaderId");
            
            // 参数验证
            if (fileId == null || fileId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "fileId不能为空"
                ));
            }
            
            if (fileName == null || fileName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "fileName不能为空"
                ));
            }
            
            if (uploaderId == null || uploaderId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "uploaderId不能为空"
                ));
            }
            
            log.info("参数验证通过 - fileId: {}, fileName: {}, uploaderId: {}", fileId, fileName, uploaderId);
            
            // 调用服务层合并文件块
            Plugin plugin = resourceService.mergeChunks(fileId, fileName, uploaderId);
            
            log.info("文件合并成功: fileId={}, fileName={}, pluginId={}", fileId, fileName, plugin.getId());
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "文件合并成功",
                "pluginId", plugin.getId(),
                "fileName", fileName
            ));
        } catch (Exception e) {
            log.error("文件合并失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "文件合并失败: " + e.getMessage()
            ));
        }
    }



    /**
     * 获取当前启用的插件，是管理员-资源管理-插件管理-当前启用资源包显示绿色小方块
     * @return 当前启用的插件信息
     */
    @GetMapping("/enabled")
    public ResponseEntity<Plugin> getEnabledPlugin() {
        try {
            Plugin enabledPlugin = pluginService.getEnabledPlugin();
            if (enabledPlugin != null) {
                return ResponseEntity.ok(enabledPlugin);
            } else {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

} 