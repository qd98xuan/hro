package com.linzen.engine.model.flowtask;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import com.linzen.permission.entity.SysUserEntity;
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
@NoArgsConstructor
@AllArgsConstructor
public class FlowConditionModel {
    private String data;
    private String nodeId;
    private UserInfo userInfo;
    private SysUserEntity userEntity;
    private FlowTaskEntity flowTaskEntity;
    private List<ChildNodeList> childNodeListAll;
    private List<ConditionList> conditionListAll;
}
