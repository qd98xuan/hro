package cn.xuyanwu.spring.file.storage.platform;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageProperties;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.linzen.model.FileListVO;
import com.linzen.util.context.SpringContext;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 文件存储接口，对应各个平台
 */
public interface FileStorage extends AutoCloseable {

    /**
     * 获取平台
     */
    String getPlatform();

    /**
     * 获取地址
     */
    String getBasePath();

    /**
     * 获取地址
     */
    String getDomain();

    /**
     * 获取命名空间
     */
    default String getBucketName(){
        return "";
    }

    /**
     * 设置平台
     */
    void setPlatform(String platform);

    /**
     * 保存文件
     */
    boolean save(FileInfo fileInfo,UploadPretreatment pre);

    /**
     * 删除文件
     */
    boolean delete(FileInfo fileInfo);

    /**
     * 文件是否存在
     */
    boolean exists(FileInfo fileInfo);

    /**
     * 下载文件
     */
    void download(FileInfo fileInfo,Consumer<InputStream> consumer);

    /**
     * 下载缩略图文件
     */
    void downloadTh(FileInfo fileInfo,Consumer<InputStream> consumer);

    /**
     * 释放相关资源
     */
    void close();

    /**
     * 获取本地储存路径
     */
    default String getLocalPath() {
        FileStorageProperties fileStorageProperties = SpringContext.getBean(FileStorageProperties.class);
        if (fileStorageProperties.getLocalPlus().size() < 1) {
            return null;
        }
        String storagePath = fileStorageProperties.getLocalPlus().get(0).getBasePath();
        return storagePath;
    }

    /**
     * 获取文件列表
     */
    default List getFileList(String folderName) {
        return Collections.EMPTY_LIST;
    }

    /**
     * 返回值统一泛型
     */
    default List<FileListVO> conversionList(String folderName) {
        return Collections.EMPTY_LIST;
    }

    /**
     * 下载到本地
     *
     * @param folderName  文件夹名
     * @param filePath   下载到本地文件路径
     * @param objectName 文件名
     */
    default void downLocal(String folderName, String filePath, String objectName) {}

}
