package com.linzen.portal.model;


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
@Schema(description="门户修改表单")
public class PortalUpForm extends PortalCrForm {

    @Schema(description = "门户id")
    private String id;

    @Schema(description = "PC:网页端 APP:手机端 ")
    String platform;

    @Schema(description = "PC:网页端 APP:手机端 ")
    Integer state;


}
