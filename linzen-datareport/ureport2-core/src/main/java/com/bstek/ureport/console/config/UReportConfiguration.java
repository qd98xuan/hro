package com.bstek.ureport.console.config;


import com.linzen.config.ConfigValueUtil;
import com.linzen.properties.SecurityProperties;
import com.linzen.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration

public class UReportConfiguration {

    @Bean("mySecurityFilter")
    public Filter getUReportSecurityFilter(SecurityProperties securityProperties, ConfigValueUtil configValueUtil, RedisUtil redisUtil){
        return new UReportSecurityFilter(securityProperties, configValueUtil, redisUtil).addInclude("/**");
    }

}
