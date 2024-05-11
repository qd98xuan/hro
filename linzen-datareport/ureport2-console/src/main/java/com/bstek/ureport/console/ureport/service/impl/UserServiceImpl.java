package com.bstek.ureport.console.ureport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bstek.ureport.console.ureport.entity.UserEntity;
import com.bstek.ureport.console.ureport.mapper.UserMapper;
import com.bstek.ureport.console.ureport.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Override
    public UserEntity getInfo(String id) {
        if(id == null){
            return null;
        }
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getId, id);
        return this.getOne(queryWrapper);
    }
}
