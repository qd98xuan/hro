package com.linzen.base.util;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.DictionaryTypeEntity;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowTemplateEntity;
import com.linzen.engine.model.flowtemplate.FlowTemplateCrForm;
import com.linzen.engine.model.flowtemplate.FlowTemplateInfoVO;
import com.linzen.engine.service.FlowTemplateJsonService;
import com.linzen.engine.service.FlowTemplateService;
import com.linzen.entity.FlowFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.onlinedev.model.OnlineDevData;
import com.linzen.service.FlowFormService;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.emnus.DictionaryDataEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 在线开发流程及表单相关方法
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@Slf4j
public class VisualFlowFormUtil {
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 修改流程基本信息及状态
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public ServiceResult saveOrUpdateFlowTemp(VisualdevEntity entity, Integer state, Boolean isSave) {
        ServiceResult result;
        FlowTemplateCrForm flowTemplateCrForm = new FlowTemplateCrForm();
        BeanUtils.copyProperties(entity, flowTemplateCrForm);
        flowTemplateCrForm.setFullName(entity.getFullName());
        flowTemplateCrForm.setEnCode(entity.getEnCode());
        flowTemplateCrForm.setId(entity.getId());
        flowTemplateCrForm.setType(OnlineDevData.FLOW_TYPE_DEV);
        flowTemplateCrForm.setFormId(entity.getId());
        flowTemplateCrForm.setCategory(categaryMapping(entity.getCategory()));
        if (flowTemplateService.isExistByFullName(flowTemplateCrForm.getFullName(), flowTemplateCrForm.getId())) {
            return ServiceResult.error("流程名称不能重复");
        }
        if (flowTemplateService.isExistByEnCode(flowTemplateCrForm.getEnCode(), flowTemplateCrForm.getId())) {
            return ServiceResult.error("流程编码不能重复");
        }
        try {
            FlowTemplateEntity info = flowTemplateService.getById(entity.getId());
            if (info==null) {
                FlowTemplateEntity creEntity = BeanUtil.toBean(flowTemplateCrForm, FlowTemplateEntity.class);
                creEntity.setEnabledMark(OnlineDevData.STATE_DISABLE);
                flowTemplateService.create(creEntity);
                result = ServiceResult.success("创建成功");
            } else {
                FlowTemplateEntity creEntity = BeanUtil.toBean(flowTemplateCrForm, FlowTemplateEntity.class);
                if(Objects.equals(state,OnlineDevData.STATE_ENABLE)){
                    creEntity.setEnabledMark(OnlineDevData.STATE_ENABLE);
                }
                flowTemplateService.update(entity.getId(), creEntity);
                result = ServiceResult.success("修改成功");
            }
        } catch (Exception e) {
            result = ServiceResult.error("操作失败！");
        }
        return result;
    }

