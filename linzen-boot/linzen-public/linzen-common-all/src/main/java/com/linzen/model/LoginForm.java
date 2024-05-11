package com.linzen.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class LoginForm {

    /**
     * 系统账号
     */
    @Schema(description = "账号")
    private String account;

    /**
     * 系统密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 登录类型
     */
    @Schema(description = "登录类型")
    private String grantType;

    /**
     * 验证码标识
     */
    @Schema(description = "验证码标识")
    private String timestamp;

    /**
     * 来源类型
     */
    @Schema(description = "来源类型")
    private String origin;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;

    public LoginForm() {
    }

    public LoginForm(String account, String password) {
        this.account = account;
        this.password = password;
    }
}
