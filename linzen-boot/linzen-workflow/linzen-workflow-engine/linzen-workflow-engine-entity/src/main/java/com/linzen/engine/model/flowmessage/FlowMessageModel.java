package com.linzen.engine.model.flowmessage;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskOperatorRecordEntity;
import com.linzen.engine.enums.FlowMessageEnum;
import com.linzen.engine.model.flowengine.shuntjson.childnode.MsgConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowMessageModel {
    private String title = "";
    private Integer type = FlowMessageEnum.wait.getCode();
    private Integer status;
    private MsgConfig msgConfig = new MsgConfig();
    private List<String> userList = new ArrayList<>();
    private Map<String, Object> data = new HashMap<>();
    private Map<String, String> contMsg = new HashMap<>();
    private String fullName;
    private FlowTaskOperatorRecordEntity recordEntity;
    private UserInfo userInfo;
}
