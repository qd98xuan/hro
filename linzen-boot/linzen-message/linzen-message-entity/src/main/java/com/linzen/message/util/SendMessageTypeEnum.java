package com.linzen.message.util;

/**
 * 消息类型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum SendMessageTypeEnum {

    /**
     * 文本消息
     */
    MESSAGE_TEXT("text"),
    /**
     * 语音消息
     */
    MESSAGE_VOICE("voice"),
    /**
     * 图片消息
     */
    MESSAGE_IMAGE("image");

    SendMessageTypeEnum() {
    }

    private String message;

    SendMessageTypeEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
