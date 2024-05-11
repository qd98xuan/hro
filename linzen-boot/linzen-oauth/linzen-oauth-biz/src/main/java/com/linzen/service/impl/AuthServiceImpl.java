package com.linzen.service.impl;

import com.linzen.base.ServiceResult;
import com.linzen.base.UserInfo;
import com.linzen.exception.LoginException;
import com.linzen.granter.TokenGranter;
import com.linzen.granter.TokenGranterBuilder;
import com.linzen.model.LoginVO;
import com.linzen.service.AuthService;
import com.linzen.service.LogService;
import com.linzen.util.LoginHolder;
import com.linzen.util.StringUtil;
import com.linzen.util.TenantProvider;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 登录与退出服务 其他服务调用
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TokenGranterBuilder tokenGranterBuilder;
    @Autowired
    private LogService logService;

    /**
     * 登录
     *
     * @param parameters {grant_type}
     * @return
     * @throws LoginException
     */
    @Override
    public ServiceResult<LoginVO> login(Map<String, String> parameters) throws LoginException {
        long millis = System.currentTimeMillis();
        TokenGranter tokenGranter = tokenGranterBuilder.getGranter(parameters.getOrDefault("grant_type", ""));
        ServiceResult<LoginVO> result;
        UserInfo userInfo = new UserInfo();
        try {
            String account = parameters.get("account");
            userInfo.setUserAccount(account);
            UserProvider.setLocalLoginUser(userInfo);
            result = tokenGranter.granter(parameters);
            //写入日志
            if (StringUtil.isEmpty(parameters.get("userId"))) {
                logService.writeLogAsync(userInfo.getUserId(), userInfo.getUserName() + "/" + userInfo.getUserAccount(), "登录成功", (System.currentTimeMillis() - millis));
            }
        } catch (Exception e) {
            if (!(e instanceof LoginException)) {
                String msg = e.getMessage();
                if (msg == null) {
                    msg = "登录异常";
                }
                log.error("登录异常 {}", e.getMessage(), e);
                throw new LoginException(msg);
            }
            String userName = StringUtil.isNotEmpty(userInfo.getUserName()) ? userInfo.getUserName() + "/" + userInfo.getUserAccount() : userInfo.getUserAccount();
            logService.writeLogAsync(userInfo.getUserId(), userName, e.getMessage(), userInfo, 0, null, (System.currentTimeMillis() - millis));
            throw e;
        } finally {
            LoginHolder.clearUserEntity();
            TenantProvider.clearBaseSystemIfo();
        }
        return result;
    }


    /**
     * 踢出用户, 用户将收到Websocket下线通知
     * 执行流程：认证服务退出用户->用户踢出监听->消息服务发送Websocket推送退出消息
     *
     * @param tokens
     */
    @Override
    public ServiceResult kickoutByToken(String... tokens) {
        UserProvider.kickoutByToken(tokens);
        return ServiceResult.success();
    }

    /**
     * 踢出用户, 用户将收到Websocket下线通知
     * 执行流程：认证服务退出用户->用户踢出监听->消息服务发送Websocket推送退出消息
     *
     * @param userId
     * @param tenantId
     */
    @Override
    public ServiceResult kickoutByUserId(String userId, String tenantId) {
        UserProvider.kickoutByUserId(userId, tenantId);
        return ServiceResult.success();
    }
}
