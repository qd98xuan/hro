package com.linzen.engine.model.flowtask;

import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowNodeListModel {
    private List<FlowTaskNodeEntity> dataAll = new ArrayList<>();
    private FlowModel flowModel = new FlowModel();
    private Boolean isAdd = false;
    private FlowTaskNodeEntity taskNode = new FlowTaskNodeEntity();
    private Long num = 1L;
}
