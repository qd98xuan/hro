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
 * 默认使用用户名获取用户信息
 */
@Service(AuthConsts.USER_ACCOUNT)
public class UserDetailsByUserAccountServiceImpl implements UserDetailService {

    @Autowired
    private UserService userService;

    @Override
    public SysUserEntity loadUserEntity(UserInfo userInfo) throws LoginException {
        SysUserEntity userEntity = userService.getUserByAccount(userInfo.getUserAccount());
        if (userEntity == null) {
            throw new LoginException(MsgCode.LOG101.get());
        }
        return userEntity;
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
