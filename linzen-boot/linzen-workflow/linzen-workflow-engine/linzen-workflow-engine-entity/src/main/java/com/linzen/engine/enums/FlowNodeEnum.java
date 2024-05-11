package com.linzen.engine.enums;


/**
 * 流程节点状态
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum FlowNodeEnum {
    //进行节点
    Process(0, "进行节点"),
    //加签人
    FreeApprover(1, "加签节点"),
    //无用节点
    Futility(-1, "无用节点");

    private Integer code;
    private String message;

    FlowNodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
