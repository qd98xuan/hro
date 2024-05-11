package com.linzen.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.linzen.base.Pagination;
import com.linzen.base.entity.DataInterfaceEntity;
import com.linzen.base.entity.DataInterfaceLogEntity;
import com.linzen.base.mapper.DataInterfaceLogMapper;
import com.linzen.base.model.InterfaceOauth.PaginationIntrfaceLog;
import com.linzen.base.service.DataInterfaceLogService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class DataInterfaceLogServiceImpl extends SuperServiceImpl<DataInterfaceLogMapper, DataInterfaceLogEntity> implements DataInterfaceLogService {
    @Autowired
    private UserProvider userProvider;

    @Override
    public void create(String dateInterfaceId, Integer invokWasteTime) {
        DataInterfaceLogEntity entity = new DataInterfaceLogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setInvokTime(DateUtil.getNowDate());
        entity.setUserId(userProvider.get().getUserId());
        entity.setInvokId(dateInterfaceId);
        entity.setInvokIp(IpUtil.getIpAddr());
        entity.setInvokType("GET");
        entity.setInvokDevice(ServletUtil.getUserAgent());
        entity.setInvokWasteTime(invokWasteTime);
        this.save(entity);
    }
    @Override
    public void create(String dateInterfaceId, Integer invokWasteTime,String appId,String invokType) {
        DataInterfaceLogEntity entity = new DataInterfaceLogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setInvokTime(DateUtil.getNowDate());
        entity.setUserId(userProvider.get().getUserId());
        entity.setInvokId(dateInterfaceId);
        entity.setInvokIp(IpUtil.getIpAddr());
        entity.setInvokType(invokType);
        entity.setInvokDevice(ServletUtil.getUserAgent());
        entity.setInvokWasteTime(invokWasteTime);
        entity.setOauthAppId(appId);
        this.save(entity);
    }

    @Override
    public List<DataInterfaceLogEntity> getList(String invokId, Pagination pagination) {
        QueryWrapper<DataInterfaceLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceLogEntity::getInvokId, invokId).orderByDesc(DataInterfaceLogEntity::getInvokTime);
        if (StringUtil.isNotEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(DataInterfaceLogEntity::getUserId, pagination.getKeyword())
                    .or().like(DataInterfaceLogEntity::getInvokIp, pagination.getKeyword())
                    .or().like(DataInterfaceLogEntity::getInvokDevice, pagination.getKeyword())
                    .or().like(DataInterfaceLogEntity::getInvokType, pagination.getKeyword())
            );
        }
        // 排序
        queryWrapper.lambda().orderByDesc(DataInterfaceLogEntity::getInvokTime);
        Page<DataInterfaceLogEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceLogEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<DataInterfaceLogEntity> getListByIds(String appId,List<String> invokIds, PaginationIntrfaceLog pagination) {
        MPJLambdaWrapper<DataInterfaceLogEntity> queryWrapper = JoinWrappers
                .lambda(DataInterfaceLogEntity.class)
                .leftJoin(DataInterfaceEntity.class,DataInterfaceEntity::getId,DataInterfaceLogEntity::getInvokId);
        queryWrapper.eq(DataInterfaceLogEntity::getOauthAppId,appId);
        queryWrapper.in(DataInterfaceLogEntity::getInvokId, invokIds);

        if (StringUtil.isNotEmpty(pagination.getKeyword())){
            queryWrapper.and(
                    t->t.like(DataInterfaceEntity::getEnCode, pagination.getKeyword())
                            .or().like(DataInterfaceEntity::getFullName, pagination.getKeyword())
            );
        }
        //日期")
        if (ObjectUtil.isNotEmpty(pagination.getStartTime()) && ObjectUtil.isNotEmpty(pagination.getEndTime())) {
            queryWrapper.between(DataInterfaceLogEntity::getInvokTime, new Date(pagination.getStartTime()), new Date(pagination.getEndTime()));
        }
        // 排序
        queryWrapper.orderByDesc(DataInterfaceLogEntity::getInvokTime);
        Page<DataInterfaceLogEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceLogEntity> iPage = this.selectJoinListPage(page, DataInterfaceLogEntity.class,queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

}
