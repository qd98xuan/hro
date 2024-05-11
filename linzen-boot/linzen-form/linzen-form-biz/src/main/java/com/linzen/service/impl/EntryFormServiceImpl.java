package com.linzen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.ServiceResult;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.entity.EntryFormEntity;
import com.linzen.entity.EntryFormEntity;
import com.linzen.exception.WorkFlowException;
import com.linzen.mapper.EntryFormMapper;
import com.linzen.mapper.FlowFormMapper;
import com.linzen.model.flow.FlowTempInfoModel;
import com.linzen.model.form.FlowFormPage;
import com.linzen.model.form.FormDraftJsonModel;
import com.linzen.model.form.VisualTableModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.FormCloumnUtil;
import com.linzen.model.visualJson.FormDataModel;
import com.linzen.model.visualJson.TableModel;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.model.visualJson.analysis.RecursionForm;
import com.linzen.service.EntryFormService;
import com.linzen.service.FlowFormService;
import com.linzen.util.*;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class EntryFormServiceImpl extends SuperServiceImpl<EntryFormMapper, EntryFormEntity> implements EntryFormService {

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private VisualDevTableCre formTableCre;

    @Autowired
    private ConcurrencyUtils concurrencyUtils;

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(EntryFormEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCOde, String id) {
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getEnCode, enCOde);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(EntryFormEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    @SneakyThrows
    public Boolean create(EntryFormEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }

        FormDataModel formDataModel = new FormDataModel();
        //判断是否要创表
        List<TableModel> tableModels = new ArrayList<>();
        Map<String, Object> formMap = null;
        if (entity.getFormType() == 2 && entity.getDraftJson() != null) {
            formDataModel = JsonUtil.createJsonToBean(entity.getDraftJson(), FormDataModel.class);
            tableModels = JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class);
            formMap = JsonUtil.stringToMap(entity.getDraftJson());
        }
        //是否开启安全锁
        Boolean concurrencyLock = formDataModel.getConcurrencyLock();
        Boolean logicalDelete = formDataModel.getLogicalDelete();
        int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();

        //有表
        if (entity.getTableJson() != null) {
            List<TableModel> visualTables = JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class);
            TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1" )).findFirst().orElse(null);
            //判断自增是否匹配
            concurrencyUtils.checkAutoIncrement(primaryKeyPolicy,entity.getDbLinkId(),visualTables);
            //在主表创建锁字段是否开启安全锁
            try {
                if (logicalDelete && mainTable != null) {
                    concurrencyUtils.creDelFlag(mainTable.getTable(), entity.getDbLinkId());
                }
                if (concurrencyLock) {
                    concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                }
                if (mainTable != null && formDataModel.getPrimaryKeyPolicy() == 2) {
                    concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                }
                concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
            } catch (Exception e) {
                log.error("创建字段失败！");
            }
        }
        entity.setDraftJson(JsonUtil.createObjectToString(new FormDraftJsonModel().setDraftJson(entity.getDraftJson()).setTableJson(entity.getTableJson())));
        return this.save(entity);
    }

    @Override
    public Boolean update(EntryFormEntity entity) throws Exception {
        List<TableModel> visualTables = JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class);
        if (entity.getFormType() == 2 && visualTables.size() > 0 && StringUtil.isNotEmpty(entity.getDraftJson())) {
            FormDataModel formDataModel = JsonUtil.createJsonToBean(entity.getDraftJson(), FormDataModel.class);
            TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);
            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
            Boolean logicalDelete = formDataModel.getLogicalDelete();
            if (logicalDelete && mainTable != null) {
                //在主表创建逻辑删除字段
                concurrencyUtils.creDelFlag(mainTable.getTable(), entity.getDbLinkId());
            }
            if (concurrencyLock) {
                //在主表创建锁字段
                concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
            }
            if (mainTable != null && formDataModel.getPrimaryKeyPolicy() == 2) {
                concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
            }
            concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
            //判断自增是否匹配
            concurrencyUtils.checkAutoIncrement(primaryKeyPolicy,entity.getDbLinkId(),visualTables);
        }
        entity.setDraftJson(JsonUtil.createObjectToString(new FormDraftJsonModel().setDraftJson(entity.getDraftJson()).setTableJson(entity.getTableJson())));
        return this.updateById(entity);
    }


    @Override
    public List<EntryFormEntity> getList(FlowFormPage flowFormPage) {
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(flowFormPage.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(EntryFormEntity::getFullName, flowFormPage.getKeyword())
                        .or().like(EntryFormEntity::getEnCode, flowFormPage.getKeyword())
            );
        }
        if (flowFormPage.getFlowType() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getFlowType, flowFormPage.getFlowType());
        }
        if (flowFormPage.getFormType() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getFormType, flowFormPage.getFormType());
        }
        if (flowFormPage.getIsRelease() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getState, flowFormPage.getIsRelease());
        }
        if (flowFormPage.getEnabledMark() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getEnabledMark, flowFormPage.getEnabledMark());
        }
        if (flowFormPage.getFlowType() == null || !flowFormPage.getFlowType().equals(1)) {
            queryWrapper.lambda().and(t -> t.ne(EntryFormEntity::getFlowType, 1).or().ne(EntryFormEntity::getFormType, 2));
        }
        queryWrapper.lambda().orderByAsc(EntryFormEntity::getSortCode);
        queryWrapper.lambda().orderByDesc(EntryFormEntity::getCreatorTime);
        Page<EntryFormEntity> page = new Page<>(flowFormPage.getCurrentPage(), flowFormPage.getPageSize());
        IPage<EntryFormEntity> list = this.page(page, queryWrapper);
        return flowFormPage.setData(list.getRecords(), list.getTotal());
    }

    @Override
    public List<EntryFormEntity> getListForSelect(FlowFormPage flowFormPage) {
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(flowFormPage.getKeyword())) {
            queryWrapper.lambda().like(EntryFormEntity::getFullName, flowFormPage.getKeyword());
        }
        if (flowFormPage.getFlowType() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getFlowType, flowFormPage.getFlowType());
        }
        if (flowFormPage.getFormType() != null) {
            queryWrapper.lambda().eq(EntryFormEntity::getFormType, flowFormPage.getFormType());
        }
        queryWrapper.lambda().eq(EntryFormEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(EntryFormEntity::getSortCode);
        queryWrapper.lambda().orderByDesc(EntryFormEntity::getCreatorTime);
        Page<EntryFormEntity> page = new Page<>(flowFormPage.getCurrentPage(), flowFormPage.getPageSize());
        IPage<EntryFormEntity> list = this.page(page, queryWrapper);
        return flowFormPage.setData(list.getRecords(), list.getTotal());
    }

    @Override
    public ServiceResult release(String id, Integer isRelease) throws WorkFlowException {
        EntryFormEntity byId = this.getById(id);
        if (isRelease != null && isRelease == 0) {//回滚
            if (byId.getEnabledMark() != null && byId.getEnabledMark() == 1) {
                EntryFormEntity entity = new EntryFormEntity();
                entity.setId(id);
                entity.setState(1);
                entity.setDraftJson(JsonUtil.createObjectToString(new FormDraftJsonModel().setDraftJson(byId.getPropertyJson()).setTableJson(byId.getTableJson())));
//                entity.setDraftJson(byId.getPropertyJson());
                this.updateById(entity);
                return ServiceResult.success("回滚成功");
            } else {
                return ServiceResult.error("该表单未发布，无法回滚表单内容");
            }
        }
        if (isRelease != null && isRelease == 1) {//发布
            FormDraftJsonModel formDraft = JsonUtil.createJsonToBean(byId.getDraftJson(), FormDraftJsonModel.class);
            if (StringUtil.isEmpty(formDraft.getDraftJson())) {
                return ServiceResult.error("该模板内表单内容为空，无法发布！");
            }
            EntryFormEntity entity = new EntryFormEntity();
            BeanUtils.copyProperties(byId, entity);
            entity.setId(id);
            entity.setEnabledMark(1);
            entity.setState(1);
            entity.setPropertyJson(formDraft.getDraftJson());
            entity.setTableJson(formDraft.getTableJson());
            entity.setUpdateTime(new Date());
            entity.setUpdateUserId(userProvider.get().getUserId());
            //判断是否要创表
            FormDataModel formDataModel = new FormDataModel();
            List<TableModel> tableModels = null;
            Map<String, Object> formMap = null;
            if (entity.getFormType() == 2 && formDraft.getDraftJson() != null) {
                formDataModel = JsonUtil.createJsonToBean(formDraft.getDraftJson(), FormDataModel.class);
                tableModels = JsonUtil.createJsonToList(formDraft.getTableJson(), TableModel.class);
                formMap = JsonUtil.stringToMap(formDraft.getDraftJson());
            }
            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
            Boolean logicalDelete = formDataModel.getLogicalDelete();
            //无表需要创表
            if (tableModels != null && tableModels.size() == 0) {
                List<FieLdsModel> list = JsonUtil.createJsonToList(formMap.get("fields"), FieLdsModel.class);
                JSONArray formJsonArray = JsonUtil.createJsonToJsonArray(String.valueOf(formMap.get("fields")));
                List<FormAllModel> formAllModel = new ArrayList<>();
                RecursionForm recursionForm = new RecursionForm();
                recursionForm.setTableModelList(JsonUtil.createJsonToList(entity.getTableJson(), TableModel.class));
                recursionForm.setList(list);
                FormCloumnUtil.recursionForm(recursionForm, formAllModel);
                String tableName = "mt" + RandomUtil.uuId();
                String dbLinkId = entity.getDbLinkId();
                VisualTableModel model = new VisualTableModel(formJsonArray, formAllModel, tableName, dbLinkId, entity.getFullName(), concurrencyLock, primaryKeyPolicy, logicalDelete);
                List<TableModel> tableModelList = formTableCre.tableList(model);
                formMap.put("fields", formJsonArray);
                //更新
                entity.setDraftJson(JsonUtil.createObjectToString(formMap));
                entity.setPropertyJson(JsonUtil.createObjectToString(formMap));
                entity.setTableJson(JsonUtil.createObjectToString(tableModelList));
                entity.setDraftJson(JsonUtil.createObjectToString(new FormDraftJsonModel().setDraftJson(entity.getDraftJson()).setTableJson(entity.getTableJson())));
            }
            this.updateById(entity);
            return ServiceResult.success(MsgCode.SU011.get());
        }
        return ServiceResult.error(MsgCode.FA011.get());
    }

    @Override
    public boolean copyForm(String id) {
        EntryFormEntity byId = this.getById(id);
        EntryFormEntity entity = new EntryFormEntity();
        BeanUtils.copyProperties(byId, entity);
        entity.setId(null);
        entity.setPropertyJson(null);
        if (byId.getEnabledMark() != null && byId.getEnabledMark() == 1) {
            entity.setDraftJson(JsonUtil.createObjectToString(new FormDraftJsonModel().setDraftJson(byId.getPropertyJson()).setTableJson(entity.getTableJson())));
        }
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(byId.getFullName() + ".副本" + copyNum);
        entity.setEnCode(byId.getEnCode() + copyNum);
        entity.setEnabledMark(0);
        entity.setState(0);
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        entity.setUpdateTime(null);
        entity.setUpdateUserId(null);
        return this.save(entity);
    }


    @Override
    @DSTransactional
    public ServiceResult ImportData(EntryFormEntity entity, String type) {
        if (entity != null) {
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setCreatorTime(new Date());
            entity.setUpdateTime(null);
            entity.setUpdateUserId(null);
            entity.setEnabledMark(0);
            entity.setState(0);
            entity.setDbLinkId("0");
            List<String> errList = new ArrayList<>();
            EntryFormEntity formEntity = Import(entity, type, errList);
            if (errList.size() > 0) {
                return ServiceResult.error(String.join("、", errList) + "重复");
            }
            try {
                this.setIgnoreLogicDelete().removeById(formEntity);
                this.setIgnoreLogicDelete().saveOrUpdate(formEntity);
                this.clearIgnoreLogicDelete();
            } catch (Exception e) {
                return ServiceResult.error("导入失败:" + e.getMessage());
            }
            return ServiceResult.success(MsgCode.IMP001.get());
        }
        return ServiceResult.error("导入数据格式不正确");
    }

    @Override
    public List<EntryFormEntity> getFlowIdList(String flowId) {
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getFlowId, flowId);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTempInfoModel getFormById(String id) throws WorkFlowException {
        FlowTempInfoModel model = new FlowTempInfoModel();
        EntryFormEntity form = this.getById(id);
        if (form == null) {
            throw new WorkFlowException("该功能未导入流程表单！");
        }
        if (form != null && StringUtil.isNotEmpty(form.getFlowId())) {
            model = this.baseMapper.findFLowInfo(form.getFlowId());
        }
        if (model == null || StringUtil.isEmpty(model.getId())) {
            throw new WorkFlowException("流程未设计，请先设计流程！");
        }
        if (form.getFlowType() == 1 && form.getFormType() == 1 && model.getEnabledMark() != 1) {
            //代码生成的功能流程需要判断流程是否启用。
            throw new WorkFlowException("该功能流程处于停用状态!");
        }
        return model;
    }

    @Override
    public void updateForm(EntryFormEntity entity) {
        UpdateWrapper<EntryFormEntity> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(EntryFormEntity::getId, entity.getId());
        wrapper.lambda().set(EntryFormEntity::getFlowId, entity.getFlowId());
        this.update(wrapper);
    }

    @Override
    public void saveLogicFlowAndForm(String id) {
        EntryFormEntity formEnt = this.setIgnoreLogicDelete().getById(id);
        if(formEnt!=null){
            formEnt.setEnabledMark(null);
            this.setIgnoreLogicDelete().updateById(formEnt);
        }
        this.clearIgnoreLogicDelete();
    }

    private EntryFormEntity Import(EntryFormEntity flowFormEntity, String type, List<String> errList) {
        EntryFormEntity entity = BeanUtil.toBean(flowFormEntity, EntryFormEntity.class);
        boolean skip = Objects.equals("0", type);
        int num = 0;
        QueryWrapper<EntryFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getId, entity.getId());
        if (this.count(queryWrapper) > 0) {
            num++;
            if (skip) {
                errList.add("ID");
            }
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getEnCode, entity.getEnCode());
        if (this.count(queryWrapper) > 0) {
            num++;
            if (skip) {
                errList.add("编码");
            }
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EntryFormEntity::getFullName, entity.getFullName());
        if (this.count(queryWrapper) > 0) {
            num++;
            if (skip) {
                errList.add("名称");
            }
        }
        if (num > 0 && !skip) {
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            entity.setFullName(entity.getFullName() + ".副本" + copyNum);
            entity.setEnCode(entity.getEnCode() + copyNum);
            entity.setFlowId(null);
        }
        entity.setId(RandomUtil.uuId());
        return entity;
    }
}
