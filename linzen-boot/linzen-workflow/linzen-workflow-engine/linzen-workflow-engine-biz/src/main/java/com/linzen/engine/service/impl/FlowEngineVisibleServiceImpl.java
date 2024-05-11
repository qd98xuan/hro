package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowEngineVisibleEntity;
import com.linzen.engine.mapper.FlowEngineVisibleMapper;
import com.linzen.engine.model.flowtask.FlowAssistModel;
import com.linzen.engine.service.FlowEngineVisibleService;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.util.RandomUtil;
import com.linzen.util.ServiceAllUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程可见
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowEngineVisibleServiceImpl extends SuperServiceImpl<FlowEngineVisibleMapper, FlowEngineVisibleEntity> implements FlowEngineVisibleService {

    @Autowired
    private ServiceAllUtil serviceUtil;

    @Override
    public List<FlowEngineVisibleEntity> getList(List<String> flowIdList) {
        List<FlowEngineVisibleEntity> list = new ArrayList<>();
        if (flowIdList.size() > 0) {
            QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowEngineVisibleEntity::getFlowId, flowIdList);
            queryWrapper.lambda().orderByAsc(FlowEngineVisibleEntity::getSortCode).orderByDesc(FlowEngineVisibleEntity::getCreatorTime);
            list.addAll(this.list(queryWrapper));
        }
        return list;
    }

    @Override
    public List<FlowEngineVisibleEntity> getList() {
        QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowEngineVisibleEntity> getVisibleFlowList(String userId) {
        return getVisibleFlowList(userId, 1);
    }

    @Override
    public List<FlowEngineVisibleEntity> getVisibleFlowList(String userId, Integer type) {
        List<String> userRelationList = serviceUtil.getListByUserIdAll(ImmutableList.of(userId)).stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        userRelationList.add(userId);
        QueryWrapper<FlowEngineVisibleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(FlowEngineVisibleEntity::getOperatorId, userRelationList);
        wrapper.lambda().eq(FlowEngineVisibleEntity::getType, type);
        List<FlowEngineVisibleEntity> flowList = this.list(wrapper);
        return flowList;
    }

    @Override
    public void deleteVisible(String flowId) {
        QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineVisibleEntity::getFlowId, flowId);
        this.remove(queryWrapper);
    }

    @Override
    public void assistList(FlowAssistModel assistModel) {
        List<FlowEngineVisibleEntity> assistListAll = this.getList(ImmutableList.of(assistModel.getTemplateId()));
        for (FlowEngineVisibleEntity entity : assistListAll) {
            this.removeById(entity.getId());
        }
        List<String> list = assistModel.getList();
        List<FlowEngineVisibleEntity> visibleList = new ArrayList<>();
        for (String idAll : list) {
            String[] id = idAll.split("--");
            String operatorId = id[0];
            String type = id.length > 1 ? id[1] : "user";
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(operatorId);
            visible.setOperatorType(type);
            visible.setType(2);
            visibleList.add(visible);
        }
        for (int i = 0; i < visibleList.size(); i++) {
            FlowEngineVisibleEntity visibleEntity = visibleList.get(i);
            visibleEntity.setId(RandomUtil.uuId());
            visibleEntity.setFlowId(assistModel.getTemplateId());
            visibleEntity.setSortCode(Long.parseLong(i + ""));
            this.save(visibleEntity);
        }
    }
}
