package com.linzen.message.model.websocket.onclose;

import com.linzen.message.model.websocket.model.MessageModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 关闭连接model
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class OnCloseModel extends MessageModel implements Serializable {

    private String userId;

    public OnCloseModel(String userId, String method) {
        this.userId = userId;
        super.method = method;
    }
}
