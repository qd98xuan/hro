package cn.xuyanwu.spring.file.storage.platform;

import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.linzen.model.FileListVO;
import com.linzen.model.FileModel;
import com.linzen.util.DateUtil;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 阿里云 OSS 存储
 */
@Getter
@Setter
public class AliyunOssFileStorage implements FileStorage {

    /* 存储平台 */
    private String platform;
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;
    private String domain;
    private String basePath;
    private OSS client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public OSS getClient() {
        if (client == null) {
            client = new OSSClientBuilder().build(endPoint,accessKey,secretKey);
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    public void close() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    @Override
    public boolean save(FileInfo fileInfo,UploadPretreatment pre) {
        String newFileKey = basePath + fileInfo.getPath() + fileInfo.getFilename();
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(domain + bucketName + "/"  + newFileKey);

        OSS client = getClient();
        try (InputStream in = pre.getFileWrapper().getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileInfo.getSize());
            metadata.setContentType(fileInfo.getContentType());
            client.putObject(bucketName,newFileKey,in,metadata);

            byte[] thumbnailBytes = pre.getThumbnailBytes();
            if (thumbnailBytes != null) { //上传缩略图
                String newThFileKey = basePath + fileInfo.getPath() + fileInfo.getThFilename();
                fileInfo.setThUrl(domain + newThFileKey);
                ObjectMetadata thMetadata = new ObjectMetadata();
                thMetadata.setContentLength(thumbnailBytes.length);
                thMetadata.setContentType(fileInfo.getThContentType());
                client.putObject(bucketName,newThFileKey,new ByteArrayInputStream(thumbnailBytes),thMetadata);
            }

            return true;
        } catch (IOException e) {
            client.deleteObject(bucketName,newFileKey);
            throw new FileStorageRuntimeException("文件上传失败！platform：" + platform + "，filename：" + fileInfo.getOriginalFilename(),e);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        OSS client = getClient();
        if (fileInfo.getThFilename() != null) {   //删除缩略图
            client.deleteObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
        }
        client.deleteObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        return true;
    }


    @Override
    public boolean exists(FileInfo fileInfo) {
        return getClient().doesObjectExist(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
    }

    @Override
    public void download(FileInfo fileInfo,Consumer<InputStream> consumer) {
        OSSObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("文件下载失败！platform：" + fileInfo,e);
        }

    }

    @Override
    public void downloadTh(FileInfo fileInfo,Consumer<InputStream> consumer) {
        if (StrUtil.isBlank(fileInfo.getThFilename())) {
            throw new FileStorageRuntimeException("缩略图文件下载失败，文件不存在！fileInfo：" + fileInfo);
        }
        OSSObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("缩略图文件下载失败！fileInfo：" + fileInfo,e);
        }
    }

    @Override
    public List getFileList(String folderName) {
        List<OSSObjectSummary> list = new ArrayList<>();
        try {
            ObjectListing objectListing = null;
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix(this.getBasePath() + folderName);
            objectListing = getClient().listObjects(listObjectsRequest);
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary result : sums) {
                list.add(result);
            }
        } catch (Exception e) {
            throw new FileStorageRuntimeException("文件获取失败！platform：" + platform, e);
        }
        return list;
    }

    @Override
    public List<FileListVO> conversionList(String folderName) {
        List fileList = getFileList(folderName);
        List<FileListVO> listVOS = new ArrayList<>(fileList.size());
        if (!fileList.isEmpty() && fileList.get(0) instanceof FileModel) {
            return JsonUtil.createJsonToList(fileList, FileListVO.class);
        }
        for (int i = 0; i < fileList.size(); i++) {
            FileListVO fileListVO = new FileListVO();
            fileListVO.setFileId(i + "");
            // 阿里云
            OSSObjectSummary summary = (OSSObjectSummary) fileList.get(i);
            String objectName = summary.getKey().replace(this.getBasePath() + folderName + "/", "");
            fileListVO.setFileName(objectName);
            fileListVO.setFileType(FileUtil.getFileType(objectName));
            fileListVO.setFileSize(FileUtil.getSize(String.valueOf(summary.getSize())));
            fileListVO.setFileTime(DateUtil.dateFormat(summary.getLastModified()));
            listVOS.add(fileListVO);
        }
        return listVOS;
    }

    @Override
    public void downLocal(String folderName, String filePath, String objectName) {
        //判断存储桶是否存在
        try {
            getClient().getObject(new GetObjectRequest(bucketName, this.getBasePath() + folderName + objectName), new File(filePath + objectName));
        } catch (Exception e) {
            throw new FileStorageRuntimeException("文件下载失败！platform：" + platform + "，下载路径：" + filePath + "，文件夹名称：" + folderName + "，文件名：" + objectName, e);
        }
    }
}
