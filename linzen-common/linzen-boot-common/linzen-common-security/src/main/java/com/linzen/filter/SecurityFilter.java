package com.linzen.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.consts.AuthConsts;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.function.Predicate;

@Slf4j
@Order(-98)
public class SecurityFilter extends SaServletFilter {


    private SecurityProperties securityProperties;

    private ConfigValueUtil configValueUtil;

    public SecurityFilter(SecurityProperties securityProperties, ConfigValueUtil configValueUtil) {
        this.securityProperties = securityProperties;
        this.configValueUtil = configValueUtil;
        setAuth(o -> {
            initAuthenticationInfo();
        });
        setBeforeAuth(o -> {
            checkRequestOrigin.test(null);
        });
    }

    /**
     * 请求来源验证
     */
    private Predicate<Object> checkRequestOrigin = t -> true;


    protected void initAuthenticationInfo(){
        //执行Token续期, 存用户信息至本地缓存后续无需重新获取
        UserProvider.renewTimeout();
        //设置租户信息
        if(configValueUtil.isMultiTenancy()) {
            UserInfo userInfo = UserProvider.getUser();
            if (StringUtil.isNotEmpty(userInfo.getTenantId())) {
                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
            }
        }
    }

    /**
     * 微服务才验证请求来源
     */
    @ConditionalOnClass(name = "org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient")
    @PostConstruct
    public void onCloudCheckRequestOrigin(){
        checkRequestOrigin = t -> {
            //验证请求来源, 网关, Feign
            if(securityProperties.isEnableInnerAuth()) {
                SaRouter.match("/favicon.ico").stop();
                SaRouter.match("/**").match(r->{
                    String innerToken = SaHolder.getRequest().getHeader(AuthConsts.INNER_TOKEN_KEY);
                    String innerGatewayToken = SaHolder.getRequest().getHeader(AuthConsts.INNER_GATEWAY_TOKEN_KEY);
                    if(!UserProvider.isValidInnerToken(innerGatewayToken) && !UserProvider.isValidInnerToken(innerToken)){
                        log.error("非法请求: {}, {}", SaHolder.getRequest().getRequestPath(), innerToken);
                        return true;
                    }
                    return false;
                }).back("非法请求, 缺少认证信息");
            }
            return true;
        };
    }
}
