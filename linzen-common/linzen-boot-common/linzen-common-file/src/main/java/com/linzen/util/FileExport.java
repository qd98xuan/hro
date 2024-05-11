package com.linzen.util;

import com.linzen.base.vo.DownloadVO;

/**
 * 导入导出工厂类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FileExport {

    /**
     *  导出
     * @param clazz         要转成Json的类
     * @param filePath      写入位置
     * @param fileName      文件名
     * @param tableName 表明
     * @return
     */
    DownloadVO exportFile(Object clazz, String filePath, String fileName, String tableName);

}
