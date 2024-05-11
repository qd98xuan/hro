package com.linzen.util;


import java.util.Arrays;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class OptimizeUtil {

    /**
     * 允许文件类型
     *
     * @param fileType      文件所有类型
     * @param fileExtension 当前文件类型
     * @return
     */
    public static boolean fileType(String fileType, String fileExtension) {
        String[] allowExtension = fileType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许图片类型
     *
     * @param imageType     图片所有类型
     * @param fileExtension 当前图片类型
     * @return
     */
    public static boolean imageType(String imageType, String fileExtension) {
        String[] allowExtension = imageType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许上传大小
     *
     * @param fileSize 文件大小
     * @param maxSize  最大的文件
     * @return
     */
    public static boolean fileSize(Long fileSize, int maxSize) {
        if (fileSize > maxSize) {
            return true;
        }
        return false;
    }

}
