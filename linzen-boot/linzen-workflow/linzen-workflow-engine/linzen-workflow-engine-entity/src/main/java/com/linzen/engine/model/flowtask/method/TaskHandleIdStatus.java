package com.linzen.engine.model.flowtask.method;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskHandleIdStatus {
    /**
     * 审批类型（0：拒绝，1：同意）
     **/
    private Integer status;
    /**
     * 当前节点属性
     **/
    private ChildNodeList nodeModel;
    /**
     * 用户
     **/
    private UserInfo userInfo;
    /**
     * 审批对象
     **/
    private FlowModel flowModel;
    /**
     * 节点list
     **/
    private List<FlowTaskNodeEntity> taskNodeList;

}
