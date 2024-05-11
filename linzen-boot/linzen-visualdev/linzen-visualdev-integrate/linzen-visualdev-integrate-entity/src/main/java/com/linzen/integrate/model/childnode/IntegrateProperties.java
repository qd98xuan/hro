package com.linzen.integrate.model.childnode;

import com.linzen.database.model.superQuery.SuperQueryJsonModel;
import com.linzen.emnus.SearchMethodEnum;
import com.linzen.model.visualJson.FieLdsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP管理员/admin
 * @date 2023-04-01
 */
@Data
public class IntegrateProperties {
    private String title;
    private String formId;
    private String flowId;
    private Integer formType = 1;
    private List<FieLdsModel> formFieldList = new ArrayList<>();
    private List<TransferModel> transferList = new ArrayList<>();
    private List<SuperQueryJsonModel> ruleList = new ArrayList<>();
    private Integer triggerEvent;
    //0.不新增 1.新增
    private Integer addRule = 0;
    //0-不更新 1-新增
    private Integer unFoundRule = 0;
    //0-删除未找到 1-删除已找到
    private Integer deleteRule = 0;
    private String ruleMatchLogic = SearchMethodEnum.And.getSymbol();


    private String msgId;
    private List<String> msgUserType = new ArrayList<>();
    private List<String> msgUserIds = new ArrayList<>();
    private List<IntegrateTemplateModel> templateJson = new ArrayList<>();
    private List<IntegrateTemplateModel> interfaceTemplateJson = new ArrayList<>();
    private IntegrateMsgModel startMsgConfig = new IntegrateMsgModel();
    private IntegrateMsgModel failMsgConfig= new IntegrateMsgModel();



    private Long startTime;
    private String cron;
    private Integer endTimeType = 1;
    //次数
    private Integer endLimit = 1;
    //结束时间
    private Long endTime;
    //类型
    private Integer integrateType = 2;

    private List<String> initiator;



}
