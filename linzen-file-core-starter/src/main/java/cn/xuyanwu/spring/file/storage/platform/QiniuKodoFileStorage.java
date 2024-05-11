package cn.xuyanwu.spring.file.storage.platform;

import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.linzen.model.FileListVO;
import com.linzen.model.FileModel;
import com.linzen.util.DateUtil;
import com.linzen.util.FileUtil;
import com.linzen.util.JsonUtil;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 七牛云 Kodo 存储
 */
@Getter
@Setter
public class QiniuKodoFileStorage implements FileStorage {

    /* 存储平台 */
    private String platform;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String domain;
    private String basePath;
    private Region region;
    private QiniuKodoClient client;

    /**
     * 单例模式运行，不需要每次使用完再销毁了
     */
    public QiniuKodoClient getClient() {
        if (client == null) {
            client = new QiniuKodoClient(accessKey,secretKey);
        }
        return client;
    }

    /**
     * 仅在移除这个存储平台时调用
     */
    @Override
    public void close() {
        client = null;
    }

    @Override
    public boolean save(FileInfo fileInfo,UploadPretreatment pre) {
        String newFileKey = basePath + fileInfo.getPath() + fileInfo.getFilename();
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(domain + bucketName + "/"  + newFileKey);

        try (InputStream in = pre.getFileWrapper().getInputStream()) {
            QiniuKodoClient client = getClient();
            UploadManager uploadManager = client.getUploadManager();
            String token = client.getAuth().uploadToken(bucketName);
            uploadManager.put(in,newFileKey,token,null,fileInfo.getContentType());

            byte[] thumbnailBytes = pre.getThumbnailBytes();
            if (thumbnailBytes != null) { //上传缩略图
                String newThFileKey = basePath + fileInfo.getPath() + fileInfo.getThFilename();
                fileInfo.setThUrl(domain + newThFileKey);
                uploadManager.put(new ByteArrayInputStream(thumbnailBytes),newThFileKey,token,null,fileInfo.getThContentType());
            }

            return true;
        } catch (IOException e) {
            try {
                client.getBucketManager().delete(bucketName,newFileKey);
            } catch (QiniuException ignored) {
            }
            throw new FileStorageRuntimeException("文件上传失败！platform：" + platform + "，filename：" + fileInfo.getOriginalFilename(),e);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        BucketManager manager = getClient().getBucketManager();
        try {
            if (fileInfo.getThFilename() != null) {   //删除缩略图
                delete(manager,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
            }
            delete(manager,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        } catch (QiniuException e) {
            throw new FileStorageRuntimeException("删除文件失败！" + e.code() + "，" + e.response.toString(),e);
        }
        return true;
    }

    public void delete(BucketManager manager,String filename) throws QiniuException {
        try {
            manager.delete(bucketName,filename);
        } catch (QiniuException e) {
            if (!(e.response != null && e.response.statusCode == 612)) {
                throw e;
            }
        }
    }


    @Override
    public boolean exists(FileInfo fileInfo) {
        BucketManager manager = getClient().getBucketManager();
        try {
            com.qiniu.storage.model.FileInfo stat = manager.stat(bucketName,fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
            if (stat != null && stat.md5 != null) return true;
        } catch (QiniuException e) {
            throw new FileStorageRuntimeException("查询文件是否存在失败！" + e.code() + "，" + e.response.toString(),e);
        }
        return false;
    }

    @Override
    public void download(FileInfo fileInfo,Consumer<InputStream> consumer) {
        String url = getClient().getAuth().privateDownloadUrl(this.getDomain() + this.getBasePath() +
                this.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        try (InputStream in = new URL(url).openStream()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("文件下载失败！platform：" + fileInfo,e);
        }
    }

    @Override
    public void downloadTh(FileInfo fileInfo,Consumer<InputStream> consumer) {
        if (StrUtil.isBlank(fileInfo.getThUrl())) {
            throw new FileStorageRuntimeException("缩略图文件下载失败，文件不存在！fileInfo：" + fileInfo);
        }
        String url = getClient().getAuth().privateDownloadUrl(this.getDomain() + this.getBasePath() +
                this.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        try (InputStream in = new URL(url).openStream()) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("缩略图文件下载失败！fileInfo：" + fileInfo,e);
        }
    }


    @Getter
    @Setter
    public static class QiniuKodoClient {
        private String accessKey;
        private String secretKey;
        private Auth auth;
        private BucketManager bucketManager;
        private UploadManager uploadManager;

        public QiniuKodoClient(String accessKey,String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }

        public Auth getAuth() {
            if (auth == null) {
                auth = Auth.create(accessKey,secretKey);
            }
            return auth;
        }

        public BucketManager getBucketManager() {
            if (bucketManager == null) {
                bucketManager = new BucketManager(getAuth(),new Configuration(Region.autoRegion()));
            }
            return bucketManager;
        }

        public UploadManager getUploadManager() {
            if (uploadManager == null) {
                uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
            }
            return uploadManager;
        }
    }

    @Override
    public List getFileList(String folderName) {
        //判断存储桶是否存在
        List<com.qiniu.storage.model.FileInfo> list = new ArrayList<>();
        try {
            BucketManager.FileListIterator fileListIterator = getClient().getBucketManager().createFileListIterator(this.getBasePath() + bucketName, folderName, 1000, "");
            while (fileListIterator.hasNext()) {
                //处理获取的file list结果
                com.qiniu.storage.model.FileInfo[] items = fileListIterator.next();
                for (com.qiniu.storage.model.FileInfo item : items) {
                    list.add(item);
                }
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
            // 七牛
            com.qiniu.storage.model.FileInfo fileInfo = (com.qiniu.storage.model.FileInfo) fileList.get(i);
            String objectName = fileInfo.key.replace(this.getBasePath() + folderName + "/", "");
            fileListVO.setFileName(objectName);
            fileListVO.setFileType(FileUtil.getFileType(objectName));
            fileListVO.setFileSize(FileUtil.getSize(String.valueOf(fileInfo.fsize)));
            fileListVO.setFileTime(DateUtil.daFormat(fileInfo.putTime));
            listVOS.add(fileListVO);
        }
        return listVOS;
    }

    @Override
    public void downLocal(String folderName, String filePath, String objectName) {
        try {
            String encodedFileName = URLEncoder.encode(this.getBasePath() + folderName + objectName, "utf-8").replace("+", "%20");
            String finalUrl = String.format("%s/%s", domain, encodedFileName);
            String downloadUrl = getClient().getAuth().privateDownloadUrl(finalUrl);
            @Cleanup InputStream inputStream = new URL(downloadUrl).openStream();
            FileUtil.write(inputStream, filePath, objectName);
        } catch (Exception e) {
            throw new FileStorageRuntimeException("文件下载失败！platform：" + platform + "，下载路径：" + filePath + "，文件夹名称：" + folderName + "，文件名：" + objectName, e);
        }
    }
}
