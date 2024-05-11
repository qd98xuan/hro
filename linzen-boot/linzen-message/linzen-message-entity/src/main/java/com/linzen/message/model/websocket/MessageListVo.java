package com.linzen.message.model.websocket;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息列表单个模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class MessageListVo implements Serializable {

    private String content;

    private String contentType;

    private String id;

    private Long receiveTime;

    private String receiveUserId;

    private Long sendTime;

    private String sendUserId;

    private Integer state;

}
