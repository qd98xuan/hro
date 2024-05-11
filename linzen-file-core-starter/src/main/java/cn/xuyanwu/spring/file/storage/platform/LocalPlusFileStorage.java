package cn.xuyanwu.spring.file.storage.platform;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import com.linzen.model.FileListVO;
import com.linzen.model.FileModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.XSSEscape;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 本地文件存储升级版
 */
@Getter
@Setter
public class LocalPlusFileStorage implements FileStorage {

    /* 基础路径 */
    private String basePath;
    /* 本地存储路径*/
    private String storagePath;
    /* 存储平台 */
    private String platform;
    /* 访问域名 */
    private String domain;

    @Override
    public void close() {
    }

    /**
     * 获取本地绝对路径
     */
    public String getAbsolutePath(String path) {
        return storagePath + path;
    }

    @Override
    public boolean save(FileInfo fileInfo,UploadPretreatment pre) {

        String newFileKey = basePath + fileInfo.getPath() + fileInfo.getFilename();
        fileInfo.setBasePath(basePath);
        fileInfo.setUrl(domain + newFileKey);

        try {
            File newFile = FileUtil.touch(getAbsolutePath(newFileKey));
            pre.getFileWrapper().transferTo(newFile);

            byte[] thumbnailBytes = pre.getThumbnailBytes();
            if (thumbnailBytes != null) { //上传缩略图
                String newThFileKey = basePath + fileInfo.getPath() + fileInfo.getThFilename();
                fileInfo.setThUrl(domain + newThFileKey);
                FileUtil.writeBytes(thumbnailBytes,getAbsolutePath(newThFileKey));
            }
            return true;
        } catch (IOException e) {
            FileUtil.del(getAbsolutePath(newFileKey));
            throw new FileStorageRuntimeException("文件上传失败！platform：" + platform + "，filename：" + fileInfo.getOriginalFilename(),e);
        }
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        if (fileInfo.getThFilename() != null) {   //删除缩略图
            FileUtil.del(getAbsolutePath(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename()));
        }
        return FileUtil.del(getAbsolutePath(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename()));
    }


    @Override
    public boolean exists(FileInfo fileInfo) {
        return new File(getAbsolutePath(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename())).exists();
    }

    @Override
    public void download(FileInfo fileInfo,Consumer<InputStream> consumer) {
        try (InputStream in = FileUtil.getInputStream(getAbsolutePath(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename()))) {
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
        try (InputStream in = FileUtil.getInputStream(getAbsolutePath(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename()))) {
            consumer.accept(in);
        } catch (IOException e) {
            throw new FileStorageRuntimeException("缩略图文件下载失败！fileInfo：" + fileInfo,e);
        }
    }

    @Override
    public List getFileList(String folderName) {
        List<FileModel> data = new ArrayList<>();
        File filePath = new File(XSSEscape.escapePath(getLocalPath() + folderName));
        List<File> files = com.linzen.util.FileUtil.getFile(filePath);
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                File item = files.get(i);
                FileModel fileModel = new FileModel();
                fileModel.setFileId(i + "");
                fileModel.setFileName(folderName + item.getName());
                fileModel.setFileType(com.linzen.util.FileUtil.getFileType(item));
                fileModel.setFileSize(com.linzen.util.FileUtil.getSize(String.valueOf(item.length())));
                fileModel.setFileTime(com.linzen.util.FileUtil.getCreateTime(filePath + item.getName()));
                data.add(fileModel);
            }
        }
        return data;
    }

    @Override
    public List<FileListVO> conversionList(String folderName) {
        List fileList = getFileList(folderName);
        List<FileListVO> listVOS = new ArrayList<>(fileList.size());
        if (!fileList.isEmpty() && fileList.get(0) instanceof FileModel) {
            return JsonUtil.createJsonToList(fileList, FileListVO.class);
        }
        return new ArrayList<>();
    }
}
