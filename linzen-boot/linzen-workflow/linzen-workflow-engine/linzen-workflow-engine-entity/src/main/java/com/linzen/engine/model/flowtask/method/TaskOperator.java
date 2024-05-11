package com.linzen.engine.model.flowtask.method;

import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowtask.FlowErrorModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskOperator {
    /**
     * 当前节点数据
     */
    private ChildNodeList childNode;
    /**
     * 引擎实例
     */
    private FlowTaskEntity taskEntity;
    /**
     * 所有节点
     */
    private List<FlowTaskNodeEntity> taskNodeList;
    /**
     * 提交数据
     */
    private FlowModel flowModel;
    /**
     * true记录 false不记录
     */
    private Boolean details = true;
    /**
     * true 验证 false不验证
     */
    private Boolean verify = true;
    /**
     * 是否驳回
     */
    private Boolean reject = false;
    /**
     * 异常节点数据
     */
    private List<FlowErrorModel> errorList = new ArrayList<>();
    /**
     * 异常规则
     */
    private Boolean errorRule = false;
    /**
     * 附加条件
     */
    private Boolean extraRule = false;
    /**
     * 默认审批通过
     */
    private Integer pass = 0;
    /**
     * 无法提交
     */
    private Integer notSubmit = 0;
    /**
     * 上一节点审批人指定处理人
     */
    private Integer node = 0;
}
