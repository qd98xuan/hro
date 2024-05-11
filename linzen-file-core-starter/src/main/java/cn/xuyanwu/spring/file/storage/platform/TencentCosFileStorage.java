package cn.xuyanwu.spring.file.storage.platform;

import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
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
 * 腾讯云 COS 存储
 */
@Getter
@Setter
public class TencentCosFileStorage implements FileStorage {

    /* 存储平台 */
    private String platform;
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String domain;
    private String basePath;
    private COSClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public COSClient getClient() {
        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            clientConfig.setHttpProtocol(HttpProtocol.https);
            client = new COSClient(cred,clientConfig);
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

        COSClient client = getClient();
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
        COSClient client = getClient();
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
        COSObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
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
        COSObject object = getClient().getObject(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
        try (InputStream in = object.getObjectContent()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("缩略图文件下载失败！fileInfo：" + fileInfo,e);
        }
    }

    @Override
    public List getFileList(String folderName) {
        List<COSObjectSummary> list = new ArrayList<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setPrefix(this.getBasePath() + folderName);
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        do {
            try {
                objectListing = getClient().listObjects(listObjectsRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
//            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                list.add(cosObjectSummary);
            }
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
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
            fileListVO.setFileId(i + "");// 腾讯
            COSObjectSummary cosObjectSummary = (COSObjectSummary) fileList.get(i);
            String objectName = cosObjectSummary.getKey().replace(this.getBasePath() + folderName + "/", "");
            fileListVO.setFileName(objectName);
            fileListVO.setFileType(FileUtil.getFileType(objectName));
            fileListVO.setFileSize(FileUtil.getSize(String.valueOf(cosObjectSummary.getSize())));
            fileListVO.setFileTime(DateUtil.daFormat(cosObjectSummary.getLastModified()));
            listVOS.add(fileListVO);
        }
        return listVOS;
    }

    @Override
    public void downLocal(String folderName, String filePath, String objectName) {
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, this.getBasePath() + folderName + objectName);
            getClient().getObject(getObjectRequest, new File(filePath + objectName));
        } catch (Exception e) {
            throw new FileStorageRuntimeException("文件下载失败！platform：" + platform + "，下载路径：" + filePath + "，文件夹名称：" + folderName + "，文件名：" + objectName, e);
        }
    }
}
