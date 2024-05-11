package com.linzen.message.model.sendmessageconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * 
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Data
public class SendConfigTemplateModel  {

    private String id;

    /** 消息发送配置id **/
    @JsonProperty("sendConfigId")
    private String sendConfigId;

    /** 消息类型 **/
    @JsonProperty("messageType")
    private String messageType;

    /** 消息模板id **/
    @JsonProperty("templateId")
    private String templateId;

    /** 账号配置id **/
    @JsonProperty("accountConfigId")
    private String accountConfigId;

    /** 接收人 **/
    private List<String> toUser;

    /** 模板参数 **/
    private Object paramJson;

    /** 消息模板名称 **/
    private String msgTemplateName;


}
