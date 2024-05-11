package com.linzen.util;

import com.linzen.permission.entity.SysUserEntity;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public class LoginHolder {


    private static final ThreadLocal<SysUserEntity> USER_CACHE = new ThreadLocal<>();

    /**
     * 获取登录用户的信息
     *
     * @return
     */
    public static SysUserEntity getUserEntity() {
        return USER_CACHE.get();
    }

    /**
     * 设置登录用户信息
     *
     * @param userEntity
     */
    public static void setUserEntity(SysUserEntity userEntity) {
        USER_CACHE.set(userEntity);
    }

    /**
     * 删除登录用户信息
     */
    public static void clearUserEntity() {
        USER_CACHE.remove();
    }
}
