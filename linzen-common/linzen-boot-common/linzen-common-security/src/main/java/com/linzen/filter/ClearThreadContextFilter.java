package com.linzen.filter;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.linzen.database.util.IgnoreLogicDeleteHolder;
import com.linzen.database.util.NotTenantPluginHolder;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.util.TenantProvider;
import com.linzen.util.UserProvider;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 线程缓存清理
 */
@Order(-99)
public class ClearThreadContextFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }finally {
            //清除线程缓存
            UserProvider.clearLocalUser();
            TenantProvider.clearBaseSystemIfo();
            TenantDataSourceUtil.clearLocalTenantInfo();
            DynamicDataSourceContextHolder.clear();
            NotTenantPluginHolder.clearNotSwitchFlag();
            IgnoreLogicDeleteHolder.clear();
        }
    }

}
