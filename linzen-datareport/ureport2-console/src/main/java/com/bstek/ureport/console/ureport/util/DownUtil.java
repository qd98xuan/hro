package com.bstek.ureport.console.ureport.util;

import com.linzen.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 下载工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class DownUtil {

    /**
     * 下载文件
     * @param str
     * @param fileName
     */
    public static Boolean downloadFile(String str, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        OutputStream os = null;
        try {
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO8859-1"));
            byte[] bytes = str.getBytes("utf-8");
            os = response.getOutputStream();
            // 将字节流传入到响应流里,响应到浏览器
            os.write(bytes);
            os.close();
            return true;
        } catch (Exception ex) {
            log.error("导出失败:", ex);
            throw new RuntimeException("导出失败");
        }finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ioEx) {
                log.error("导出失败:", ioEx);
            }
        }
    }

}
