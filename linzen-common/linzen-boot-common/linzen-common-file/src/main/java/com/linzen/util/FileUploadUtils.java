package com.linzen.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageProperties;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import com.linzen.entity.FileDetail;
import com.linzen.util.context.SpringContext;
import com.linzen.model.FileListVO;
import com.linzen.service.FileDetailService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUploadUtils {

    private static FileStorageService fileStorageService;
    private static FileDetailService fileDetailService;
    private static FileStorageProperties fileStorageProperties;

    static {
        fileStorageService = SpringContext.getBean(FileStorageService.class);
        fileDetailService = SpringContext.getBean(FileDetailService.class);
        fileStorageProperties = SpringContext.getBean(FileStorageProperties.class);
    }

    /**
     * 获取文件信息
     *
     * @param path
     * @param fileName
     * @param origin
     * @return
     */
    public static FileDetail getFileDetail(String path, String fileName, boolean origin) {
        FileDetail fileDetail = new FileDetail();
        fileDetail.setPath(path);
        fileDetail.setFilename(fileName);
        String basePath;
        String platform;
        FileStorage fileStorage = fileStorageService.getFileStorage();
        if (origin) {
            basePath = fileStorage.getLocalPath();
            platform = "local-plus-1";
        } else {
            basePath = fileStorage.getBasePath();
            platform = getDefaultPlatform();
        }
        fileDetail.setBasePath(basePath);
        fileDetail.setPlatform(platform);
        return fileDetail;
    }

//    /**
//     * 获取文件url
//     *
//     * @param path
//     * @param fileName
//     * @return
//     */
//    public static String getFileUrl(String path, String fileName) {
//        FileDetail fileDetail = getFileDetail(path, fileName, );
//        String url = null;
//        if (fileDetail == null) {
//            url = fileStorageService.getFileStorage().getBasePath() + path + fileName;
//        } else {
//            url = fileDetail.getUrl();
//        }
//        return url;
//    }

    /**
     * 返回本地地址且固定为local-plus-1
     *
     * @return
     */
    public static String getLocalBasePath() {
        return fileStorageService.getFileStorage().getLocalPath();
    }

    /**
     * 获取文件信息
     *
     * @param path
     * @param fileName
     * @param origin
     * @return
     */
    public static FileInfo getFileInfo(String path, String fileName, boolean origin) {
        FileDetail fileDetail = getFileDetail(path, fileName, origin);
        return BeanUtil.copyProperties(fileDetail,FileInfo.class,"attr");
    }

    /**
     * 上传文件，通过字节数组
     *
     * @param bytes    内容
     * @param path     路径
     * @param fileName 文件名
     */
    public static FileInfo uploadFile(byte[] bytes, String path, String fileName) {
        FileInfo fileInfo = fileStorageService.of(bytes)
                .setPath(path)
                .setOriginalFilename(fileName)
                .upload();
        Assert.notNull(fileInfo, "文件上传失败！");
        return fileInfo;
    }

    /**
     * 上传文件，MultipartFile
     *
     * @param multipartFile 文件
     * @param path          路径
     * @param fileName      文件名
     */
    public static FileInfo uploadFile(MultipartFile multipartFile, String path, String fileName) {
        FileInfo fileInfo = null;
        try {
            fileInfo = fileStorageService.of(multipartFile)
                    .setPath(path)
                    .setThumbnailBytes(multipartFile.getBytes())
                    .upload();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notNull(fileInfo, "文件上传失败！");
        return fileInfo;
    }

    /**
     * 上传文件，File
     *
     * @param file 文件
     * @param path          路径
     * @param fileName      文件名
     */
    public static FileInfo uploadFile(File file, String path, String fileName) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(path)
                .upload();
        Assert.notNull(fileInfo, "文件上传失败！");
        return fileInfo;
    }

    /**
     * 获取文件列表
     *
     * @param path          路径
     */
    public static List<FileListVO> getFileList(String path) {
        return fileStorageService.getFileStorage().conversionList(path);
    }

    /**
     * 获取命名空间
     *
     */
    public static String getBucketName() {
        String bucketName = fileStorageService.getFileStorage().getBucketName();
        if (StringUtil.isNotEmpty(bucketName)) {
            return bucketName + "/";
        }
        return bucketName;
    }

    /**
     * 获取命名空间
     *
     */
    public static String getDomain() {
        return fileStorageService.getFileStorage().getDomain();
    }

    /**
     * 删除文件
     *
     * @param path
     * @param fileName
     */
    public static boolean removeFile(String path, String fileName) {
        FileDetail fileDetail = getFileDetail(path, fileName, false);
        return fileStorageService.delete(fileDetail.getUrl());
    }

//    /**
//     * 下载文件
//     *
//     * @param path
//     * @param fileName
//     */
//    public static void downloadFile(String path, String fileName) {
//        String fileUrl = getFileUrl(path, fileName);
//        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(fileUrl);
//        fileStorageService.download(fileInfo).file(fileUrl);
//    }

    /**
     * 下载文件
     *
     * @param path
     * @param fileName
     */
    public static void downloadFile(String path, String fileName) {
        FileInfo fileInfo = getFileInfo(path, fileName, false);
        fileStorageService.download(fileInfo).file(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
    }

    /**
     * 下载文件得到字节数组
     *  @param path
     * @param fileName
     * @param origin
     */
    public static byte[] downloadFileByte(String path, String fileName, boolean origin) {
        FileInfo fileInfo = getFileInfo(path, fileName, origin);
        return fileStorageService.download(fileInfo).bytes();
    }

//    /**
//     * 下载文件得到流
//     *
//     * @param path
//     * @param fileName
//     * @param origin
//     */
//    public static ByteArrayOutputStream downloadFilStream(String path, String fileName, boolean origin) {
//        FileInfo fileInfo = getFileInfo(path, fileName, origin);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        fileStorageService.download(fileInfo).outputStream(out);
//        return out;
//    }

    /**
     * 获取文件信息
     *
     *
     * @param folderName
     * @param id
     * @return
     */
    public static FileListVO getFileDetail(String folderName, String id) {
        List<FileListVO> fileList = getFileList(folderName);
        Integer integer = Integer.valueOf(id);
        if (fileList.size() > integer) {
            return fileList.get(integer);
        }
        return null;
    }

    /**
     * 默认存储平台
     *
     * @param
     * @return
     */
    public static String getDefaultPlatform() {
        return fileStorageProperties.getDefaultPlatform();
    }

    /**
     * 下载到本地
     *
     * @param folderName  文件夹名
     * @param filePath   下载到本地文件路径
     * @param objectName 文件名
     */
    public static void downLocal(String folderName, String filePath, String objectName) {
        fileStorageService.getFileStorage().downLocal(folderName, filePath, objectName);
    }

    /**
     * 下载到本地
     *
     * @param folderName  文件夹名
     */
    public static List<FileListVO> getDefaultFileList(String folderName) {
        return fileStorageService.getFileStorage().conversionList(folderName);
    }

    /**
     * 判断文件是否存在
     *
     * @param type
     * @param fileName
     * @return
     */
    public static boolean exists(String type, String fileName) {
        String typePath = FilePathUtil.getFilePath(type);
        if(fileName.indexOf(",") >= 0) {
            typePath += fileName.substring(0, fileName.lastIndexOf(",")+1).replaceAll(",", "/");
            fileName = fileName.substring(fileName.lastIndexOf(",")+1);
        }
        FileInfo fileInfo = getFileInfo(typePath, fileName, false);
        return fileStorageService.exists(fileInfo);
    }

    /**
     * 根据路径和文件名删除文件
     * @param path
     * @param fileName
     * @return
     */
    public static boolean deleteFileByPathAndFileName(String path, String fileName) {
        FileInfo fileInfo = getFileInfo(path, fileName, false);
        return fileStorageService.delete(fileInfo);
    }

}
