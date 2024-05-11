package com.linzen.message.model.websocket.savamessage;

import com.alibaba.fastjson2.annotation.JSONField;
import com.linzen.message.model.websocket.model.MessageModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 保存消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class SavaMessageModel extends MessageModel implements Serializable {

    @JSONField(name = "UserId")
    private String userId;

    private String toUserId;

    private Long dateTime;

    private String headIcon;

    private Long latestDate;

    private String realName;

    private String account;

    private String toAccount;

    private String toRealName;

    private String toHeadIcon;

    private String messageType;

    private Object toMessage;

}
