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
public class UserModifyPasswordForm {

    @NotBlank(message = "必填")
    @Schema(description = "旧密码,需要 MD5 加密后传输")
    private String oldPassword;

    @NotBlank(message = "必填")
    @Schema(description = "新密码")
    private String password;

    @NotBlank(message = "必填")
    @Schema(description = "验证码")
    private String code;

    @NotBlank(message = "必填")
    @Schema(description = "验证码标识")
    private String timestamp;
}
