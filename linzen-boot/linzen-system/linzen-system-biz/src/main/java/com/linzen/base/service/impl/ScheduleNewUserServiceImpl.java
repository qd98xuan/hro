package com.linzen.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.ScheduleNewUserEntity;
import com.linzen.base.mapper.ScheduleNewUserMapper;
import com.linzen.base.service.ScheduleNewUserService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日程
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Service
public class ScheduleNewUserServiceImpl extends SuperServiceImpl<ScheduleNewUserMapper, ScheduleNewUserEntity> implements ScheduleNewUserService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ScheduleNewUserEntity> getList(String scheduleId,Integer type) {
        QueryWrapper<ScheduleNewUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ScheduleNewUserEntity::getScheduleId, scheduleId);
        if(ObjectUtil.isNotEmpty(type)){
            queryWrapper.lambda().eq(ScheduleNewUserEntity::getType, type);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<ScheduleNewUserEntity> getList() {
        QueryWrapper<ScheduleNewUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ScheduleNewUserEntity::getToUserId, userProvider.get().getUserId());
        queryWrapper.lambda().eq(ScheduleNewUserEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }

    @Override
    public void create(ScheduleNewUserEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    @DSTransactional
    public void deleteByScheduleId(List<String> scheduleIdList) {
        if (scheduleIdList.size() > 0) {
            QueryWrapper<ScheduleNewUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(ScheduleNewUserEntity::getScheduleId, scheduleIdList);
            this.remove(queryWrapper);
        }
    }

    @Override
    public void deleteByUserId(List<String> scheduleIdList) {
        UserInfo userInfo = userProvider.get();
        if (scheduleIdList.size() > 0) {
            UpdateWrapper<ScheduleNewUserEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(ScheduleNewUserEntity::getToUserId, userInfo.getUserId());
            updateWrapper.lambda().in(ScheduleNewUserEntity::getScheduleId, scheduleIdList);
            updateWrapper.lambda().set(ScheduleNewUserEntity::getEnabledMark, 0);
            this.update(updateWrapper);
        }
    }
}
