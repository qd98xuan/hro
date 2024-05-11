package com.linzen.message.model.websocket.onconnettion;

import com.linzen.message.model.websocket.model.MessageModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户在线推送模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnLineModel extends MessageModel implements Serializable {

    /**
     * 在线用户
     */
    private String userId;

    public OnLineModel(String method, String userId) {
        super.method = method;
        this.userId = userId;
    }
}
