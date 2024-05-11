package com.linzen.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import com.linzen.consts.AuthConsts;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * OAuth自动装配
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Configuration
@EnableConfigurationProperties(OauthConfigration.class)
public class AuthAutoConfigration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "oauth.login")
    public SaTokenConfig getLinzenTokenConfig() {
        return new TokenConfigration();
    }

    @Primary
    @Bean(AuthConsts.ACCOUNT_LOGIC_BEAN_DEFAULT)
    public StpLogic getLinzenTokenJwtLogic() {
        return new StpLogicJwtForSimple(AuthConsts.ACCOUNT_TYPE_DEFAULT);
    }

    @Bean(AuthConsts.ACCOUNT_LOGIC_BEAN_TENANT)
    public StpLogic getLinzenTenantTokenJwtLogic() {
        return new StpLogicJwtForSimple(AuthConsts.ACCOUNT_TYPE_TENANT);
    }
}
