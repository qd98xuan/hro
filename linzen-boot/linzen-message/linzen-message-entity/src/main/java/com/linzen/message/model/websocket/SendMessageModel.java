package com.linzen.message.model.websocket;

import com.linzen.base.UserInfo;
import com.linzen.message.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 发送消息到mq模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageModel implements Serializable {

    private List<String> toUserIds;

    private MessageEntity entity;

    private UserInfo userInfo;

    private Integer messageType;

}
