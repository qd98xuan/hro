package com.linzen.base.model.systemconfig;

import lombok.Data;

/**
 * 发送短信配置模型
 *
 * @version V0.0.1
 * @copyright 领致信息
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
