package com.example.backend.tool;

import com.example.backend.entity.Plugin;
import com.example.backend.service.admin.resource_manage.PluginService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ClassName: DirectoryTool
 * Package: com.example.backend.tool
 * Description:
 *
 * @Author 陈昊锡
 * @Create 2025/9/3 15:41
 * @Version 1.0
 */
public class DirectoryTool {
    // 目标目录名称（通过构造函数传入，支持任意目录名）
    private final String targetDirectoryName;

    /**
     * 构造函数：指定目标目录名称
     *
     * @param targetDirectoryName 要查找或创建的目录名称（如"media"、"docs"、"images"等）
     */
    public DirectoryTool(String targetDirectoryName) {
        if (targetDirectoryName == null || targetDirectoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("目标目录名称不能为空");
        }
        this.targetDirectoryName = targetDirectoryName.trim();
    }

    /**
     * 查找或创建目标目录（递归搜索）
     *
     * @param basePath 基础路径（从该路径开始搜索或创建目录）
     * @return 目标目录的绝对路径
     * 
     * 使用示例:
     * DirectoryTool tool = new DirectoryTool("media");
     * String absolutePath = tool.findOrCreateTargetDirectory("/uploads/resources");
     * // 返回 "/uploads/resources/media" 或在子目录中找到的media目录的绝对路径
     */
    public String findOrCreateTargetDirectory(String basePath) {
        File baseDir = new File(basePath);
        File targetDir = searchTargetDirectory(baseDir);

        // 如果未找到，则在基础路径下创建目标目录
        if (targetDir == null) {
            targetDir = new File(basePath, targetDirectoryName);
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new RuntimeException("无法创建目标目录: " + targetDir.getAbsolutePath());
            }
        }

        return targetDir.getAbsolutePath();
    }

    /**
     * 在基础路径下查找目标目录，并返回相对于基础路径的路径
     *
     * @param basePath 基础路径
     * @return 相对于基础路径的路径，使用正斜杠分隔符
     * 
     * 使用示例:
     * DirectoryTool tool = new DirectoryTool("DataStructure");
     * String relativePath = tool.findRelativePath("D:/uploads/resources");
     * // 如果找到目录 "D:/uploads/resources/V1/V1/DataStructure"，则返回 "V1/V1/DataStructure"
     * 
     * 另一个示例:
     * DirectoryTool tool = new DirectoryTool("media");
     * String relativePath = tool.findRelativePath("/app/uploads");
     * // 如果找到目录 "/app/uploads/course/intro/media"，则返回 "course/intro/media"
     */
    public String findRelativePath(String basePath) {
        // 创建基础目录的File对象，例如: basePath = "/uploads/resources"
        File baseDir = new File(basePath);
        // 在基础目录下递归搜索目标目录，例如: 搜索名为"DataStructure"的目录
        File targetDir = searchTargetDirectory(baseDir);

        // 如果未找到目标目录，返回null
        if (targetDir == null) {
            return null;
        }

        // 计算相对路径
        // 将基础路径转换为绝对路径并规范化，例如: "/uploads/resources" -> "/uploads/resources"
        Path base = Paths.get(basePath).toAbsolutePath().normalize();
        // 将找到的目标目录转换为绝对路径并规范化，例如: "/uploads/resources/V1/V1/DataStructure" -> "/uploads/resources/V1/V1/DataStructure"
        Path target = targetDir.toPath().toAbsolutePath().normalize();
        // 计算相对路径，例如: "V1/V1/DataStructure"
        Path relativePath = base.relativize(target);

        // 将路径分隔符统一为正斜杠，例如: "V1\V1\DataStructure" -> "V1/V1/DataStructure"
        return relativePath.toString().replace('\\', '/');
    }

    //获取当前启用插件文件夹的根绝对路径
    public String findCurrentPluginResorceRootAbsolutePath(String baseResourcePath, String pluginKey) {
        // 检查参数是否为空
        if (baseResourcePath == null || pluginKey == null) {
            return null;
        }

        // 获取基础路径的绝对路径
        Path absoluteBasePath = Paths.get(baseResourcePath).toAbsolutePath().normalize();

        // 添加路径在基础路径的基础上加一层名字叫pluginKey的路径后形成路径a
        Path pluginPath = absoluteBasePath.resolve(pluginKey);

        // 在路径a下寻找targetDirectoryName目录，并返回其目录的绝对路径
        File baseDir = pluginPath.toFile();
        File targetDir = searchTargetDirectory(baseDir);
        targetDir = new File(targetDir.getParent());

        if (targetDir == null) {
            return null;
        }

        // 返回目标目录的绝对路径
        return targetDir.getAbsolutePath();
    }


    /**
     * 查找当前启用插件文件夹的绝对路径
     * 
     * @param baseResourcePath 基础资源路径
     * @param pluginKey 插件键
     * @return 目标目录的绝对路径
     */
    public String findCurrentPluginFolderAbsolutePath(String baseResourcePath, String pluginKey) {
        // 检查参数是否为空
        if (baseResourcePath == null || pluginKey == null) {
            return null;
        }
        
        // 获取基础路径的绝对路径
        Path absoluteBasePath = Paths.get(baseResourcePath).toAbsolutePath().normalize();
        
        // 添加路径在基础路径的基础上加一层名字叫pluginKey的路径后形成路径a
        Path pluginPath = absoluteBasePath.resolve(pluginKey);
        
        // 在路径a下寻找targetDirectoryName目录，并返回其目录的绝对路径
        File baseDir = pluginPath.toFile();
        File targetDir = searchTargetDirectory(baseDir);

        if (targetDir == null) {
            return null;
        }

        // 返回目标目录的绝对路径
        return targetDir.getAbsolutePath();
    }

    /**
     * 递归搜索目标目录
     *
     * @param dir 要搜索的目录
     * @return 找到的目标目录，未找到则返回null
     */
    private File searchTargetDirectory(File dir) {
        // 目录不存在或不是目录，直接返回null
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        // 检查当前目录是否为目标目录（忽略大小写）
        if (targetDirectoryName.equalsIgnoreCase(dir.getName())) {
            return dir;
        }

        // 递归搜索所有子目录
        File[] subDirs = dir.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                File foundDir = searchTargetDirectory(subDir);
                if (foundDir != null) {
                    return foundDir;
                }
            }
        }

        return null;
    }
}