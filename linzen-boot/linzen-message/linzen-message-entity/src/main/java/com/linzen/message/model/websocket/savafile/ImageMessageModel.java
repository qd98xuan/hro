package com.linzen.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片消息模型
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImageMessageModel extends MessageTypeModel implements Serializable {

    private String width;

    private String height;

    public ImageMessageModel(String width, String height, String path) {
        this.width = width;
        this.height = height;
        super.path = path;
    }

}
