package com.linzen.message.model;

import lombok.Data;

/**
 * 未读消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImUnreadNumModel {
    /**
    *  发送者Id
    */
    private String sendUserId;

    /**
    * 租户id
    */
    private String tenantId;

    /**
    *  未读数量
    */
    private int unreadNum;

    /**
    *  默认消息
    */
    private String defaultMessage;

    /**
    *  默认消息类型
    */
    private String defaultMessageType;

    /**
    *  默认消息时间
    */
    private String defaultMessageTime;
}
