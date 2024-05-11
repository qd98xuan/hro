package com.linzen.integrate.model.childnode;

import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateParamModel {
    private String field;
    private String fieldName;
    private Boolean required = false;
    private String relationField;
    private String msgTemplateId;
    private Boolean isSubTable = false;
}
