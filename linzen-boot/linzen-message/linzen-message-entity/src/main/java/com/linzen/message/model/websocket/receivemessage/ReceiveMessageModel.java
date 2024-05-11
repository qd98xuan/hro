package com.linzen.message.model.websocket.receivemessage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linzen.message.model.websocket.model.MessageModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 接受消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ReceiveMessageModel extends MessageModel implements Serializable {

    private String formUserId;

    private Long dateTime;

    private String headIcon;

    private Long latestDate;

    private String realName;

    private String account;

    private String messageType;

    private Object formMessage;

    @JsonIgnore
    private String userId;

}
