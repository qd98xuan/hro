package com.linzen.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserResetPasswordForm {

    @NotBlank(message = "必填")
    @Schema(description = "用户id")
    private String id;

    @NotBlank(message = "必填")
    @Schema(description = "新密码，需要 MD5 加密后传输")
    private String userPassword;

    @NotBlank(message = "必填")
    @Schema(description = "重复新密码")
    private String validatePassword;
}
