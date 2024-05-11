package com.bstek.ureport.console;

import com.bstek.ureport.console.config.DataReportListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * 启动类
 * @author FHNP
 */
@SpringBootApplication(scanBasePackages ={"com.bstek.ureport.console","com.bstek.ureport.utils", "linzen"},exclude = {DataSourceAutoConfiguration.class})
@ImportResource("classpath:ureport.xml")
@MapperScan(basePackages = {"com.bstek.ureport.console.ureport.mapper"})
public class DataReportApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DataReportApplication.class);
        //添加监听器
        springApplication.addListeners(new DataReportListener());
        springApplication.run(args);
    }

    @Bean
    public ServletRegistrationBean buildUreportServlet(){
        return new ServletRegistrationBean(new UReportServlet(), "/*");
    }
}
