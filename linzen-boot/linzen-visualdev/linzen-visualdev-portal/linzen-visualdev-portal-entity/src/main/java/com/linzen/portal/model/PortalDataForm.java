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
public class PortalDataForm {

    @Schema(description = "门户id")
    private String portalId;

    @Schema(description = "PC:网页端 APP:手机端 ")
    private String platform;

    @Schema(description = "PC:网页端 APP:手机端 ")
    private String formData;

}
