package com.linzen.engine.model.flowtask;

import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeoutJobModel {

    private String tenantId;
    private String tenantDbConnectionString;
    private boolean isAssignDataSource;


    private FlowModel flowModel;
    private String taskId;
    private String taskNodeId;
    private String taskNodeOperatorId;
    private FlowTaskOperatorEntity operatorEntity;
    private Integer counter;
    private Integer overtimeNum;
    private boolean isSuspend = false;


}
