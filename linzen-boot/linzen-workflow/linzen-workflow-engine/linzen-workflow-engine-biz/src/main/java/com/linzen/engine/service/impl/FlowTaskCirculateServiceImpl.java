package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowTaskCirculateEntity;
import com.linzen.engine.mapper.FlowTaskCirculateMapper;
import com.linzen.engine.service.FlowTaskCirculateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程传阅
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowTaskCirculateServiceImpl extends SuperServiceImpl<FlowTaskCirculateMapper, FlowTaskCirculateEntity> implements FlowTaskCirculateService {

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(List<FlowTaskCirculateEntity> entitys) {
        for (FlowTaskCirculateEntity entity : entitys) {
            this.save(entity);
        }
    }

    @Override
    public List<FlowTaskCirculateEntity> getList(String taskId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskId, taskId);
        return this.list(queryWrapper);
    }
}
