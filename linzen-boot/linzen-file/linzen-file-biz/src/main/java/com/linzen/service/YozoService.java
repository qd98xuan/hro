package com.linzen.service;

import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperService;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.FileEntity;
import com.linzen.model.YozoFileParams;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public interface YozoService extends SuperService<FileEntity> {
    /**
     * 生成文件预览url
     * @param params
     * @return
     */
    String getPreviewUrl(YozoFileParams params);

    /**
     * 新建文档保存versionId
     * @param fileVersionId
     * @param fileId
     * @param fileName
     * @return
     */
    ServiceResult saveFileId(String fileVersionId, String fileId, String fileName);

    /**
     * 根据文件名查询
     * @param fileNa
     * @return
     */
    FileEntity selectByName(String fileNa);

    /**
     * 上传文件到永中
     * @param fileVersionId
     * @param fileId
     * @param fileUrl
     * @return
     */
    ServiceResult saveFileIdByHttp(String fileVersionId, String fileId, String fileUrl);

    /**
     * 删除文件
     * @param versionId
     * @return
     */
    ServiceResult deleteFileByVersionId(String versionId);

    /**
     * 根据versionId查询文件
     * @param fileVersionId
     * @return
     */
    FileEntity selectByVersionId(String fileVersionId);

    /**
     * 批量删除
     * @param versions
     * @return
     */
    ServiceResult deleteBatch(String[] versions);

    /**
     * 更新versionId
     * @param oldFileId
     * @param newFileId
     */
    void editFileVersion(String oldFileId, String newFileId);

    List<FileEntity> getAllList(PaginationVO pageModel);
}
