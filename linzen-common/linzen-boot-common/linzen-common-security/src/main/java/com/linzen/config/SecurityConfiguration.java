package com.linzen.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaStrategy;
import com.linzen.filter.SecurityFilter;
import com.linzen.handler.IRestHandler;
import com.linzen.base.UserInfo;
import com.linzen.consts.AuthConsts;
import com.linzen.filter.ClearThreadContextFilter;
import com.linzen.filter.RequestWrapperFilter;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration implements WebMvcConfigurer {


    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        initSaInterfaceAuth(registry);
    }

    /**
     * 请求封装过滤器
     */
    @Bean("myRequestWrapperFilter")
    @ConditionalOnMissingBean(name = "myRequestWrapperFilter")
    public Filter getRequestWrapperFilter(List<IRestHandler> handlers){
        return new RequestWrapperFilter(handlers, securityProperties);
    }

    /**
     * 线程变量清除过滤器
     */
    @Bean("myClearThreadContextFilter")
    @ConditionalOnMissingBean(name = "myClearThreadContextFilter")
    public Filter getClearThreadContextFilter(){
        return new ClearThreadContextFilter();
    }

    /**
     * 来源验证、用户、租户设置过滤器
     */
    @Bean("mySecurityFilter")
    @ConditionalOnMissingBean(name = "mySecurityFilter")
    public Filter getSecurityFilter(SecurityProperties securityProperties, ConfigValueUtil configValueUtil){
        return new SecurityFilter(securityProperties, configValueUtil).addInclude("/**");
    }


    protected void initSaInterfaceAuth(InterceptorRegistry registry){
        if(securityProperties.isEnablePreAuth()) {
            // 开启接口请求权限控制
            registry.addInterceptor(new SaInterceptor((handler) -> {

            }).isAnnotation(securityProperties.isEnablePreAuth())).addPathPatterns("/**");
        }
        //接口鉴权忽略管理员、内部请求
        BiFunction<List<String>, String, Boolean> oldCheckFunc = SaStrategy.me.hasElement;
        SaStrategy.me.hasElement = (list, element) -> {
            //启用之后才验证
            if (securityProperties.isEnablePreAuth()) {
                UserInfo userInfo = UserProvider.getUser();
                //未获取到用户信息返回false
                if (StringUtil.isEmpty(userInfo.getUserId())) {
                    return false;
                }
                //管理员返回true
                if (userInfo.getIsAdministrator()) {
                    return true;
                }
                boolean result = oldCheckFunc.apply(list, element);
                //如果鉴权失败， 检测是否来自内部请求
                if (!result) {
                    String innerToken = SaHolder.getRequest().getHeader(AuthConsts.INNER_TOKEN_KEY);
                    //来自内部请求(非网关) 无需鉴权
                    if (UserProvider.isValidInnerToken(innerToken)) {
                        result = true;
                    }
                }
                return result;
            }
            return true;
        };
    }



}
