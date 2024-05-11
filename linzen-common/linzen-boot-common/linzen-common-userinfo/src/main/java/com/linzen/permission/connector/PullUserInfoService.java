package com.linzen.permission.connector;

import java.util.Map;

/**
 * 用户推送
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface PullUserInfoService {

    /**
     * 同步数据到本地数据库
     *
     * @param userEntity
     * @param method
     * @param tenantId
     */
    void syncUserInfo(Map<String, Object> userEntity, String method, String tenantId);

}
