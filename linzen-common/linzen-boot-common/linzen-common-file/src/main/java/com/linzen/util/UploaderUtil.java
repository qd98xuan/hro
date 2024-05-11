package com.linzen.util;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class UploaderUtil {

    /**
     * 头像名称处理
     * @param fileName
     * @return
     */
    public static String uploaderImg(String fileName) {
        return uploaderImg(null, fileName);
    }

    /**
     * 头像名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderImg(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Image/userAvatar/";
        }
        return url + fileName;
    }

    /**
     * 附件名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderFile(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Download?encryption=";
        }
        String ticket = TicketUtil.createTicket("", 60);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }

    /**
     * 附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderFile(String fileName) {
        return uploaderFile(null, fileName);
    }

    /**
     * 代码生成器附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderVisualFile(String fileName) {
        String url = "/api/visualdev/Generater/DownloadVisCode?encryption=";
        String ticket = TicketUtil.createTicket("", 60);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }

}
