package com.linzen.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 超时设置定时器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class AutoApproveJob extends QuartzJobBean {

    public static final String autoApprove = "idgenerator_AutoApprove";

    @Autowired
    private WorkTimeoutJobUtil workTimeoutJobUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        workTimeoutJobUtil.approveModel(redisTemplate);
    }

}
