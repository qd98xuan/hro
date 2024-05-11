package com.linzen.integrate.job;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.integrate.entity.IntegrateQueueEntity;
import com.linzen.integrate.model.nodeJson.IntegrateModel;
import com.linzen.integrate.service.IntegrateQueueService;
import com.linzen.integrate.util.IntegrateUtil;
import com.linzen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@DisallowConcurrentExecution
public class IntegrateQueryJobUtil extends QuartzJobBean {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private IntegrateUtil integrateUtil;
    @Autowired
    private IntegrateQueueService integrateQueueService;

    public static Map<String, ScheduledFuture> futureList = new HashMap<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    IntegrateQueryJobUtil(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(20, threadPoolTaskExecutor.getThreadPoolExecutor().getThreadFactory());
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<String> hashValues = redisUtil.getHashValues(IntegrateJobUtil.WORKTIMEOUT_REDIS_KEY);
        for (String userInfoJson : hashValues) {
            UserInfo userInfo = JsonUtil.createJsonToBean(userInfoJson, UserInfo.class);
            String tenantId = StringUtil.isNotEmpty(userInfo.getTenantId()) ? userInfo.getTenantId() : "linzen";
            boolean useSuccess = redisTemplate.opsForValue().setIfAbsent(IntegrateJobUtil.WORKTIMEOUT_REDIS_KEY + "_key:" + tenantId, System.currentTimeMillis(), 3600, TimeUnit.SECONDS);
            if (!useSuccess) continue;
            if (futureList.get(tenantId) == null) {
                ScheduledFuture scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Task(userInfo), 0, 1, TimeUnit.SECONDS);
                futureList.put(tenantId, scheduledFuture);
            }
        }
    }

    class Task implements Runnable {
        private UserInfo userInfo;

        public Task(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        @Override
        public void run() {
            String tenantId = StringUtil.isNotEmpty(userInfo.getTenantId()) ? userInfo.getTenantId() : "linzen";
            if (configValueUtil.isMultiTenancy()) {
                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
            }
            List<IntegrateQueueEntity> list = integrateQueueService.getList();
            if (list.size() > 0) {
                String token = AuthUtil.loginTempUser(userInfo.getUserId(), userInfo.getTenantId(), true);
                userInfo = UserProvider.getUser(token);
                UserProvider.setLocalLoginUser(userInfo);
                for (IntegrateQueueEntity entity : list) {
                    IntegrateModel model = new IntegrateModel();
                    model.setUserInfo(userInfo);
                    model.setId(entity.getId());
                    boolean integrate = IntegrateJobUtil.getIntegrate(model, redisUtil);
                    boolean useSuccess = redisTemplate.opsForValue().setIfAbsent(IntegrateJobUtil.WORKTIMEOUT_REDIS_KEY + "_key:" + entity.getId(), System.currentTimeMillis(), 3600, TimeUnit.SECONDS);
                    if (integrate && useSuccess) {
                        entity.setState(1);
                        integrateQueueService.update(entity.getId(), entity);
                        try {
                            integrateUtil.integrate(entity.getIntegrateId(), userInfo);
                        } catch (Exception e) {

                        }
                        integrateQueueService.delete(entity);
                        IntegrateJobUtil.removeIntegrate(model, redisUtil);
                    }
                }
            }
            //删除
            redisTemplate.delete(IntegrateJobUtil.WORKTIMEOUT_REDIS_KEY + "_key:" + tenantId);
        }
    }
}
