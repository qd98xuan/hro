package com.linzen.model.login.vo;


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
public class UserSystemVO {

    @Schema(description = "系统id")
    private String id;

    @Schema(description = "系统名称")
    private String name;

    @Schema(description = "系统图标")
    private String icon;

    @Schema(description = "是否是当前系统")
    private boolean currentSystem;

    @Schema(description = "系统说明")
    private String description;

    @Schema(description = "系统左上角的文字图片")
    private String navigationIcon;

    @Schema(description = "系统图片")
    private String workLogoIcon;
}
