package com.linzen.database.config;


import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.exception.DataBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Configuration
public class DruidConfig {

    @Autowired
    DataSourceUtil dataSourceUtil;

    public DataSource druid() throws DataBaseException {
        return ConnUtil.getDruidDataSource(dataSourceUtil);
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> params = new HashMap<>(4);
        params.put("loginUsername", "linzen");
        params.put("loginPassword", "123456");
        //默认就是允许所有访问
        params.put("allow", "");
        servletRegistrationBean.setInitParameters(params);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> webStatFilter() {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>(16);
        initParams.put("exclusions", "*.js,*.css,/druid/*");
        filter.setInitParameters(initParams);
        filter.setUrlPatterns(Collections.singletonList("/*"));

        return filter;
    }
}
