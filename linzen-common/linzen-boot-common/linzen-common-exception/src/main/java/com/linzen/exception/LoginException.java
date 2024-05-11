package com.linzen.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录异常
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class LoginException extends RuntimeException {

    @Getter
    @Setter
    private Object data;
    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Object data) {
        super(message);
        this.data = data;
    }

}
