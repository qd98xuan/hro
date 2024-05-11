package com.linzen.engine.util;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.engine.entity.*;
import com.linzen.engine.enums.FlowMessageEnum;
import com.linzen.engine.enums.FlowTaskStatusEnum;
import com.linzen.engine.model.flowbefore.FlowTemplateAllModel;
import com.linzen.engine.model.flowdelegate.FlowDelegateModel;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.childnode.Properties;
import com.linzen.engine.model.flowengine.shuntjson.childnode.*;
import com.linzen.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import com.linzen.engine.model.flowmessage.FlowEventModel;
import com.linzen.engine.model.flowmessage.FlowMessageModel;
import com.linzen.engine.model.flowmessage.FlowMsgModel;
import com.linzen.engine.model.flowmessage.FlowParameterModel;
import com.linzen.engine.model.flowtask.FlowContModel;
import com.linzen.engine.service.FlowDelegateService;
import com.linzen.engine.service.FlowEventLogService;
import com.linzen.engine.service.FlowTaskNodeService;
import com.linzen.engine.service.FlowTaskService;
import com.linzen.exception.WorkFlowException;
import com.linzen.message.model.SentMessageForm;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.JsonUtil;
import com.linzen.util.ServiceAllUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class FlowMsgUtil {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTaskUtil flowTaskUtil;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private FlowEventLogService flowEventLogService;

    /**
     * 发送消息
     *
     * @param flowMsgModel
     */
    public void message(FlowMsgModel flowMsgModel) throws WorkFlowException {
        List<SentMessageForm> messageListAll = new ArrayList<>();
        FlowTaskEntity taskEntity = flowMsgModel.getTaskEntity();
        List<String> creatorUserId = new ArrayList<>();
        SysUserEntity user = taskEntity != null ? serviceUtil.getUserInfo(taskEntity.getCreatorUserId()) : null;
        if (taskEntity != null) {
            creatorUserId.add(taskEntity.getCreatorUserId());
        }
        FlowTaskNodeEntity flowTaskNode = flowMsgModel.getTaskNodeEntity();
        FlowTemplateAllModel flowTemplateAllModel = flowMsgModel.getFlowTemplateAllModel();
        List<FlowTaskNodeEntity> nodeList = flowMsgModel.getNodeList();
        List<FlowTaskOperatorEntity> operatorList = flowMsgModel.getOperatorList();
        List<FlowTaskCirculateEntity> circulateList = flowMsgModel.getCirculateList();
        FlowTaskNodeEntity startNode = nodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().orElse(null);
        String nodeJson = startNode != null ? startNode.getNodePropertyJson() : "{}";
        FlowModel flowModel = flowMsgModel.getFlowModel();
        UserInfo userInfo = flowModel.getUserInfo();
        FlowTaskOperatorRecordEntity recordEntity = new FlowTaskOperatorRecordEntity();
        recordEntity.setTaskId(startNode != null ? startNode.getTaskId() : "");
        recordEntity.setHandleId(userInfo != null ? userInfo.getUserId() : "");
        //等待
        if (flowMsgModel.getWait()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig taskMsgConfig = properties.getWaitMsgConfig();
            if (taskMsgConfig.getOn() == 3) {
                taskMsgConfig.setMsgId("PZXTLC001");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(key);
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(recordEntity.getTaskNodeId())).findFirst().orElse(null);
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setFullName(taskEntity.getFullName());
//                if (StringUtil.isNotEmpty(flowMsgModel.getTitle())) {
//                    messageModel.setFullName(taskEntity.getFullName() + flowMsgModel.getTitle());
//                }
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
                for (FlowTaskOperatorEntity operator : taskOperatorList) {
                    List<SentMessageForm> delegationMsg = this.delegationMsg(operator, messageModel, flowTemplateAllModel);
                    messageListAll.addAll(delegationMsg);
                }
            }
        }
        //结束
        if (flowMsgModel.getEnd()) {
            //发起人
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getEndMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC010");
            }
            List<SentMessageForm> messageList = new ArrayList<>();
            FlowMessageModel messageModel = new FlowMessageModel();
            String formId = properties.getFormId();
            Map<String, Object> data = flowTaskUtil.infoData(formId, childNode.getTaskId());
            messageModel.setData(data);
            messageModel.setTitle("已【结束】");
            messageModel.setMsgConfig(msgConfig);
            messageModel.setType(FlowMessageEnum.me.getCode());
            messageModel.setRecordEntity(recordEntity);
            messageModel.setStatus(taskEntity.getStatus());
            messageModel.setFullName(taskEntity.getFullName());
            messageModel.setUserInfo(userInfo);
            List<FlowTaskOperatorEntity> taskOperatorList = new ArrayList() {{
                FlowTaskOperatorEntity operatorEntity = new FlowTaskOperatorEntity();
                operatorEntity.setTaskId(childNode.getTaskId());
                operatorEntity.setTaskNodeId(childNode.getTaskNodeId());
                operatorEntity.setHandleId(taskEntity.getCreatorUserId());
                add(operatorEntity);
            }};
            this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
            this.setMessageList(messageList, messageModel);
            messageListAll.addAll(messageList);
        }
        //同意
        if (flowMsgModel.getApprove()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getApproveMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC002");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(flowTaskNode.getId());
                //默认获取当前节点
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(recordEntity.getTaskNodeId())).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getApproveMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC002");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("已被【同意】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
                for (FlowTaskOperatorEntity operator : taskOperatorList) {
                    List<SentMessageForm> delegationMsg = this.delegationMsg(operator, messageModel, flowTemplateAllModel);
                    messageListAll.addAll(delegationMsg);
                }
            }
        }
        //拒绝
        if (flowMsgModel.getReject()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getRejectMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC003");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(flowTaskNode.getId());
                //默认获取当前节点
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(recordEntity.getTaskNodeId())).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getRejectMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC003");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("已被【退回】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setType(FlowMessageEnum.me.getCode());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
                for (FlowTaskOperatorEntity operator : taskOperatorList) {
                    List<SentMessageForm> delegationMsg = this.delegationMsg(operator, messageModel, flowTemplateAllModel);
                    messageListAll.addAll(delegationMsg);
                }
            }
        }
        //抄送
        if (flowMsgModel.getCopy()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getCopyMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC007");
            }
            Map<String, List<FlowTaskCirculateEntity>> circulateMap = circulateList.stream().collect(Collectors.groupingBy(FlowTaskCirculateEntity::getTaskNodeId));
            for (String key : circulateMap.keySet()) {
                recordEntity.setTaskNodeId(key);
                //默认获取当前节点
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getCopyMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC007");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = new ArrayList<>();
                for (FlowTaskCirculateEntity circulateEntity : circulateMap.get(key)) {
                    FlowTaskOperatorEntity operatorEntity = BeanUtil.toBean(circulateEntity, FlowTaskOperatorEntity.class);
                    operatorEntity.setHandleId(circulateEntity.getObjectId());
                    taskOperatorList.add(operatorEntity);
                }
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("已被【抄送】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setType(FlowMessageEnum.circulate.getCode());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
            }
        }
        //子流程
        if (flowMsgModel.getLaunch()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getLaunchMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC011");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(key);
                //默认获取当前节点
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getLaunchMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC011");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(properties.getFormId(), childNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("请发起【子流程】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setType(FlowMessageEnum.me.getCode());
                messageModel.setStatus(FlowTaskStatusEnum.Draft.getCode());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
            }
        }
        //发起人
        if (flowMsgModel.getStart()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getRejectMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC003");
            }
            List<SentMessageForm> messageList = new ArrayList<>();
            FlowMessageModel meModel = new FlowMessageModel();
            Map<String, Object> data = flowTaskUtil.infoData(properties.getFormId(), childNode.getTaskId());
            meModel.setData(data);
            meModel.setTitle("已被【退回】");
            meModel.setRecordEntity(recordEntity);
            meModel.setStatus(taskEntity.getStatus());
            meModel.setMsgConfig(msgConfig);
            meModel.setType(FlowMessageEnum.me.getCode());
            meModel.setFullName(taskEntity.getFullName());
            meModel.setUserInfo(userInfo);
            List<FlowTaskOperatorEntity> meOperatorList = new ArrayList() {{
                FlowTaskOperatorEntity operatorEntity = new FlowTaskOperatorEntity();
                operatorEntity.setTaskId(flowTaskNode.getTaskId());
                operatorEntity.setHandleId(taskEntity.getCreatorUserId());
                add(operatorEntity);
            }};
            this.messageModel(meOperatorList, flowTemplateAllModel, meModel);
            this.setMessageList(messageList, meModel);
            messageListAll.addAll(messageList);
        }
        //超时
        if (flowMsgModel.getOvertime()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getOvertimeMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC009");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(key);
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getOvertimeMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC009");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("已【超时】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
                for (FlowTaskOperatorEntity operator : taskOperatorList) {
                    List<SentMessageForm> delegationMsg = this.delegationMsg(operator, messageModel, flowTemplateAllModel);
                    messageListAll.addAll(delegationMsg);
                }
            }
        }
        //提醒
        if (flowMsgModel.getNotice()) {
            ChildNodeList childNode = JsonUtil.createJsonToBean(nodeJson, ChildNodeList.class);
            com.linzen.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            MsgConfig msgConfig = properties.getNoticeMsgConfig();
            if (msgConfig.getOn() == 3) {
                msgConfig.setMsgId("PZXTLC008");
            }
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                recordEntity.setTaskNodeId(key);
                FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
                ChildNodeList taskChildNode = JsonUtil.createJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                com.linzen.engine.model.flowengine.shuntjson.childnode.Properties taskProperties = taskChildNode.getProperties();
                MsgConfig taskMsgConfig = taskProperties.getNoticeMsgConfig();
                if (taskMsgConfig.getOn() == 2) {
                    taskMsgConfig = msgConfig;
                }
                if (taskMsgConfig.getOn() == 3) {
                    taskMsgConfig.setMsgId("PZXTLC008");
                }
                List<SentMessageForm> messageList = new ArrayList<>();
                List<FlowTaskOperatorEntity> taskOperatorList = operatorMap.get(key);
                FlowMessageModel messageModel = new FlowMessageModel();
                Map<String, Object> data = flowTaskUtil.infoData(taskNode.getFormId(), taskNode.getTaskId());
                messageModel.setData(data);
                messageModel.setTitle("请尽快【审批】");
                messageModel.setMsgConfig(taskMsgConfig);
                messageModel.setRecordEntity(recordEntity);
                messageModel.setStatus(taskEntity.getStatus());
                messageModel.setFullName(taskEntity.getFullName());
                messageModel.setUserInfo(userInfo);
                this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
                this.setMessageList(messageList, messageModel);
                messageListAll.addAll(messageList);
                for (FlowTaskOperatorEntity operator : taskOperatorList) {
                    List<SentMessageForm> delegationMsg = this.delegationMsg(operator, messageModel, flowTemplateAllModel);
                    messageListAll.addAll(delegationMsg);
                }
            }
        }
        for (SentMessageForm sentMessageForm : messageListAll) {
            FlowTemplateJsonEntity templateJson = flowTemplateAllModel.getTemplateJson();
            sentMessageForm.setFlowName(templateJson.getFullName());
            sentMessageForm.setUserName(user != null ? user.getRealName() : "");
        }
        serviceUtil.sendMessage(messageListAll);
    }

    /**
     * 封装站内信消息
     */
    private void messageModel(List<FlowTaskOperatorEntity> taskOperatorList, FlowTemplateAllModel flowTemplateAllModel, FlowMessageModel messageModel) {
        List<String> userList = new ArrayList<>();
        Map<String, String> contMsg = new HashMap<>();
        for (FlowTaskOperatorEntity taskOperator : taskOperatorList) {
            FlowContModel contModel = this.flowMessage(flowTemplateAllModel, taskOperator, messageModel);
            contMsg.put(taskOperator.getHandleId(), JsonUtil.createObjectToString(contModel));
            userList.add(taskOperator.getHandleId());
        }
        messageModel.setUserList(userList);
        messageModel.setContMsg(contMsg);
    }

    /**
     * 封装站内信对象
     *
     * @param taskOperator
     * @return
     */
    private FlowContModel flowMessage(FlowTemplateAllModel flowTemplateAllModel, FlowTaskOperatorEntity taskOperator, FlowMessageModel messageModel) {
        FlowTemplateJsonEntity templateJson = flowTemplateAllModel.getTemplateJson();
        FlowTemplateEntity template = flowTemplateAllModel.getTemplate();
        FlowContModel contModel = new FlowContModel();
        contModel.setEnCode(template.getEnCode());
        contModel.setFlowId(templateJson.getId());
        contModel.setTaskNodeId(taskOperator.getTaskNodeId());
        contModel.setTaskOperatorId(taskOperator.getId());
        contModel.setProcessId(taskOperator.getTaskId());
        contModel.setType(messageModel.getType());
        contModel.setStatus(messageModel.getStatus());
        return contModel;
    }

    /**
     * 整合发送消息
     *
     * @param messageList
     * @param flowMessageModel
     */
    private void setMessageList(List<SentMessageForm> messageList, FlowMessageModel flowMessageModel) {
        Map<String, Object> data = new HashMap<>(flowMessageModel.getData());
        MsgConfig msgConfig = flowMessageModel.getMsgConfig() != null ? flowMessageModel.getMsgConfig() : new MsgConfig();
        List<String> userList = flowMessageModel.getUserList();
        FlowTaskOperatorRecordEntity record = flowMessageModel.getRecordEntity();
        String templateId = msgConfig.getOn() == 0 ? "0" : msgConfig.getMsgId();
        Boolean sysMessage = msgConfig.getOn() != 0;
        //解析发送配置json，获取消息模板参数
        List<TemplateJsonModel> templateJson = new ArrayList<>();
        for (SendConfigJson sendConfigJson : msgConfig.getTemplateJson()) {
            List<TemplateJsonModel> templateJson1 = sendConfigJson.getParamJson();
            templateJson.addAll(templateJson1);
        }
        SentMessageForm messageModel = new SentMessageForm();
        messageModel.setSysMessage(sysMessage);
        messageModel.setTemplateId(templateId);
        messageModel.setToUserIds(userList);
        Map<String, Object> parameterMap = new HashMap<>();
        for (TemplateJsonModel templateJsonModel : templateJson) {
            String fieldId = templateJsonModel.getField();
            String msgTemplateId = templateJsonModel.getMsgTemplateId();
            String relationField = templateJsonModel.getRelationField();
            String dataValue = data.get(relationField) != null ? String.valueOf(data.get(relationField)) : "";
            String dataFieldValue = relationField;
            String dataJson = "1".equals(templateJsonModel.getSourceType()) ? dataValue : dataFieldValue;
            FlowEventModel eventModel = FlowEventModel.builder().data(data).dataJson(dataJson).record(record).templateJson(templateJsonModel).build();
            dataJson = this.data(eventModel);
            parameterMap.put(msgTemplateId + fieldId, dataJson);
        }
        data.putAll(parameterMap);
        messageModel.setUserInfo(flowMessageModel.getUserInfo());
        messageModel.setParameterMap(data);
        messageModel.setContentMsg(flowMessageModel.getContMsg());
        messageModel.setTitle(flowMessageModel.getFullName());
        if (userList.size() > 0) {
            messageList.add(messageModel);
        }
    }

    /**
     * @return
     */
    private String data(FlowEventModel eventModel) {
        FlowTaskOperatorRecordEntity record = eventModel.getRecord();
        TemplateJsonModel templateJson = eventModel.getTemplateJson();
        String relationField = StringUtil.isNotEmpty(templateJson.getRelationField()) ? templateJson.getRelationField() : "";
        boolean isType = "1".equals(templateJson.getSourceType());
        boolean isSubTable = templateJson.getIsSubTable();
        String dataJson = eventModel.getDataJson();
        Map<String, Object> data = eventModel.getData();
        UserInfo userInfo = userProvider.get();
        String userId = StringUtil.isNotEmpty(userInfo.getUserId()) ? userInfo.getUserId() : StringUtil.isNotEmpty(record.getHandleId()) ? record.getHandleId() : "";
        SysUserEntity userEntity = serviceUtil.getUserInfo(userId);
        String userName = userEntity != null ? userEntity.getRealName() : "";
        String value = dataJson;
        FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(record.getTaskId(), FlowTaskEntity::getFlowId
                , FlowTaskEntity::getFlowName, FlowTaskEntity::getFullName, FlowTaskEntity::getCreatorUserId);
        if (isType) {
            switch (relationField) {
                case "@flowId":
                    value = taskEntity.getFlowId();
                    break;
                case "@taskId":
                    value = record.getTaskId();
                    break;
                case "@taskNodeId":
                    value = record.getTaskNodeId();
                    break;
                case "@flowFullName":
                    value = taskEntity.getFlowName();
                    break;
                case "@taskFullName":
                    value = taskEntity.getFullName();
                    break;
                case "@launchUserId":
                    value = taskEntity.getCreatorUserId();
                    break;
                case "@launchUserName":
                    SysUserEntity createUser = taskEntity != null ? serviceUtil.getUserInfo(taskEntity.getCreatorUserId()) : null;
                    value = createUser != null ? createUser.getRealName() : "";
                    break;
                case "@flowOperatorUserId":
                    value = userId;
                    break;
                case "@flowOperatorUserName":
                    value = userName;
                    break;
                default:
                    String[] model = StringUtil.isNotEmpty(relationField) ? relationField.split("-") : new String[]{};
                    if (model.length > 1) {
                        Object dataList = data.get(model[0]);
                        if (dataList instanceof List) {
                            List<Map<String, Object>> listAll = (List<Map<String, Object>>) dataList;
                            List<Object> list = new ArrayList<>();
                            for (Map<String, Object> objectMap : listAll) {
                                list.add(objectMap.get(model[1]));
                            }
                            value = String.valueOf(list);
                        }
                    }
                    break;
            }
        }
        return value;
    }


    //--------------------------------------------事件处理---------------------------------------------------------

    /**
     * 流程事件
     *
     * @param status    事件状态 1.发起 2.结束 3.发起撤回 4同意 5拒绝 6节点撤回 7 超时 8提醒
     * @param childNode 节点数据
     * @param record    审批数据
     */
    public void event(Integer status, ChildNodeList childNode, FlowTaskOperatorRecordEntity record, FlowModel flowModel) {
        if (childNode != null) {
            boolean on = false;
            String interId = "";
            List<TemplateJsonModel> templateJsonModelList = new ArrayList<>();
            FuncConfig config = null;
            List<Integer> eventList = ImmutableList.of(2, 7, 8);
            Map<Integer, String> map = ImmutableMap.of(1, "发起事件", 2, "结束事件",
                    3, "撤回事件", 4, "同意事件", 5, "退回事件", 6, "撤回事件",
                    7, "超时事件", 8, "提醒事件");
            //属性
            Properties properties = childNode.getProperties();
            Integer funcConfigRule = properties.getFuncConfigRule();
            switch (status) {
                case 1:
                    config = properties.getInitFuncConfig();
                    break;
                case 2:
                    config = properties.getEndFuncConfig();
                    break;
                case 3:
                    config = properties.getFlowRecallFuncConfig();
                    break;
                case 4:
                    config = properties.getApproveFuncConfig();
                    break;
                case 5:
                    config = properties.getRejectFuncConfig();
                    break;
                case 6:
                    config = properties.getRecallFuncConfig();
                    break;
                case 7:
                    config = properties.getOvertimeFuncConfig();
                    break;
                case 8:
                    config = properties.getNoticeFuncConfig();
                    break;
                default:
                    break;
            }
            if (config != null) {
                on = config.getOn();
                interId = config.getInterfaceId();
                templateJsonModelList = config.getTemplateJson();
            }
            if (on && StringUtil.isNotEmpty(interId)) {
                Map<String, Object> data = flowModel.getFormData();
                Map<String, String> parameterMap = new HashMap<>();
                for (TemplateJsonModel templateJsonModel : templateJsonModelList) {
                    String fieldId = templateJsonModel.getField();
                    String relationField = templateJsonModel.getRelationField();
                    String dataValue = data.get(relationField) != null ? String.valueOf(data.get(relationField)) : null;
                    String dataFieldValue = relationField;
                    String dataJson = "1".equals(templateJsonModel.getSourceType()) ? dataValue : dataFieldValue;
                    FlowEventModel eventModel = FlowEventModel.builder().data(data).dataJson(dataJson).record(record).templateJson(templateJsonModel).build();
                    dataJson = this.data(eventModel);
                    parameterMap.put(fieldId, dataJson);
                }
                List<FlowParameterModel> allEvent = FlowContextHolder.getAllEvent();
                if (allEvent.size() == 0) {
                    ServiceResult result = serviceUtil.infoToId(interId, parameterMap);
                    boolean error = Objects.equals(400, result.getCode());
                    FlowEventLogEntity logEntity = new FlowEventLogEntity();
                    logEntity.setTaskNodeId(childNode.getTaskNodeId());
                    logEntity.setFullName(map.get(status));
                    logEntity.setInterfaceId(interId);
                    logEntity.setResult(error ? result.getMsg() : "");
                    flowEventLogService.create(logEntity);
                    if (error && !eventList.contains(status) && Objects.equals(FlowNature.FuncTerminate, funcConfigRule)) {
                        FlowContextHolder.addEvent(interId, parameterMap);
                    }
                }
            }
        }
    }

    /**
     * 封装委托消息
     *
     * @param operator
     * @param messageModel
     * @return
     */
    private List<SentMessageForm> delegationMsg(FlowTaskOperatorEntity operator, FlowMessageModel messageModel, FlowTemplateAllModel flowTemplateAllModel) {
        List<SentMessageForm> messageList = new ArrayList<>();
        FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(operator.getTaskId(), FlowTaskEntity::getFlowId);
        if (taskEntity != null) {
            //todo 获取委托人
            List<String> userList = flowDelegateService.getUser(null, taskEntity.getTemplateId(), operator.getHandleId()).stream().map(FlowDelegateEntity::getToUserId).collect(Collectors.toList());
            List<FlowTaskOperatorEntity> taskOperatorList = new ArrayList<>();
            for (String user : userList) {
                FlowTaskOperatorEntity delegaOperator = BeanUtil.toBean(operator, FlowTaskOperatorEntity.class);
                delegaOperator.setHandleId(user);
                taskOperatorList.add(delegaOperator);
            }
            this.messageModel(taskOperatorList, flowTemplateAllModel, messageModel);
            this.setMessageList(messageList, messageModel);
        }
        return messageList;
    }


    //--------------------------------------委托消息------------------------------------------------------
    public void delegateMsg(FlowDelegateModel flowDelegate) {
        List<String> toUserIds = flowDelegate.getToUserIds();
        UserInfo userInfo = flowDelegate.getUserInfo();
        FlowTaskEntity flowTask = flowDelegate.getFlowTask();
        FlowTemplateAllModel templateAllModel = flowDelegate.getTemplateAllModel();
        Map<String, String> contentMsg = new HashMap<>();
        Boolean delegate = flowDelegate.getDelegate();
        boolean approve = flowDelegate.getApprove();
        if (approve) {
            SentMessageForm flowMsgModel = new SentMessageForm();
            flowMsgModel.setToUserIds(toUserIds);
            flowMsgModel.setUserInfo(flowDelegate.getUserInfo());
//            String flowName  = flowTask.getFlowName();
            String flowName = templateAllModel.getTemplateJson().getFullName();
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("@Mandator", userInfo.getUserName());
            parameterMap.put("@Title", flowTask != null ? flowTask.getFullName() : "");
            flowMsgModel.setParameterMap(parameterMap);
            //1.委托设置 2.委托给我
            Integer type = flowDelegate.getType();
            if (delegate) {
                String title = FlowNature.StartMsg.equals(type) ? "了发起委托" : FlowNature.ApproveMsg.equals(type) ? "了审批委托" : "的委托已结束";
                String templateId = FlowNature.StartMsg.equals(type) ? "PZXTLG012" : FlowNature.ApproveMsg.equals(type) ? "PZXTLG013" : "PZXTLG014";
                String content = userInfo.getUserName() + "向您发起" + title;
                flowMsgModel.setTitle(content);
                flowMsgModel.setTemplateId(templateId);
            } else {
                String title = FlowNature.StartMsg.equals(type) ? "已发起了您的" : "已审批了您的";
                String templateId = FlowNature.StartMsg.equals(type) ? "PZXTLG015" : "PZXTLG016";
                String content = userInfo.getUserName() + title + flowName;
                flowMsgModel.setTitle(content);
                flowMsgModel.setTemplateId(templateId);
            }
            Integer delegateType = delegate ? FlowNature.EndMsg : FlowNature.ApproveMsg;
            contentMsg.put("type", delegateType + "");
            flowMsgModel.setContentMsg(contentMsg);
            flowMsgModel.setFlowType(2);
            flowMsgModel.setType(2);
            List<SentMessageForm> messageListAll = new ArrayList<>();
            if (toUserIds.size() > 0) {
                messageListAll.add(flowMsgModel);
            }
            serviceUtil.sendDelegateMsg(messageListAll);
        }
    }

}
