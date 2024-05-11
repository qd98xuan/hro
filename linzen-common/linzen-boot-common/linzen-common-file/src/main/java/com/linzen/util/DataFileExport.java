package com.linzen.util;

import cn.xuyanwu.spring.file.storage.FileInfo;
import com.linzen.base.vo.DownloadVO;
import com.linzen.constant.DbSensitiveConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 数据接口文件导入导出
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@Slf4j
public class DataFileExport implements FileExport {

    @Override
    public DownloadVO exportFile(Object clazz, String filePath, String fileName, String tableName) {
        fileName = containsSensitive(fileName);
        //model拼凑成Json字符串
        String json = JsonUtil.createObjectToString(clazz);
        if (json == null) {
            json = "";
        }
        //写入到文件中
        /** 2.写入到文件中 */
        fileName += "_" + DateUtil.dateFormatByPattern(new Date(), "yyyyMMddHHmmss") + "." + tableName;
        //是否需要上产到存储空间
        FileInfo fileInfo = FileUploadUtils.uploadFile(json.getBytes(StandardCharsets.UTF_8), filePath, fileName);
        //生成下载下载文件路径
        DownloadVO vo = DownloadVO.builder().name(fileInfo.getFilename()).url(UploaderUtil.uploaderFile(fileInfo.getFilename() + "#" + "export") + "&name=" + fileName).build();
        return vo;
    }

    /**
     * 替换敏感字
     *
     * @param fileName
     * @return
     */
    private String containsSensitive(String fileName) {
        if (StringUtil.isNotEmpty(fileName)) {
            String[] split = DbSensitiveConstant.FILE_SENSITIVE.split(",");
            for (String str : split) {
                fileName = fileName.replaceAll(str, "");
            }
        }
        return fileName;
    }

}
