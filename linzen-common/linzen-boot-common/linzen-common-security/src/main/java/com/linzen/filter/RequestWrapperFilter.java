package com.linzen.filter;

import cn.dev33.satoken.context.SaHolder;
import com.linzen.handler.IRestHandler;
import com.linzen.wrapper.MyRequestWrapper;
import com.linzen.wrapper.MyResponseWrapper;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 请求内容
 */
@Slf4j
@Order(-97)
public class RequestWrapperFilter extends OncePerRequestFilter {

    private List<IRestHandler> handlers;
    private SecurityProperties securityProperties;

    public RequestWrapperFilter(List<IRestHandler> handlers, SecurityProperties securityProperties) {
        this.handlers = handlers;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(handlers.isEmpty()){
            filterChain.doFilter(request, response);
        } else {
            String url = SaHolder.getRequest().getRequestPath();
            if(PathUtil.isIgnorePath(url, securityProperties.ignoreRestEncryptUrl)){
                filterChain.doFilter(request, response);
                return;
            }
            MyRequestWrapper myRequest = new MyRequestWrapper(request, handlers);
            MyResponseWrapper wrapResponse = new MyResponseWrapper(response, handlers);
            filterChain.doFilter(myRequest, wrapResponse);
            wrapResponse.doFinal();
        }
    }

}
