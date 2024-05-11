package com.linzen.engine.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowTaskNodeEntity;
import com.linzen.engine.enums.FlowNodeEnum;
import com.linzen.engine.mapper.FlowTaskNodeMapper;
import com.linzen.engine.model.flowtasknode.TaskNodeListModel;
import com.linzen.engine.service.FlowTaskNodeService;
import com.linzen.engine.util.FlowNature;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowTaskNodeServiceImpl extends SuperServiceImpl<FlowTaskNodeMapper, FlowTaskNodeEntity> implements FlowTaskNodeService {

    @Override
    public List<FlowTaskNodeEntity> getList(String taskId, SFunction<FlowTaskNodeEntity, ?>... columns) {
        TaskNodeListModel nodeListModel = new TaskNodeListModel();
        nodeListModel.setId(taskId);
        return getList(nodeListModel, columns);
    }

    @Override
    public List<FlowTaskNodeEntity> getList(List<String> id, SFunction<FlowTaskNodeEntity, ?>... columns) {
        List<FlowTaskNodeEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTaskNodeEntity::getId, id);
            queryWrapper.lambda().select(columns);
            list.addAll(this.list(queryWrapper));
        }
        return list;
    }

    @Override
    public List<FlowTaskNodeEntity> getList(TaskNodeListModel nodeListModel, SFunction<FlowTaskNodeEntity, ?>... columns) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        String taskId = nodeListModel.getId();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        Integer state = nodeListModel.getState();
        if (ObjectUtil.isNotEmpty(state)) {
            queryWrapper.lambda().eq(FlowTaskNodeEntity::getState, state);
        }
        Integer completion = nodeListModel.getCompletion();
        if (ObjectUtil.isNotEmpty(completion)) {
            queryWrapper.lambda().eq(FlowTaskNodeEntity::getCompletion, completion);
        }
        String nodeCode = nodeListModel.getNotNodeCode();
        if (ObjectUtil.isNotEmpty(nodeCode)) {
            queryWrapper.lambda().ne(FlowTaskNodeEntity::getNodeCode, nodeCode);
        }
        queryWrapper.lambda().select(columns);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskNodeEntity getInfo(String id, SFunction<FlowTaskNodeEntity, ?>... columns) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getId, id);
        queryWrapper.lambda().select(columns);
        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(FlowTaskNodeEntity entity) {
        this.save(entity);
    }

    @Override
    public void update(FlowTaskNodeEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void update(String taskId) {
        UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        wrapper.lambda().set(FlowTaskNodeEntity::getCompletion, FlowNodeEnum.Futility.getCode());
        wrapper.lambda().set(FlowTaskNodeEntity::getState, FlowNodeEnum.Futility.getCode());
        this.update(wrapper);
    }

    @Override
    public void updateCompletion(List<String> id, int start) {
        if (id.size() > 0) {
            UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
            wrapper.lambda().in(FlowTaskNodeEntity::getId, id);
            wrapper.lambda().set(FlowTaskNodeEntity::getCompletion, start);
            this.update(wrapper);
        }
    }

    @Override
    public void updateTaskNode(List<FlowTaskNodeEntity> taskNodeLis) {
        for (FlowTaskNodeEntity taskNodeLi : taskNodeLis) {
            String nodeNext = StringUtil.isNotEmpty(taskNodeLi.getNodeNext()) ? taskNodeLi.getNodeNext() : FlowNature.NodeEnd;
            UpdateWrapper<FlowTaskNodeEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(FlowTaskNodeEntity::getId, taskNodeLi.getId());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getNodeNext, nodeNext);
            updateWrapper.lambda().set(FlowTaskNodeEntity::getSortCode, taskNodeLi.getSortCode());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getState, taskNodeLi.getState());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getCompletion, taskNodeLi.getCompletion());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getNodePropertyJson, taskNodeLi.getNodePropertyJson());
            this.update(updateWrapper);
        }
    }

    @Override
    public void updateTaskNodeCandidates(List<String> id, String candidates) {
        if (id.size() > 0) {
            UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
            wrapper.lambda().in(FlowTaskNodeEntity::getId, id);
            wrapper.lambda().set(FlowTaskNodeEntity::getCandidates, candidates);
            this.update(wrapper);
        }
    }

    @Override
    public void updateTaskIdList(FlowTaskNodeEntity entity) {
        UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(FlowTaskNodeEntity::getId, entity.getId());
        wrapper.lambda().set(FlowTaskNodeEntity::getNodePropertyJson, entity.getNodePropertyJson());
        this.update(wrapper);
    }

}
