package com.linzen.service.impl;

import com.linzen.base.UserInfo;
import com.linzen.constant.MsgCode;
import com.linzen.consts.AuthConsts;
import com.linzen.exception.LoginException;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.service.UserService;
import com.linzen.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 使用用户ID获取用户信息
 */
@Service(AuthConsts.USERDETAIL_USER_ID)
public class UserDetailsByUserIdServiceImpl implements UserDetailService {

    private static final Integer ORDER = 1;

    @Autowired
    private UserService userService;

    @Override
    public SysUserEntity loadUserEntity(UserInfo userInfo) throws LoginException {
        SysUserEntity userEntity = userService.getInfo(userInfo.getUserId());
        if (userEntity == null) {
            throw new LoginException(MsgCode.LOG101.get());
        }
        return userEntity;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
