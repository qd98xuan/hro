package com.linzen.message.util;

/**
 * 获取消息参数
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum MessageParameterEnum {

    /**
     * 接收者ID
     */
    PARAMETER_TOUSERID("toUserId"),
    PARAMETER_MESSAGETYPE("messageType"),
    PARAMETER_MESSAGECONTENT("messageContent"),
    PARAMETER_TOKEN("token"),
    PARAMETER_METHOD("method"),
    PARAMETER_MOBILEDEVICE("mobileDevice"),
    ;

    private String value;

    MessageParameterEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
