package com.linzen.engine.model.flowengine;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ChildNode;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class FlowUpdateNode {
    private UserInfo userInfo;
    private FlowTaskEntity flowTask;
    private ChildNode childNodeAll;
    private List<ChildNodeList> nodeListAll;
    private List<ConditionList> conditionListAll;
    private List<FlowTaskNodeEntity> taskNodeList;
    private boolean isSubmit = false;
}
