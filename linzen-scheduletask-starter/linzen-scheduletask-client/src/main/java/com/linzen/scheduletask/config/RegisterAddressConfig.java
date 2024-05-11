package com.linzen.scheduletask.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = RegisterAddressConfig.PREFIX)
@Component
public class RegisterAddressConfig {

    public static final String PREFIX = "xxl.admin.register";

    /**
     * 获取执行器列表
     */
    private String handle_query_address;

    /**
     * 获取任务详情
     */
    private String job_info_address;

    /**
     * 通过任务id获取日志列表
     */
    private String log_query_address;

    /**
     * 获取分页数据
     */
    private String task_list_address;

    /**
     * 通过任务id获取任务详情
     */
    private String task_info_address;

    /**
     * 保存任务调度
     */
    private String task_save_address;

    /**
     * 修改任务调度
     */
    private String task_update_address;

    /**
     * 删除任务调度
     */
    private String task_remove_address;

    /**
     * 启动或停止任务
     */
    private String task_startOrRemove_address;

}
