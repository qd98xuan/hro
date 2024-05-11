package com.linzen.message.model;

import lombok.Data;

import java.util.Date;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ImReplySavaModel {

    private String userId;

    private String receiveUserId;

    private Date receiveTime;

    public ImReplySavaModel(String userId, String receiveUserId, Date receiveTime) {
        this.userId = userId;
        this.receiveUserId = receiveUserId;
        this.receiveTime = receiveTime;
    }
}
