package com.linzen.permission.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.permission.entity.SysUserOldPasswordEntity;
import com.linzen.permission.mapper.UserOldPasswordMapper;
import com.linzen.permission.service.UserOldPasswordService;
import com.linzen.permission.service.UserRelationService;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
@DSTransactional
public class UserOldPasswordServiceImpl extends SuperServiceImpl<UserOldPasswordMapper, SysUserOldPasswordEntity> implements UserOldPasswordService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserRelationService userRelationService;

    @Override
    public List<SysUserOldPasswordEntity> getList(String userId) {
        QueryWrapper<SysUserOldPasswordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserOldPasswordEntity::getUserId,userId);
        queryWrapper.lambda().orderByDesc(SysUserOldPasswordEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public Boolean create(SysUserOldPasswordEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorTime(DateUtil.getNowDate());
        this.save(entity);
        return true;
    }

}
