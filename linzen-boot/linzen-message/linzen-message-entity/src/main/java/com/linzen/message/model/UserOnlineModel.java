package com.linzen.message.model;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserOnlineModel {
    private String userId;
    private String userAccount;
    private String userName;
    private String loginTime;
    private String loginIPAddress;
    private String loginSystem;
    private String tenantId;
    private String token;
    private String device;
    private String organize;
    private String loginBrowser;
    private String loginAddress;
}
