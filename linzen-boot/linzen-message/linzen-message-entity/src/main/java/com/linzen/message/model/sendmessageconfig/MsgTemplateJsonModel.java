package com.linzen.message.model.sendmessageconfig;

import lombok.Data;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
public class MsgTemplateJsonModel {

    public String field;
    public String fieldName;
    public String relationField;
    private String id;
    private Boolean isSubTable = false;
    private String msgTemplateId;

}
