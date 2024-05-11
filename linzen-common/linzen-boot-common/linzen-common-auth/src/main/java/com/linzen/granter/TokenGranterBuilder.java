package com.linzen.granter;

import com.linzen.base.UserInfo;
import com.linzen.exception.LoginException;
import com.linzen.config.OauthConfigration;
import com.linzen.consts.AuthConsts;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Component
public class TokenGranterBuilder {

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private OauthConfigration oauthConfig;

    private final Map<String, TokenGranter> granterPool = new ConcurrentHashMap<>();

    public TokenGranterBuilder(Map<String, TokenGranter> granterPool) {
        this.granterPool.putAll(granterPool);
    }

    /**
     * 获取TokenGranter 根据配置文件查看是否是单点登录
     *
     * @param grantType 授权类型
     * @return ITokenGranter
     */
    public TokenGranter getGranter(String grantType) throws LoginException {
        TokenGranter tokenGranter = null;
        if (!oauthConfig.getSsoEnabled()) {
            tokenGranter = granterPool.get(grantType);
        }
        if (tokenGranter == null) {
            //URL匹配
            for (TokenGranter value : granterPool.values()) {
                if (value.requiresAuthentication()) {
                    tokenGranter = value;
                    break;
                }
            }
        }
        if (tokenGranter == null) {
            if (oauthConfig.getSsoEnabled()) {
                throw new LoginException("已开启单点登录, 不支持此登录方式");
            } else {
                throw new LoginException("不支持此登录方式");
            }
        }
        return tokenGranter;
    }


    /**
     * 获取当前登录用户的TokenGranter
     *
     * @return
     * @throws LoginException
     */
    public TokenGranter getGranterByLogin(String grandType) {
        if (grandType == null || grandType.isEmpty()) {
            UserInfo userInfo = userProvider.get();
            if (userInfo.getGrantType() != null) {
                grandType = userInfo.getGrantType();
            } else {
                grandType = AuthConsts.GRANT_TYPE_PASSWORD;
            }
        }
        return granterPool.get(grandType);
    }


}
