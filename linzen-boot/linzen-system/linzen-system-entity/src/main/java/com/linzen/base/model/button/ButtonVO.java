package com.linzen.base.model.button;

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
public class ButtonVO {
    @Schema(description = "按钮主键")
    private String id;
    @Schema(description = "按钮上级")
    private String parentId;
    @Schema(description = "按钮名称")
    private String fullName;
    @Schema(description = "按钮编码")
    private String enCode;
    @Schema(description = "按钮图标")
    private String icon;
    @Schema(description = "请求地址")
    private String urlAddress;
    @Schema(description = "功能主键")
    private String moduleId;
}
