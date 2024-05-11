package com.linzen.portal.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class PortalInfoVO extends PortalCrForm {

    private String id;

    @Schema(description = "pc发布标识")
    Integer pcIsRelease;
    @Schema(description = "app发布标识")
    Integer appIsRelease;

    @Schema(description = "pc是否发布门户" )
    private Integer pcPortalIsRelease;
    @Schema(description = "app是否发布门户" )
    private Integer appPortalIsRelease;

}
