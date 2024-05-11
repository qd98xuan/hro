package com.linzen.portal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@Data
public class PortalReleaseVO {

   @Schema(description = "pc发布标识")
   Integer pcIsRelease;
   @Schema(description = "app发布标识")
   Integer appIsRelease;

}
