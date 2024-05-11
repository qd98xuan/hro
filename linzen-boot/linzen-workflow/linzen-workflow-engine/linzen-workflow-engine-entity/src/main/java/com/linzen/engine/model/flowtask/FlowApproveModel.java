package com.linzen.engine.model.flowtask;

import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
public class FlowApproveModel {
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
    private FlowTaskEntity flowTask = new FlowTaskEntity();
    private FlowModel flowModel = new FlowModel();
    private boolean isSubmit = false;
}
