package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.engine.entity.FlowDelegateEntity;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.enums.FlowNodeEnum;
import com.linzen.engine.mapper.FlowTaskOperatorMapper;
import com.linzen.engine.service.FlowDelegateService;
import com.linzen.engine.service.FlowTaskOperatorService;
import com.linzen.engine.util.FlowNature;
import com.linzen.exception.WorkFlowException;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程经办记录
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowTaskOperatorServiceImpl extends SuperServiceImpl<FlowTaskOperatorMapper, FlowTaskOperatorEntity> implements FlowTaskOperatorService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowDelegateService flowDelegateService;

    @Override
    public List<FlowTaskOperatorEntity> getList(String taskId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId).orderByDesc(FlowTaskOperatorEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskOperatorEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getId, id);
        FlowTaskOperatorEntity entity = this.getOne(queryWrapper);
        if (entity == null) {
            throw new WorkFlowException(MsgCode.WF123.get());
        }
        return entity;
    }

    @Override
    public FlowTaskOperatorEntity getOperatorInfo(String id) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getId, id);
        FlowTaskOperatorEntity entity = this.getOne(queryWrapper);
        return entity;
    }


    @Override
    public void create(List<FlowTaskOperatorEntity> entitys) {
        for (FlowTaskOperatorEntity entity : entitys) {
            this.save(entity);
        }
    }

    @Override
    public void update(FlowTaskOperatorEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void update(String taskNodeId, List<String> userId, String completion) {
        if (userId.size() > 0) {
            UpdateWrapper<FlowTaskOperatorEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskNodeId, taskNodeId);
            updateWrapper.lambda().in(FlowTaskOperatorEntity::getHandleId, userId);
            updateWrapper.lambda().set(FlowTaskOperatorEntity::getCompletion, FlowNature.AuditCompletion);
            this.update(updateWrapper);
        }
    }

    @Override
    public void update(String taskNodeId, Integer type) {
        UpdateWrapper<FlowTaskOperatorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskNodeId, taskNodeId);
        updateWrapper.lambda().eq(FlowTaskOperatorEntity::getType, type);
        updateWrapper.lambda().set(FlowTaskOperatorEntity::getCompletion, FlowNature.AuditCompletion);
        this.update(updateWrapper);
    }

    @Override
    public void update(String taskId) {
        UpdateWrapper<FlowTaskOperatorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId);
        updateWrapper.lambda().set(FlowTaskOperatorEntity::getState, FlowNodeEnum.Futility.getCode());
        this.update(updateWrapper);
    }

    @Override
    public List<FlowTaskOperatorEntity> press(String taskId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getCompletion, FlowNature.ProcessCompletion)
                .eq(FlowTaskOperatorEntity::getTaskId, taskId)
                .eq(FlowTaskOperatorEntity::getState, FlowNodeEnum.Process.getCode() + "");
        return this.list(queryWrapper);
    }

    @Override
    public void updateReject(String taskId, Set<String> taskNodeId) {
        if (taskNodeId.size() > 0) {
            UpdateWrapper<FlowTaskOperatorEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId);
            updateWrapper.lambda().in(FlowTaskOperatorEntity::getTaskNodeId, taskNodeId);
            updateWrapper.lambda().set(FlowTaskOperatorEntity::getState, FlowNodeEnum.Futility.getCode());
            this.update(updateWrapper);
        }
    }

    @Override
    public void deleteList(List<String> idAll) {
        if (idAll.size() > 0) {
            QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTaskOperatorEntity::getId, idAll);
            this.remove(queryWrapper);
        }
    }

    @Override
    public List<FlowTaskOperatorEntity> getParentId(String parentId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getAppendHandleId, parentId);
        queryWrapper.lambda().orderByDesc(FlowTaskOperatorEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public void updateTaskOperatorState(List<String> idAll) {
        if (idAll.size() > 0) {
            UpdateWrapper<FlowTaskOperatorEntity> queryWrapper = new UpdateWrapper<>();
            queryWrapper.lambda().in(FlowTaskOperatorEntity::getId, idAll);
            queryWrapper.lambda().set(FlowTaskOperatorEntity::getState, FlowNodeEnum.Futility.getCode());
            queryWrapper.lambda().set(FlowTaskOperatorEntity::getCompletion, FlowNature.RejectCompletion);
            this.update(queryWrapper);
        }
    }

    @Override
    public List<FlowTaskOperatorEntity> getBatchList() {
        UserInfo userInfo = userProvider.get();
        List<String> userList = flowDelegateService.getUser(userInfo.getUserId()).stream().map(FlowDelegateEntity::getCreatorUserId).collect(Collectors.toList());
        userList.add(userInfo.getUserId());
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(FlowTaskOperatorEntity::getHandleId, userList);
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getCompletion, FlowNature.ProcessCompletion);
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getState, FlowNature.ProcessCompletion);
        queryWrapper.lambda().select(FlowTaskOperatorEntity::getTaskId);
        return list(queryWrapper);
    }

}
