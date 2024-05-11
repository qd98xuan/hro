package com.linzen.message.model.messagetemplateconfig;

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
public class TemplateParamModel  {

    /** 模板参数 **/
    private String field;

    /** 参数说明 **/
    private String fieldName;

    /** 参数变量**/
    private String value;

    /** 参数主键 **/
    private String id;

    /** 消息模板类型 **/
    private String templateType;

    /** 消息模板编码 **/
    private String templateCode;

    /** 消息模板id **/
    private String templateId;

    /** 消息模板名称 **/
    private String templateName;

}
