package com.linzen.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linzen.base.entity.SuperBaseEntity;
import lombok.Data;

/**
 * 流程事件日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@TableName("flow_event_log")
public class FlowEventLogEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * 节点主键
     */
    @TableField("F_TASK_NODE_ID")
    private String taskNodeId;

    /**
     * 接口主键
     */
    @TableField("F_INTERFACE_ID")
    private String interfaceId;

    /**
     * 事件名称
     */
    @TableField("F_FULL_NAME")
    private String fullName;

    /**
     * 执行结果
     */
    @TableField("F_RESULT")
    private String result;

}
