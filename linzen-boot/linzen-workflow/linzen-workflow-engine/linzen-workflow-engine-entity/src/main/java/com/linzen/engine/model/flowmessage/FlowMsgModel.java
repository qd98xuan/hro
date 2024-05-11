package com.linzen.engine.model.flowmessage;

import com.linzen.engine.entity.FlowTaskCirculateEntity;
import com.linzen.engine.entity.FlowTaskEntity;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowbefore.FlowTemplateAllModel;
import com.linzen.engine.model.flowengine.FlowModel;
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
 * @date 2023-04-01
 */
@Data
@NoArgsConstructor
public class FlowMsgModel {
    private String title;
    private FlowTemplateAllModel flowTemplateAllModel = new FlowTemplateAllModel();
    private Map<String, Object> data = new HashMap<>();
    private FlowModel flowModel = new FlowModel();
    private FlowTaskEntity taskEntity = new FlowTaskEntity();
    private FlowTaskNodeEntity taskNodeEntity = new FlowTaskNodeEntity();
    private List<FlowTaskNodeEntity> nodeList = new ArrayList<>();
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private List<FlowTaskCirculateEntity> circulateList = new ArrayList<>();
    /**
     * 代办 (通知代办)
     */
    private Boolean wait = true;
    /**
     * 同意
     */
    private Boolean approve = false;
    /**
     * 拒绝
     */
    private Boolean reject = false;
    /**
     * 抄送人
     */
    private Boolean copy = false;
    /**
     * 结束 (通知发起人)
     */
    private Boolean end = false;
    /**
     * 子流程通知
     */
    private Boolean launch = false;
    /**
     * 拒绝发起节点
     */
    private Boolean start = false;
    /**
     * 超时
     */
    private Boolean overtime = false;
    /**
     * 提醒
     */
    private Boolean notice = false;
}
