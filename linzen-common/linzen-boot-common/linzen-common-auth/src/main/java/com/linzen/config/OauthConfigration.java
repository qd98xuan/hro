package com.linzen.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.linzen.config.OauthConfigration.PREFIX;


/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class OauthConfigration {

    public static final String PREFIX = "oauth";

    /**
     * 服务器域名
     */
    @Value("${config.ApiDomain:}")
    private String linzenDomain;


    /**
     * 开启单点登录, 需额外代码支持
     */
    private Boolean ssoEnabled = false;

    /**
     * 后端登录完整路径路径
     */
    private String loginPath;

    /**
     * 默认发起的登录协议
     */
    private String defaultSSO = "cas";

    /**
     * 轮询Ticket有效期, 秒
     */
    private long ticketTimeout = 60;

    /**
     * pc端服务器域名
     */
    @Value("${config.FrontDomain:}")
    private String linzenFrontDomain;

    /**
     * app端服务器域名
     */
    @Value("${config.AppDomain:}")
    private String linzenAppDomain;

}
