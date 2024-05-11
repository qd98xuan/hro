package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 冻结审批
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@TableName("flow_reject_data")
public class FlowRejectDataEntity extends SuperExtendEntity<String> {

    /**
     * 经办数据
     */
    @TableField("F_TASK_OPERATOR_JSON")
    public String taskOperatorJson;
    /**
     * 节点数据
     */
    @TableField("F_TASK_NODE_JSON")
    private String taskNodeJson;
    /**
     * 流程任务
     */
    @TableField("F_TASK_JSON")
    private String taskJson;


}
