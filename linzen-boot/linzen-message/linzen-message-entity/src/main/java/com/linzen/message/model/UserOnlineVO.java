package com.linzen.message.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserOnlineVO {
    private String userId;
    private String userName;
    private String loginTime;
    private String loginIPAddress;
    private String loginSystem;
    @Schema(description = "所属组织")
    private String organize;
    @Schema(description = "浏览器")
    private String loginBrowser;
    @Schema(description = "登录地址")
    private String loginAddress;
}
