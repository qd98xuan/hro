package com.linzen.engine.model.flowdelegate;

import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.model.flowbefore.FlowTemplateAllModel;
import com.linzen.engine.util.FlowNature;
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
public class FlowDelegateModel {
    //true 委托 false 审批
    private Boolean delegate = true;
    //0.发起 1.审批 2.结束
    private Integer type = FlowNature.StartMsg;
    private List<String> toUserIds = new ArrayList<>();
    private UserInfo userInfo = new UserInfo();
    private FlowTaskEntity flowTask = new FlowTaskEntity();
    private FlowTemplateAllModel templateAllModel = new FlowTemplateAllModel();
    //审批是否要发送消息
    private Boolean approve = true;
}
