package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowUserEntity;
import com.linzen.engine.mapper.FlowUserMapper;
import com.linzen.engine.service.FlowUserService;
import com.linzen.util.RandomUtil;
import org.springframework.stereotype.Service;

/**
 * 流程发起用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Service
public class FlowUserServiceImpl extends SuperServiceImpl<FlowUserMapper, FlowUserEntity> implements FlowUserService {

    @Override
    public FlowUserEntity getInfo(String id) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public FlowUserEntity getTaskInfo(String id) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getTaskId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(FlowUserEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowUserEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(FlowUserEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }
}
