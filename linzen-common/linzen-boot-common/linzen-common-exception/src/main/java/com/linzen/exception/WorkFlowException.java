package com.linzen.exception;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class WorkFlowException extends Exception {

    private Integer code = 400;

    public WorkFlowException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public WorkFlowException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }
}