    /**
     * 保存或修改流程表单信息
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public void saveOrUpdateForm(VisualdevEntity entity, int delFlag, boolean isSave) throws WorkFlowException {
        String userId = userProvider.get().getUserId();
        FlowFormEntity flowFormEntity = Optional.ofNullable(flowFormService.getById(entity.getId())).orElse(new FlowFormEntity());
        flowFormEntity.setId(entity.getId());
//        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        flowFormEntity.setEnCode(entity.getEnCode());
        flowFormEntity.setFullName(entity.getFullName());
        //功能流程（在线开发-自定义表单-隐藏）
        flowFormEntity.setFlowType(OnlineDevData.FLOW_TYPE_DEV);
        flowFormEntity.setFormType(OnlineDevData.FORM_TYPE_DEV);
        if (entity.getType() == 4) {//功能系统表单，代码生成-功能系统表单
            flowFormEntity.setFormType(OnlineDevData.FORM_TYPE_SYS);
        }
        if (entity.getType() == 3) {//发起系统表单，代码生成-发起系统表单
            flowFormEntity.setFlowType(OnlineDevData.FLOW_TYPE_FLOW);
            flowFormEntity.setFormType(OnlineDevData.FORM_TYPE_SYS);
        }
        flowFormEntity.setCategory(entity.getCategory());
        flowFormEntity.setPropertyJson(entity.getFormData());
        flowFormEntity.setDescription(entity.getDescription());
        flowFormEntity.setSortCode(entity.getSortCode());
        flowFormEntity.setEnabledMark(delFlag);
        if (isSave) {
            flowFormEntity.setCreatorTime(new Date());
            flowFormEntity.setCreatorUserId(userId);
        } else {

            flowFormEntity.setUpdateTime(new Date());
            flowFormEntity.setUpdateUserId(userId);
        }
        flowFormEntity.setTableJson(entity.getVisualTables());
        flowFormEntity.setDbLinkId(entity.getDbLinkId());
        flowFormEntity.setFlowId(entity.getId());
        //判断名称是否重复
        if (flowFormService.isExistByFullName(flowFormEntity.getFullName(), flowFormEntity.getId())) {
            throw new WorkFlowException(MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (flowFormService.isExistByEnCode(flowFormEntity.getEnCode(), flowFormEntity.getId())) {
            throw new WorkFlowException(MsgCode.EXIST002.get());
        }
        flowFormService.saveOrUpdate(flowFormEntity);
    }

    /**
     * 删除流程引擎信息
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public void deleteTemplateInfo(String id) {
        String msg = "";
        try {
            FlowTemplateEntity entity = flowTemplateService.getInfo(id);
            flowTemplateService.delete(entity);
        } catch (Exception e) {
            msg = e.getMessage();
        }
    }

    /**
     * 获取流程引擎信息
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public FlowTemplateInfoVO getTemplateInfo(String id) {
        FlowFormEntity byId = flowFormService.getById(id);
        FlowTemplateInfoVO vo = new FlowTemplateInfoVO();
        try {
            vo = flowTemplateService.info(byId.getFlowId());
        } catch (Exception e) {
            vo = null;
        }
        return vo;
    }

    /**
     * 获取字典相关列表
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public List<DictionaryDataEntity> getListByTypeDataCode(Integer type) {
        return getListByTypeDataCode(DictionaryDataEnum.getTypeId(type));
    }

    /**
     * 获取字典数据
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public DictionaryDataEntity getdictionaryDataInfo(String category) {
        return dictionaryDataService.getInfo(category);
    }

    /**
     * 将在线开发分类字段转换成流程分类字段id
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public String categaryMapping(String devCateId) {
        //流程分类
        String flowCateId = "";
        try {
            List<DictionaryDataEntity> flowDictionList = getListByTypeDataCode(DictionaryDataEnum.FLOWWOEK_ENGINE.getDictionaryTypeId());
            List<DictionaryDataEntity> devDictionList = getListByTypeDataCode(DictionaryDataEnum.VISUALDEV.getDictionaryTypeId());
            for (DictionaryDataEntity devItem : devDictionList) {
                if (devItem.getId().equals(devCateId)) {
                    for (DictionaryDataEntity flowItem : flowDictionList) {
                        if (flowItem.getEnCode().equals(devItem.getEnCode())) {
                            flowCateId = flowItem.getId();
                        }
                        if (StringUtil.isEmpty(flowCateId) && OnlineDevData.DEFAULT_CATEGATY_ENCODE.equals(flowItem.getEnCode())) {//没值，给默认
                            flowCateId = flowItem.getId();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("分类字段转换失败！:{}", e.getMessage());
        }
        return flowCateId;
    }

    /**
     * 获取字典数据信息列表
     *
     * @param typeCode 字典分类code
     * @return
     */
    public List<DictionaryDataEntity> getListByTypeDataCode(String typeCode) {
        DictionaryTypeEntity dictionaryTypeEntity = dictionaryTypeService.getInfoByEnCode(typeCode);
        List<DictionaryDataEntity> list = null;
        if (dictionaryTypeEntity != null) {
            list = dictionaryDataService.getList(dictionaryTypeEntity.getId());
        }
        return list;
    }

    /**
     * 删除流程表单信息
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public void deleteFlowForm(String id) {
        try {
            flowFormService.removeById(id);
        } catch (Exception e) {
        }
    }

    /**
     * 逻辑删除恢复流程和表单
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public void saveLogicFlowAndForm(VisualdevEntity entity) throws WorkFlowException {
        flowFormService.saveLogicFlowAndForm(entity.getId());
        flowTemplateService.saveLogicFlowAndForm(entity.getId());
        this.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, false);
        this.saveOrUpdateFlowTemp(entity, OnlineDevData.STATE_DISABLE, false);
    }
}
