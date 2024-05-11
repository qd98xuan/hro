package com.linzen.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 授权接口列表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class IdentInterfaceListModel {

    @Schema(description = "接口认证id")
    private String interfaceIdentId;

    @Schema(description = "接口id")
    private String dataInterfaceIds;
}
