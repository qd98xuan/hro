package com.linzen.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * app常用数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Schema(description = "常用模型")
public class AppFlowListAllVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "图标背景色")
    private String iconBackground;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "是否常用")
    private Boolean isData;
}
