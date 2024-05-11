package com.linzen.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.linzen.base.UserInfo;
import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.entity.FlowTemplateJsonEntity;
import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.engine.model.flowbefore.FlowTemplateAllModel;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ChildNode;
import com.linzen.engine.service.FlowDynamicService;
import com.linzen.engine.service.FlowTaskNewService;
import com.linzen.engine.util.FlowContextHolder;
import com.linzen.engine.util.FlowTaskUtil;
import com.linzen.exception.WorkFlowException;
import com.linzen.model.flow.FlowFormDataModel;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 在线开发工作流
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Service
public class FlowDynamicServiceImpl implements FlowDynamicService {


    @Autowired
    public ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTaskUtil flowTaskUtil;
    @Autowired
    private FlowTaskNewService flowTaskNewService;

    @Override
    public void flowTask(FlowModel flowModel, FlowStatusEnum flowStatus, ChildNode childNode) throws WorkFlowException {
        Map<String, Object> formData = flowModel.getFormData();
        String id = flowModel.getProcessId();
        Map<String, Object> map = flowModel.getFormData();
        formData.put(FlowFormConstant.FLOWID, flowModel.getFlowId());
        String formId = childNode.getProperties().getFormId();
        List<Map<String, Object>> formOperates = childNode.getProperties().getFormOperates();
        FlowFormDataModel formDataModel = FlowFormDataModel.builder().
                formId(formId).id(id).delegateUser(serviceUtil.getUserInfo(flowModel.getUserId())).
                map(map).formOperates(formOperates).build();
        switch (flowStatus) {
            case save:
                flowTaskNewService.save(flowModel);
                serviceUtil.createOrUpdate(formDataModel);
                break;
            case submit:
                FlowContextHolder.addData(formId, formData);
                FlowContextHolder.addChildData(id, formId, formData);
                FlowContextHolder.addFormOperates(id, formId, formOperates);
                flowTaskNewService.submitAll(flowModel);
                break;
            case none:
                serviceUtil.createOrUpdate(formDataModel);
                break;
            default:
                break;
        }
    }

    @Override
    public void createOrUpdate(FlowModel flowModel) throws WorkFlowException {
        FlowTemplateAllModel model = flowTaskUtil.templateJson(flowModel.getFlowId());
        FlowTemplateJsonEntity templateJson = model.getTemplateJson();
        FlowTemplateEntity template = model.getTemplate();
        ChildNode childNode = JsonUtil.createJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
        FlowStatusEnum statusEnum =
                FlowStatusEnum.submit.getMessage().equals(flowModel.getStatus()) ?
                        FlowStatusEnum.submit : template.getType() == 0 ? FlowStatusEnum.save : FlowStatusEnum.none;
        this.flowTask(flowModel, statusEnum, childNode);
    }

    @Override
    @DSTransactional
    public void batchCreateOrUpdate(FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        List<String> batchUserId = flowModel.getDelegateUserList();
        boolean isBatchUser = batchUserId.isEmpty();
        if (isBatchUser) {
            batchUserId.add(userInfo.getUserId());
        }
        for (String id : batchUserId) {
            FlowModel model = BeanUtil.toBean(flowModel, FlowModel.class);
            model.setDelegateUser(isBatchUser ? model.getDelegateUser() : userInfo.getUserId());
            model.setProcessId(StringUtil.isNotEmpty(model.getId()) ? model.getId() : RandomUtil.uuId());
            if (!isBatchUser) {
                SysUserEntity userEntity = serviceUtil.getUserInfo(id);
                if (userEntity != null) {
                    UserInfo info = new UserInfo();
                    info.setUserName(userEntity.getRealName());
                    info.setUserId(userEntity.getId());
                    model.setUserInfo(info);
                }
            }
            model.setUserId(id);
            this.createOrUpdate(model);
        }
    }


}
