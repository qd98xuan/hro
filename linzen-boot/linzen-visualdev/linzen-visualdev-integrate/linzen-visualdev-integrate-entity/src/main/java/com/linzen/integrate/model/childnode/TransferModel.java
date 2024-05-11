package com.linzen.integrate.model.childnode;

import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class TransferModel {
    private String targetField;
    private String targetFieldLabel;
    private String sourceType;
    private String sourceValue;
    private Boolean required = false;
}
