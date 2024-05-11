package com.xxl.job.admin.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xxl.job.admin.service.HandlerNameService;
import com.linzen.util.context.SpringContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
public class XxlJobListener implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        HandlerNameService handlerNameService = SpringContext.getBean(HandlerNameService.class);
        handlerNameService.removeAll();
    }

    @Bean
    public MybatisPlusInterceptor pageHelper() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}