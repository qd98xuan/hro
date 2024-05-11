package com.linzen;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.linzen"}, exclude = {DataSourceAutoConfiguration.class})
@EnableFileStorage
public class LinzenAdminApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LinzenAdminApplication.class);
        springApplication.run(args);
        System.out.println("LinzenAdminApplication启动完成");
    }
}
