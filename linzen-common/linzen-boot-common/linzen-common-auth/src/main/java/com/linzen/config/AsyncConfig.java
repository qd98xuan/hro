package com.linzen.config;

import com.linzen.base.UserInfo;
import com.linzen.model.tenant.TenantVO;
import com.linzen.util.TenantHolder;
import com.linzen.util.UserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 提供一个全局的Spring线程池对象
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Configuration
@EnableAsync(proxyTargetClass = true)
@AllArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    @Primary
    @Bean("threadPoolTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置线程池核心容量
        threadPoolTaskExecutor.setCorePoolSize(10);
        // 设置线程池最大容量
        threadPoolTaskExecutor.setMaxPoolSize(50);
        // 设置任务队列长度
        threadPoolTaskExecutor.setQueueCapacity(2000);
        // 设置线程超时时间
        threadPoolTaskExecutor.setKeepAliveSeconds(30);
        // 设置线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("sysTaskExecutor");
        // 设置任务丢弃后的处理策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setTaskDecorator( r ->{
            //实现线程上下文穿透， 异步线程内无法获取之前的Request，租户信息等， 如有新的上下文对象在此处添加
            //此方法在请求结束后在无法获取request, 下方完整异步Servlet请求
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            TenantVO tenantVO = TenantHolder.getLocalTenantCache();
            UserInfo userInfo = UserProvider.getUser();
            return () -> {
                try {
                    if(attributes!= null) {
                        RequestContextHolder.setRequestAttributes(attributes);
                    }
                    if(tenantVO != null){
                        TenantHolder.setLocalTenantCache(tenantVO);
                    }
                    UserProvider.setLocalLoginUser(userInfo);
                    r.run();
                } finally {
                    UserProvider.clearLocalUser();
                    RequestContextHolder.resetRequestAttributes();
                    TenantHolder.clearLocalTenantCache();
                }
            };
        });
        return threadPoolTaskExecutor;
    }


    @Bean("defaultExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutorDef(@Qualifier("threadPoolTaskExecutor") Executor executor) {
        return (ThreadPoolTaskExecutor) executor;
    }

}
