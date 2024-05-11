package com.linzen.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 授权用户展示
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class InterfaceUserVo {

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户密钥")
    private String userKey;
}
