package com.linzen.message.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.message.entity.WechatUserEntity;
import com.linzen.message.mapper.WechatUserMapper;
import com.linzen.message.service.WechatUserService;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息模板（新）
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class WechatUserServiceImpl extends SuperServiceImpl<WechatUserMapper, WechatUserEntity> implements WechatUserService {



    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public WechatUserEntity getInfoByGzhId(String userId,String gzhId){
        QueryWrapper<WechatUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WechatUserEntity::getUserId,userId);
        queryWrapper.lambda().eq(WechatUserEntity::getGzhId,gzhId);
        queryWrapper.lambda().eq(WechatUserEntity::getCloseMark,1);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(WechatUserEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, WechatUserEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(WechatUserEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}