package com.linzen.service;

import com.linzen.base.UserInfo;
import com.linzen.exception.LoginException;
import org.springframework.core.Ordered;

public interface UserDetailService extends Ordered {

    static final String USER_DETAIL_PREFIX = "USERDETAIL_";

    /**
     * 获取用户信息
     * @param userInfo
     * @return UserEntity
     * @param <T>
     */
    <T> T loadUserEntity(UserInfo userInfo) throws LoginException;

}
