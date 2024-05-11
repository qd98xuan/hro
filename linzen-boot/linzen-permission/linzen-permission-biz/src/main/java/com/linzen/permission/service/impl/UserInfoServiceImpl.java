package com.linzen.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.permission.connector.UserInfoService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.Md5Util;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户信息保存
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserService userService;

    @Override
    public Boolean create(Map<String, Object> map) {
        SysUserEntity entity = BeanUtil.toBean(map, SysUserEntity.class);
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserEntity::getAccount, entity.getAccount());
        SysUserEntity entity1 = userService.getOne(queryWrapper);
        if (entity1 != null) {
            entity.setId(entity1.getId());
            return userService.updateById(entity);
        } else {
            if (StringUtil.isEmpty(entity.getId())) {
                String userId = RandomUtil.uuId();
                entity.setId(userId);
            }
            entity.setSecretkey(RandomUtil.uuId());
            entity.setPassword(Md5Util.getStringMd5(entity.getPassword().toLowerCase() + entity.getSecretkey().toLowerCase()));
            entity.setIsAdministrator(0);
            return userService.save(entity);
        }
    }

    @Override
    public Boolean update(Map<String, Object> map) {
        return create(map);
    }

    @Override
    public Boolean delete(Map<String, Object> map) {
        SysUserEntity entity = BeanUtil.toBean(map, SysUserEntity.class);
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserEntity::getAccount, entity.getAccount());
        SysUserEntity entity1 = userService.getOne(queryWrapper);
        if (entity1 != null) {
            entity.setId(entity1.getId());
        }
        return userService.removeById(entity.getId());
    }

    @Override
    public Map<String, Object> getInfo(String id) {
        SysUserEntity entity = userService.getInfo(id);
        return JsonUtil.entityToMap(entity);
    }
}
