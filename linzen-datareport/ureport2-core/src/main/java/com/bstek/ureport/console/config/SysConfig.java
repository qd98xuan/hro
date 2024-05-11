package com.bstek.ureport.console.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.bstek.ureport.console.config.SysConfig.PREFIX;

@Getter
@Setter
@Component
@ConfigurationProperties(PREFIX)
public class SysConfig {
    public static final String PREFIX = "config";

    /**
     * 数据库查询时间限制， 避免数据量过大系统假死内存溢出
     */

    private int queryTimeout = 5;

    /**
     * 报表最多处理数据量， 避免数据量过大系统假死内存溢出
     */
    private int maxRows = 100000;
}
