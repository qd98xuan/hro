package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.entity.FileEntity;
import com.linzen.mapper.FileMapper;
import com.linzen.model.YozoFileParams;
import com.linzen.service.YozoService;
import com.linzen.utils.SplicingUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
public class YozoServiceImpl extends SuperServiceImpl<FileMapper, FileEntity> implements YozoService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public String getPreviewUrl(YozoFileParams params) {
        String previewUrl = SplicingUrlUtil.getPreviewUrl(params);
        return previewUrl;
    }

    @Override
    public ServiceResult saveFileId(String fileVersionId, String fileId, String fileName) {
        FileEntity fileEntity =new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setFileName(fileName);
        fileEntity.setFileVersionId(fileVersionId);
        fileEntity.setType("create");
        this.save(fileEntity);

        return ServiceResult.success("新建文档成功");
    }

    @Override
    public FileEntity selectByName(String fileNa) {
        QueryWrapper<FileEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(FileEntity::getFileName,fileNa);
        return this.getOne(wrapper);
    }

    @Override
    public ServiceResult saveFileIdByHttp(String fileVersionId, String fileId, String fileUrl) {
        String fileName = "";
        String url = "";
        String name = "";
        try {
            url = URLDecoder.decode(fileUrl, "UTF-8");
            if (url.contains("/")) {
                fileName = url.substring(url.lastIndexOf("/") + 1);
            } else {
                fileName = url.substring(url.lastIndexOf("\\") + 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //同一url文件数
        QueryWrapper<FileEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("F_Url", url);
        Long total = fileMapper.selectCount(wrapper);
        if (total == 0) {
            name = fileName;
        } else {
            String t = total.toString();
            name = fileName + "(" + t + ")";
        }
        FileEntity fileEntity = new FileEntity();
        fileEntity.setType(url.contains("http") ? "http" : "local");
        fileEntity.setFileVersionId(fileVersionId);
        fileEntity.setId(fileId);
        fileEntity.setFileName(name);
        fileEntity.setUrl(url);
        fileMapper.insert(fileEntity);
        return ServiceResult.success("新建文档成功");
    }

    @Override
    public ServiceResult deleteFileByVersionId(String versionId) {
        QueryWrapper<FileEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("F_FileVersion", versionId);
        int i = fileMapper.delete(wrapper);
        if (i == 1) {
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error("删除失败");
    }

    @Override
    public FileEntity selectByVersionId(String fileVersionId) {
        QueryWrapper<FileEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("F_FileVersion", fileVersionId);
        FileEntity fileEntity = fileMapper.selectOne(wrapper);
        return fileEntity;
    }

    @Override
    public ServiceResult deleteBatch(String[] versions) {
        for (String version : versions) {
            QueryWrapper<FileEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("F_FileVersion", version);
            int i = fileMapper.delete(wrapper);
            if (i == 0) {
                return ServiceResult.error("删除文件:" + version + "失败");
            }
        }
        return ServiceResult.success(MsgCode.SU003.get());

    }

    @Override
    public void editFileVersion(String oldFileId, String newFileId) {
        UpdateWrapper<FileEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("F_FileVersion", oldFileId);
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileVersionId(newFileId);
        fileEntity.setOldFileVersionId(oldFileId);
        fileMapper.update(fileEntity, wrapper);
    }

    @Override
    public List<FileEntity> getAllList(PaginationVO pageModel) {
        Page page = new Page(pageModel.getCurrentPage(), pageModel.getPageSize());
        IPage<FileEntity> iPage = fileMapper.selectPage(page, null);
        return iPage.getRecords();
    }

}
