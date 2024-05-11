package com.linzen.integrate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.integrate.entity.IntegrateQueueEntity;
import com.linzen.integrate.mapper.IntegrateQueueMapper;
import com.linzen.integrate.service.IntegrateQueueService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IntegrateQueueServiceImpl extends SuperServiceImpl<IntegrateQueueMapper, IntegrateQueueEntity> implements IntegrateQueueService {


    @Override
    public List<IntegrateQueueEntity> getList() {
        QueryWrapper<IntegrateQueueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(IntegrateQueueEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public void create(IntegrateQueueEntity entity) {
        if(StringUtil.isEmpty(entity.getId())){
            entity.setId(RandomUtil.uuId());
        }
        entity.setCreatorTime(new Date());
        this.save(entity);
    }

    @Override
    public Boolean update(String id, IntegrateQueueEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        return this.updateById(entity);
    }

    @Override
    public void delete(IntegrateQueueEntity entity) {
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }

    @Override
    public void delete(String integrateId) {
        QueryWrapper<IntegrateQueueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IntegrateQueueEntity::getIntegrateId,integrateId);
        this.remove(queryWrapper);
    }
}
