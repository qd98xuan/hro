package com.linzen.service;

import com.linzen.base.UserInfo;
import com.linzen.exception.LoginException;
import com.linzen.model.BaseSystemInfo;
import com.linzen.model.login.vo.PcUserVO;

/**
 * 登陆业务层
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface LoginService {

    /**
     * 租戶登录验证
     *
     * @param userInfo
     * @return userAccount, tenantId, tenandDb
     * @throws LoginException
     */
    UserInfo getTenantAccount(UserInfo userInfo) throws LoginException;

    /**
     * 生成用户登录信息
     * @param userInfo   账户信息
     * @param sysConfigInfo 系统配置
     * @return
     * @throws LoginException
     */
    UserInfo userInfo(UserInfo userInfo, BaseSystemInfo sysConfigInfo) throws LoginException;

    /**
     * 获取用户登陆信息
     *
     * @return
     */
    PcUserVO getCurrentUser(String type, String systemCode);

    /**
     * 修改密码信息发送
     *
     * @return
     */
    void updatePasswordMessage();

    /**
     *
     * @param tenantId
     * @param tenantDb
     * @param isAssignDataSource 是否租户指定数据源
     * @return
     */
    BaseSystemInfo getBaseSystemConfig(String tenantId);



}
