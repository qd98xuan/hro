package com.linzen.permission.model.user.form;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class UserPasswordForm {

    private String oldPassword;

    private String password;

    private String code;
}
