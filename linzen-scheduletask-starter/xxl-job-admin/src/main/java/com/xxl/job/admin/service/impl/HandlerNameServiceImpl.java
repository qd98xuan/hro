package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.mapper.HandlerNameMapper;
import com.xxl.job.admin.service.HandlerNameService;
import com.linzen.scheduletask.entity.HandlerNameEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务名称业务实现类
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class HandlerNameServiceImpl extends ServiceImpl<HandlerNameMapper, HandlerNameEntity> implements HandlerNameService {

    @Override
    public boolean create(HandlerNameEntity entity) {
        entity.setId(entity.getHandlerName());
        return this.save(entity);
    }

    @Override
    public boolean delete(HandlerNameEntity entity) {
        QueryWrapper<HandlerNameEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(HandlerNameEntity::getExecutor, entity.getExecutor()).eq(HandlerNameEntity::getHandlerName, entity.getHandlerName());
        return this.remove(queryWrapper);
    }

    @Override
    public List<HandlerNameEntity> queryList() {
        QueryWrapper<HandlerNameEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().groupBy(HandlerNameEntity::getHandlerName);
        return this.list();
    }

    @Override
    public boolean removeAll() {
        return this.remove(new QueryWrapper<HandlerNameEntity>());
    }

    @Override
    public HandlerNameEntity getInfo(String localHostTaskId) {
        QueryWrapper<HandlerNameEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(HandlerNameEntity::getId, localHostTaskId);
        return this.getOne(queryWrapper);
    }
}
