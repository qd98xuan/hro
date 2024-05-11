package com.linzen.util.minio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件储存类型常量类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class StorageType {
    /**
     * 本地存储
     */
    public static final String STORAGE = "local";

    /**
     * Minio存储
     */
    public static final String MINIO = "minio";

    /**
     * AliOSS存储
     */
    public static final String ALI_OSS = "aliyun-oss";

    /**
     * 七牛云
     */
    public static final String QINIU = "qiniu-kodo";

    /**
     * 腾讯云
     */
    public static final String TENCENT = "tencent-cos";

}
