package com.linzen.portal.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发布(同步)表单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
@Schema(description="门户创建表单")
public class PortalReleaseForm extends ReleaseModel {

    @Schema(description = "pc标识")
    private Integer pcPortal;
    @Schema(description = "pc应用集合")
    private String pcPortalSystemId;
    @Schema(description = "app标识")
    private Integer appPortal;
    @Schema(description = "pc应用集合")
    private String appPortalSystemId;

}
