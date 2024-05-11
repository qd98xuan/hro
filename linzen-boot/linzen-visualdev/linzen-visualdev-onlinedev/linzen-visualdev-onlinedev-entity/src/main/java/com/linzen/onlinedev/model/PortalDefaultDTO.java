package com.linzen.onlinedev.model;


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
public class PortalDefaultDTO {

    @Schema(description = "默认门户ID")
    private String defaultPortalId;

    @Schema(description = "系统ID")
    private String systemId;

    public PortalDefaultDTO(){

    }

}
