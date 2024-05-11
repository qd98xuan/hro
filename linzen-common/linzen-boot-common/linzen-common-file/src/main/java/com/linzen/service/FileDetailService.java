package com.linzen.service;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.recorder.FileRecorder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linzen.entity.FileDetail;
import com.linzen.mapper.FileDetailMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用来将文件上传记录保存到数据库，这里使用了 MyBatis-Plus 和 Hutool 工具类
 */
@Service
public class FileDetailService extends ServiceImpl<FileDetailMapper, FileDetail> implements FileRecorder {


    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean record(FileInfo info) {
        return true;
    }

    /**
     * 根据 url 查询文件信息
     */
    @SneakyThrows
    @Override
    public FileInfo getByUrl(String url) {
        return null;
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        return true;
    }

    /**
     * 通过路径获取文件列表
     *
     * @param path
     * @return
     */
    public List<FileDetail> getFileList(String path) {
        return null;
    }

    /**
     * 获取文件信息
     *
     * @param path
     * @param fileName
     * @return
     */
    public FileDetail getFileDetail(String path, String fileName, String platform) {
        return null;
    }
}


