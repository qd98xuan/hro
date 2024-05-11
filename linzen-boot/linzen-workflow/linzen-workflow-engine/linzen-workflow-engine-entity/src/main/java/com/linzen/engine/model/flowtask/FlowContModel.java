package com.linzen.engine.model.flowtask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowContModel {
    /**
     * 审批类型
     */
    private Integer type;
    /**
     * 编码
     */
    private String enCode;
    /**
     * 引擎id
     */
    private String flowId;
    /**
     * 表单分类
     */
    private Integer formType;
    /**
     * 任务id
     */
    private String processId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 代办id
     */
    private String taskOperatorId;
    /**
     * 节点id
     */
    private String taskNodeId;
}
