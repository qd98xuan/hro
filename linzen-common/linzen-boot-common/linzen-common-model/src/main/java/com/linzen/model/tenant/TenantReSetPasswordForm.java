package com.linzen.model.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 租户重置密码类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TenantReSetPasswordForm {

    @NotNull(message = "主键不能为空")
    @Schema(description = "主键")
    private String id;

    @NotNull(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String userPassword;

    @NotNull(message = "确认新密码不能为空")
    @Schema(description = "确认新密码")
    private String validatePassword;

    @Schema(description = "tenantId")
    private String tenantId;
}
