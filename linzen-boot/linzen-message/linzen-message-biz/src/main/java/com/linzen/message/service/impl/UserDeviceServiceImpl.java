package com.linzen.message.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.UserDeviceEntity;
import com.linzen.message.mapper.UserDeviceMapper;
import com.linzen.message.service.UserDeviceService;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class UserDeviceServiceImpl extends SuperServiceImpl<UserDeviceMapper, UserDeviceEntity> implements UserDeviceService {



    @Autowired
    private UserProvider userProvider;


    @Override
    public UserDeviceEntity getInfoByUserId(String userId){
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getUserId,userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<String> getCidList(String userId){
        List<String> cidList = new ArrayList<>();
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getUserId,userId);
        if(this.list(queryWrapper) != null && this.list(queryWrapper).size()>0) {
            cidList = this.list(queryWrapper).stream().map(t -> t.getClientId()).distinct().collect(Collectors.toList());
        }
        return cidList;
    }

    @Override
    public UserDeviceEntity getInfoByClientId(String clientId){
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getClientId,clientId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(UserDeviceEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, UserDeviceEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(UserDeviceEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}