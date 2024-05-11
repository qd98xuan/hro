package com.linzen.base.service;

import com.linzen.base.entity.DataInterfaceUserEntity;
import com.linzen.base.model.InterfaceOauth.InterfaceUserForm;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface DataInterfaceUserService extends SuperService<DataInterfaceUserEntity> {

    /**
     * 授权用户
     *
     * @param interfaceUserForm
     */
    void saveUserList(InterfaceUserForm interfaceUserForm);

    /**
     * 根据认证接口id查询授权用户列表
     *
     * @param oauthId
     * @return
     */
    List<DataInterfaceUserEntity> select(String oauthId);

    /**
     * 通过用户密钥获取用户token
     *
     * @param oauthId
     * @param userKey
     * @return
     */
    String getInterfaceUserToken(String tenantId, String oauthId, String userKey);

}
