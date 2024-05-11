package com.linzen.util;

import com.google.common.collect.ImmutableMap;
import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.exception.LoginException;
import com.linzen.model.LoginVO;
import com.linzen.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 内部登录、退出用户工具
 */
@Slf4j
@Component
public class AuthUtil {

    public static AuthService authApi;

    @Autowired
    public void setAuthApi(AuthService authApi) {
        AuthUtil.authApi = authApi;
    }

    /**
     * 登录临时用户
     * 此用户已经登录将返回现有用户Token
     * 未登录将直接使用用户ID进行免密登录返回Token
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return
     */
    public static String loginTempUser(String userId, String tenantId){
        return loginTempUser(userId, tenantId, false);
    }

    /**
     * 登录临时用户
     * 此用户已经登录将返回现有用户Token
     * 未登录将直接使用用户ID进行免密登录返回Token
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param limited 是否是限制型临时用户(无法登录主系统前端)
     * @return
     */
    public static String loginTempUser(String userId, String tenantId, boolean limited){
        Map<String, String> loginInfo = ImmutableMap.of(
                "grant_type", "tempuser",
                "token", UserProvider.getInnerAuthToken(),
                "userId", userId,
                "tenantId", Optional.ofNullable(tenantId).orElse(StringUtil.NULLSTR),
                "limited", String.valueOf(limited));
        String errMsg;
        try {
            ServiceResult<LoginVO> result = authApi.login(loginInfo);
            if(Constants.SUCCESS.equals(result.getCode())){
                return result.getData().getToken();
            }
            errMsg = result.getMsg();
        } catch (LoginException e) {
            errMsg = e.getMessage();
        }
        log.error("登录临时用户失败:", errMsg);
        return null;
    }

    /**
     * 踢出用户, 用户将收到Websocket下线通知
     * 执行流程：认证服务退出用户->用户踢出监听->消息服务发送Websocket推送退出消息
     * @param tokens
     */
    public static void kickoutByToken(String... tokens){
        authApi.kickoutByToken(tokens);
    }

    /**
     * 踢出用户, 用户将收到Websocket下线通知
     * 执行流程：认证服务退出用户->用户踢出监听->消息服务发送Websocket推送退出消息
     * @param userId
     */
    public static void kickoutByUserId(String userId){
        String tenantId = TenantHolder.getDatasourceId();
        if(tenantId == null) {
            UserInfo userInfo = UserProvider.getUser();
            if (userInfo.getUserId() == null) {
                throw new RuntimeException("请设置UserInfo");
            }
            tenantId = userInfo.getTenantId();
        }
        authApi.kickoutByUserId(userId, tenantId);
    }
}