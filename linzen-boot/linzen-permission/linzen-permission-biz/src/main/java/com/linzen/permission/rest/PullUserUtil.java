package com.linzen.permission.rest;

import com.linzen.permission.connector.PullUserInfoService;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 推送工具类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
public class PullUserUtil {


    private static PullUserInfoService pullUserInfoService;

    public PullUserUtil(@Autowired(required = false) PullUserInfoService pullUserInfoService){
        PullUserUtil.pullUserInfoService = pullUserInfoService;
    }

    /**
     * 推送到
     *
     * @param userEntity
     * @param method
     * @param tenantId
     */
    public static void syncUser(SysUserEntity userEntity, String method, String tenantId) {
        if (pullUserInfoService != null) {
            Map<String, Object> map = JsonUtil.entityToMap(userEntity);
            pullUserInfoService.syncUserInfo(map, method, tenantId);
        }
    }

}
