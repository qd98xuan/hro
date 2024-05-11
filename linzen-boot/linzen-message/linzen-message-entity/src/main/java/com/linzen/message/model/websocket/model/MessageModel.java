package com.linzen.message.model.websocket.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class MessageModel implements Serializable {

    protected String method;

}
