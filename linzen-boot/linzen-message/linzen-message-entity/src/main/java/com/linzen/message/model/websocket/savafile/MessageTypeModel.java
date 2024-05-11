package com.linzen.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片和语音共有属性
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class MessageTypeModel implements Serializable {

    protected String path;

}
