package com.linzen.engine.model.flowengine.shuntjson.nodejson;

import com.linzen.emnus.SearchMethodEnum;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ProperCond;
import lombok.Data;

import java.util.ArrayList;
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
public class ConditionList {
    /**
     * 条件
     **/
    private List<ProperCond> conditions = new ArrayList<>();
    /**
     * 表达式
     **/
    private String matchLogic = SearchMethodEnum.And.getSymbol();
    /**
     * 条件节点id
     **/
    private String nodeId;
    /**
     * 上一节点id
     **/
    private String prevId;
    /**1.先判断分流节点 2.在判断孩子节点 3.最后获取子节点**/
    /**
     * 判断是否有分流节点
     **/
    private Boolean flow = false;
    /**
     * 判断是否有选择节点
     **/
    private Boolean branchFlow = false;
    /**
     * 判断是否有转转向
     **/
    private Boolean swerve = false;
    /**
     * 分流的节点id
     **/
    private String flowId;
    /**
     * 判断是否有子节点
     **/
    private Boolean child;
    /**
     * 条件成功id
     **/
    private String childNodeId;
    /**
     * 子节点id
     **/
    private String firstId;
    /**
     * 判断是否其他条件
     **/
    private Boolean isDefault;
    /**
     * 名称
     **/
    private String title;
    /**
     * 转向节点
     **/
    private String swerveNode;
}
