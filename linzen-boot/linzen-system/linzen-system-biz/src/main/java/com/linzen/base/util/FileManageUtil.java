package com.linzen.base.util;

import com.linzen.config.ConfigValueUtil;
import com.linzen.model.FileModel;
import com.linzen.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class FileManageUtil {

    @Autowired
    private ConfigValueUtil configValueUtil;

    // 添加附件：将临时文件夹的文件拷贝到正式文件夹里面

    /**
     * 添加附件：将临时文件夹的文件拷贝到正式文件夹里面
     * @param data list集合
     */
    public void createFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String temporaryFilePath = configValueUtil.getTemporaryFilePath();
            String systemFilePath = configValueUtil.getSystemFilePath();
            for (FileModel item : data) {
                FileUtil.copyFile(temporaryFilePath + item.getFileId(), systemFilePath + item.getFileId());
            }
        }
    }

    /**
     * 更新附件
     * @param data list集合
     */
    public void updateFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String temporaryFilePath = configValueUtil.getTemporaryFilePath();
            String systemFilePath = configValueUtil.getSystemFilePath();
            for (FileModel item : data) {
                if ("add".equals(item.getFileType())) {
                    FileUtil.copyFile(temporaryFilePath + item.getFileId(), systemFilePath + item.getFileId());
                } else if ("delete".equals(item.getFileType())) {
                    FileUtil.deleteFile(systemFilePath + item.getFileId());
                }
            }
        }
    }

    /**
     * 删除附件
     * @param data list集合
     */
    public void deleteFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String systemFilePath = configValueUtil.getSystemFilePath();
            for (FileModel item : data) {
                FileUtil.deleteFile(systemFilePath + item.getFileId());
            }
        }
    }
}
