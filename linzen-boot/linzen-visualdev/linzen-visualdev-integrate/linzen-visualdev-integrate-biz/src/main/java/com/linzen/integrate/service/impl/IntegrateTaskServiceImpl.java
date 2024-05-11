package com.linzen.integrate.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.integrate.entity.IntegrateTaskEntity;
import com.linzen.integrate.mapper.IntegrateTaskMapper;
import com.linzen.integrate.model.integrate.IntegratePageModel;
import com.linzen.integrate.service.IntegrateNodeService;
import com.linzen.integrate.service.IntegrateTaskService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IntegrateTaskServiceImpl extends SuperServiceImpl<IntegrateTaskMapper, IntegrateTaskEntity> implements IntegrateTaskService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private IntegrateNodeService integrateNodeService;

    @Override
    public List<IntegrateTaskEntity> getList(IntegratePageModel pagination) {
        QueryWrapper<IntegrateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IntegrateTaskEntity::getIntegrateId,pagination.getIntegrateId());
        //日期")
        String startTime = pagination.getStartTime() != null ? pagination.getStartTime() : null;
        String endTime = pagination.getEndTime() != null ? pagination.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = new Date(Long.parseLong(startTime));
            Date endTimes = new Date(Long.parseLong(endTime));
            queryWrapper.lambda().ge(IntegrateTaskEntity::getExecutionTime, startTimes).le(IntegrateTaskEntity::getExecutionTime, endTimes);
        }
        if (ObjectUtil.isNotEmpty(pagination.getResultType())) {
            queryWrapper.lambda().eq(IntegrateTaskEntity::getResultType, pagination.getResultType());
        }
        queryWrapper.lambda().orderByDesc(IntegrateTaskEntity::getExecutionTime);
        Page<IntegrateTaskEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<IntegrateTaskEntity> userPage = page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public List<IntegrateTaskEntity> getList(List<String> id) {
        List<IntegrateTaskEntity> list = new ArrayList<>();
        QueryWrapper<IntegrateTaskEntity> queryWrapper = new QueryWrapper<>();
        if (id.size() > 0) {
            queryWrapper.lambda().in(IntegrateTaskEntity::getIntegrateId, id);
            list.addAll(this.list(queryWrapper));
        }
        return list;
    }

    @Override
    public IntegrateTaskEntity getInfo(String id) {
        QueryWrapper<IntegrateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IntegrateTaskEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(IntegrateTaskEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setEnabledMark(0);
        this.save(entity);
    }

    @Override
    public Boolean update(String id, IntegrateTaskEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(IntegrateTaskEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
            integrateNodeService.delete(entity.getId());
        }
    }

}
