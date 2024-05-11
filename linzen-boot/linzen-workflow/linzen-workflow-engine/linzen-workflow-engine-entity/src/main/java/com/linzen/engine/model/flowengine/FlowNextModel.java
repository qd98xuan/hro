package com.linzen.engine.model.flowengine;

import com.linzen.engine.entity.FlowTaskNodeEntity;
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
public class FlowNextModel {
    private List<FlowTaskNodeEntity> nodeListAll = new ArrayList<>();
    private List<FlowTaskNodeEntity> nextNodeEntity = new ArrayList<>();
    private FlowTaskNodeEntity taskNode;
    private FlowModel flowModel;
    private Boolean isCountersign = false;
}
