package com.linzen.engine.model.flowtask;

import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
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
public class FlowCountersignModel {
    private FlowTaskNodeEntity taskNode;
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private Boolean fixed = false;
    private double pass = 100;
    private List<FlowTaskOperatorEntity> passNumList = new ArrayList<>();
}
