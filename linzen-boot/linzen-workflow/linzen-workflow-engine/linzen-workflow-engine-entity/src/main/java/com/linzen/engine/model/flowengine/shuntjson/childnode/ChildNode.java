package com.linzen.engine.model.flowengine.shuntjson.childnode;

import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class ChildNode {
    private String type;
    private String content;
    private Properties properties = new Properties();
    private String nodeId;
    private String prevId;
    private ChildNode childNode;
    private String conditionType;
    private List<ChildNode> conditionNodes;
    private Boolean isInterflow;
    private Boolean isBranchFlow;

}
