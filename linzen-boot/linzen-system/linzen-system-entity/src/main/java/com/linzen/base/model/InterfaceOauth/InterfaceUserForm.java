package com.linzen.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 授权用户表单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class InterfaceUserForm {

    @Schema(description = "接口认证id")
    private String interfaceIdentId;

    @Schema(description = "授权用户列表")
    private List<String> userIds;
}
