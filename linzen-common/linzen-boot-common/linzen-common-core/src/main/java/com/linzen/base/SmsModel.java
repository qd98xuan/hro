package com.linzen.base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发送短信配置模型
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SmsModel {
    /**
     * 阿里
     */
    private String aliAccessKey;
    private String aliSecret;

    /**
     * 腾讯
     */
    private String tencentSecretId;
    private String tencentSecretKey;
    private String tencentAppId;
    private String tencentAppKey;

}
