package com.linzen.message.util;

import lombok.Data;

import javax.websocket.Session;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnlineUserModel {
    /**
     *  连接Id
     */
    private String connectionId;
    /**
     *  用户Id
     */
    private String userId;
    /**
     *  租户Id
     */
    private String tenantId;

    public String getTenantId() {
        return tenantId = tenantId == null ? "" : tenantId;
    }

    /**
     *  移动端
     */
    private Boolean isMobileDevice;
    /**
     * token
     */
    private String token;
    /**
     * session
     */
    private Session webSocket;
    /**
     * session
     */
    private String systemId;
}
