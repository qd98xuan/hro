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
public class AuthorizeDataUpForm {
    @Schema(description = "对象类型")
    private String objectType;

    @Schema(description = "按钮id")
    private String[] button;
    @Schema(description = "列表id")
    private String[] column;
    @Schema(description = "菜单id")
    private String[] module;
    @Schema(description = "数据权限方案id")
    private String[] resource;
    @Schema(description = "表单id")
    private String[] form;


    @Schema(description = "系统id")
    private String[] systemIds;

}
