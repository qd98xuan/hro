package com.linzen.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 语音消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class VoiceMessageModel extends MessageTypeModel implements Serializable {

    private String length;

    public VoiceMessageModel(String length, String path) {
        this.length = length;
        super.path = path;
    }

}
