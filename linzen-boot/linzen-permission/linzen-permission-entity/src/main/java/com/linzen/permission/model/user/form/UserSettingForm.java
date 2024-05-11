package com.linzen.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserSettingForm {

    @Schema(description = "主要类型")
    private String majorType;

    @Schema(description = "主要Id")
    private String majorId;

    @Schema(description = "菜单类型")
    private Integer menuType;

}
