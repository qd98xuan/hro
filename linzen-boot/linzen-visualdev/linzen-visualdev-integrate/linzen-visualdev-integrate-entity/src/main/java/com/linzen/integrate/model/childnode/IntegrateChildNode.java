package com.linzen.integrate.model.childnode;

import lombok.Data;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateChildNode {
    private String type;
    private String content;
    private String nodeId;
    private String prevId;
    private IntegrateChildNode childNode;
    private IntegrateProperties properties = new IntegrateProperties();
}
