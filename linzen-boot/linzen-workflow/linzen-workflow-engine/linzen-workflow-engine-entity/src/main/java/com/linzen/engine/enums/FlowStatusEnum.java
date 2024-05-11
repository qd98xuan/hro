package com.linzen.engine.enums;

/**
 * 提交状态
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum FlowStatusEnum {
    //不操作
    none("-1"),
    //保存
    save("1"),
    // 提交
    submit("0");

    private String message;

    FlowStatusEnum(String message) {
        this.message = message;
    }

    /**
     * 根据状态code获取枚举名称
     *
     * @return
     */
    public static FlowStatusEnum getByCode(Integer code) {
        for (FlowStatusEnum status : FlowStatusEnum.values()) {
            if (status.getMessage().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
