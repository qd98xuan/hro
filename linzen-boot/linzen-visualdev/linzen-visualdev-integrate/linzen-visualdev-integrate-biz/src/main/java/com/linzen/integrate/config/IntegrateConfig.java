package com.linzen.integrate.config;

import com.linzen.integrate.job.IntegrateQueryJobUtil;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrateConfig {

    @Bean
    public JobDetail integrateJobDetail() {
        JobDetail JobDetail = JobBuilder.newJob(IntegrateQueryJobUtil.class)
                .storeDurably() //必须调用该方法，添加任务
                .build();
        return JobDetail;
    }

    @Bean
    public Trigger integrateTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("* * * * * ?");
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(integrateJobDetail())
                .withSchedule(cronScheduleBuilder) //对触发器配置任务
                .build();
        return trigger;
    }

}
