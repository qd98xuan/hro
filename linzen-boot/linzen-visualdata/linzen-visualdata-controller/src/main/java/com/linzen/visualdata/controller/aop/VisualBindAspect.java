package com.linzen.visualdata.controller.aop;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */

import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.util.ServletUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class VisualBindAspect {


    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Pointcut("execution(* com.linzen.*.controller.VisualCategoryController.list(..))  || execution(* com.linzen.*.controller.VisualMapController.dataInfo(..))")
    public void bindDataSource() {

    }

    /**
     * NoDataSourceBind 不需要绑定数据库的注解
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("bindDataSource()")
    public Object doAroundService(ProceedingJoinPoint pjp) throws Throwable {
        if (configValueUtil.isMultiTenancy()) {
            String jwtToken = ServletUtil.getRequest().getHeader("Authorization");
            if(StringUtil.isEmpty(jwtToken)){
                //兼容旧版大屏前端
                jwtToken = ServletUtil.getRequest().getParameter("token");
            }
            UserInfo userInfo = userProvider.get(jwtToken);
            if(userInfo.getTenantId() == null){
                throw new RuntimeException("租户信息为空: " + jwtToken);
            }
            //设置租户
            TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
        }
        Object obj = pjp.proceed();
        return obj;
    }
}

