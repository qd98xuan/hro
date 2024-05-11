package com.linzen.message.model.sendmessageconfig;

import lombok.Data;

/**
 *
 * 
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendConfigTestResultModel {


    /** 消息类型 **/
    private String messageType;

    /** 是否成功 **/
    private String isSuccess;

    /** 失败原因 **/
    private String result;


}
