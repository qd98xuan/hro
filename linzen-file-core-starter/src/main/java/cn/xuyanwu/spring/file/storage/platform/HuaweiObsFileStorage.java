package cn.xuyanwu.spring.file.storage.platform;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import com.obs.services.ObsClient;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.linzen.model.FileListVO;
import com.linzen.model.FileModel;
import com.linzen.util.DateUtil;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 华为云 OBS 存储
 */
@Getter
@Setter
public class HuaweiObsFileStorage implements FileStorage {

    /* 存储平台 */
    private String platform;
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;
    private String domain;
    private String basePath;
    private ObsClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public ObsClient getClient() {
        if (client == null) {
            client = new ObsClient(accessKey,secretKey,endPoint);
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    public void close() {
        IoUtil.close(client);
    }

    @Override
    public boolean save(FileInfo fileInfo,UploadPretreatment pre) {
        String newFileKey = basePath + pre.getPath() + fileInfo.getFilename();
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(domain + newFileKey);

        ObsClient client = getClient();
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
                thMetadata.setContentLength((long) thumbnailBytes.length);
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
        ObsClient client = getClient();
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
        ObsObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
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
        ObsObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("缩略图文件下载失败！fileInfo：" + fileInfo,e);
        }
    }

    @Override
    public void downLocal(String folderName, String filePath, String objectName) {
        try {
            ObsObject obsObject = getClient().getObject(bucketName,this.getBasePath() + folderName + objectName + "/");
            @Cleanup InputStream stream = obsObject.getObjectContent();
            FileUtil.write(stream, filePath, objectName);
        } catch (Exception e) {
            throw new FileStorageRuntimeException("文件获取失败！platform：" + platform, e);
        }
    }

    @Override
    public List getFileList(String folderName) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        listObjectsRequest.setPrefix(this.getBasePath() + folderName);
        ObjectListing objectListing = getClient().listObjects(listObjectsRequest);
        return objectListing.getObjects() != null ? objectListing.getObjects() : Collections.EMPTY_LIST;
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
            ObsObject obsObject = (ObsObject) fileList.get(i);
            String objectName = obsObject.getObjectKey();
            if (StringUtil.isEmpty(objectName)
//                    || objectName.split("/").length <= 1 || objectName.split("/").length > 2
            ) {
                continue;
            }
            fileListVO.setFileName(objectName);
            fileListVO.setFileType(FileUtil.getFileType(objectName));
            fileListVO.setFileSize(FileUtil.getSize(String.valueOf(obsObject.getMetadata().getContentLength())));
            fileListVO.setFileTime(DateUtil.dateFormat(obsObject.getMetadata().getLastModified()));
            listVOS.add(fileListVO);
        }
        return listVOS;
    }

}
