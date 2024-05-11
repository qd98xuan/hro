package com.linzen.engine.model.flowengine;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class FlowOperator {
    private UserInfo userInfo;
    private FlowModel flowModel;
    private FlowTaskEntity flowTask;
    private List<ChildNodeList> nodeList;
    private List<FlowTaskNodeEntity> taskNodeListAll;
    private List<FlowTaskOperatorEntity> operatorListAll;
    private boolean reject = false;
    private Map<String, List<String>> asyncTaskList = new HashMap<>();
    private Map<String, List<String>> nodeTaskIdList = new HashMap<>();
}
