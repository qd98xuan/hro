package com.linzen.engine.model.flowengine;

import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class FlowDataModel {
    private ChildNodeList childNodeList;
    private List<FlowTaskNodeEntity> taskNodeList;
    private FlowModel flowModel;
    private Boolean isAssig = true;
    private Boolean isData = true;

}
