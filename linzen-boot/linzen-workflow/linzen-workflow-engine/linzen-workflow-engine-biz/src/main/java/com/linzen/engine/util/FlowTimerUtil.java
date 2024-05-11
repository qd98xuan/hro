package com.linzen.engine.util;

import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowengine.shuntjson.childnode.LimitModel;
import com.linzen.engine.model.flowengine.shuntjson.childnode.Properties;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowtime.FlowTimeModel;
import com.linzen.engine.service.FlowTaskNodeService;
import com.linzen.engine.service.FlowTaskOperatorService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.DateUtil;
import com.linzen.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class FlowTimerUtil {

    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;


    /**
     * 限时开始时间
     */
    public FlowTimeModel time(FlowTaskNodeEntity taskNodeEntity, List<FlowTaskNodeEntity> nodeList
            , FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity operatorInfo) throws WorkFlowException {
        FlowTaskNodeEntity startNode = nodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().orElse(null);
        String nodeJson = startNode != null ? startNode.getNodePropertyJson() : "{}";
        ChildNodeList childNode = JsonUtil.createJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
        FlowTimeModel date = new FlowTimeModel();
        date.setChildNode(JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class));//开始节点
        date.setChildNodeEvnet(childNode);//当前节点
        Date(operatorInfo, flowTaskEntity, date);
        return date;
    }


    private void Date(FlowTaskOperatorEntity operatorInfo, FlowTaskEntity flowTaskEntity, FlowTimeModel flowTimeModel) throws WorkFlowException {
        Properties taskProperties = flowTimeModel.getChildNodeEvnet().getProperties();
        LimitModel limitModel = taskProperties.getTimeLimitConfig();
        boolean isOn = limitModel.getOn() != 0;
        if (limitModel.getOn() == 2) {
            taskProperties = flowTimeModel.getChildNode().getProperties();
            limitModel = taskProperties.getTimeLimitConfig();
        }
        Map<String, Object> data = JsonUtil.stringToMap(flowTaskEntity.getFlowFormContentJson());
        flowTimeModel.setOn(isOn);
        if (isOn) {
            Date date = null;
            if (limitModel.getNodeLimit() == 0) {
                date = operatorInfo.getCreatorTime();
            } else if (limitModel.getNodeLimit() == 1) {
                date = flowTaskEntity.getCreatorTime();
            } else {
                Object formData = data.get(limitModel.getFormField());
                try {
                    date = new Date((Long) formData);
                } catch (Exception e) {
                }
                if (date == null) {
                    try {
                        date = DateUtil.stringToDate(String.valueOf(formData));
                    } catch (Exception e) {
                    }
                }
            }
            if (date == null) {
                date = flowTaskEntity.getCreatorTime();
            }
            flowTimeModel.setDate(date);
        }
    }

}
