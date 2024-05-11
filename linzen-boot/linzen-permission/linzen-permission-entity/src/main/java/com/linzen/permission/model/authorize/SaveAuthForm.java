package com.linzen.permission.model.authorize;

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
public class SaveAuthForm {
    @Schema(description = "权限类型")
    private String itemType;
    @Schema(description = "对象类型")
    private String objectType;
    @Schema(description = "对象主键")
    private String[] objectId;
}
