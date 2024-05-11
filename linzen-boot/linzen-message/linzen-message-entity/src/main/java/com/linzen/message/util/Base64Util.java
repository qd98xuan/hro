package com.linzen.message.util;

import com.linzen.config.ConfigValueUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.context.SpringContext;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class Base64Util {

    private static ConfigValueUtil configValueUtil = SpringContext.getBean(ConfigValueUtil.class);


    /**
     * 把文件转化为base64.
     *
     * @param filePath 源文件路径
     */
    public static String fileToBase64(String filePath) {
        if (!StringUtil.isEmpty(filePath)) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                return Base64.encodeBase64String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
