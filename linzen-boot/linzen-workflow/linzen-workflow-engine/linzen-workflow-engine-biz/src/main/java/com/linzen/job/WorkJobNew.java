package com.linzen.job;

import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.engine.entity.FlowTaskOperatorEntity;
import com.linzen.engine.model.flowmessage.FlowMsgModel;
import com.linzen.engine.model.flowtask.WorkJobModel;
import com.linzen.engine.util.FlowMsgUtil;
import com.linzen.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@DisallowConcurrentExecution
public class WorkJobNew extends QuartzJobBean {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private FlowMsgUtil flowMsgUtil;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        System.out.println("定时器");
//        List<WorkJobModel> listRedis = WorkJobUtil.getListRedis(redisUtil);
        Map<String, Object> listRedisTime = WorkJobUtil.getListRedisTime(redisUtil);
        if (listRedisTime != null) {
            try {
                long time = System.currentTimeMillis();
//                runCode(listRedis, time);
                //新逻辑，取key 时间缓存，如有延迟，则取延迟workJobModel对象缓存。
                for (String key : listRedisTime.keySet()) {
                    boolean useSuccess = redisTemplate.opsForValue().setIfAbsent(WorkJobUtil.REDIS_KEY + "_key:" + key, System.currentTimeMillis(), 300, TimeUnit.SECONDS);
                    if (!useSuccess) continue;
                    try {
                        boolean isNext = Long.valueOf(listRedisTime.get(key).toString()).longValue() < time;
                        if (isNext) {
                            WorkJobModel workJobModel = WorkJobUtil.getListRedisJson(redisUtil, key);
                            UserInfo userInfo = workJobModel.getUserInfo();
                            FlowMsgModel flowMsgModel = workJobModel.getFlowMsgModel();
                            if (configValueUtil.isMultiTenancy()) {
                                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
                            }
                            flowMsgUtil.message(flowMsgModel);
                            redisUtil.removeHash(WorkJobUtil.REDIS_KEY, key);
                            redisUtil.removeHash(WorkJobUtil.REDIS_KEY + "_json", key);
                        }
                    } finally {
                        redisTemplate.delete(WorkJobUtil.REDIS_KEY + "_key:" + key);
                    }
                }
            } catch (Exception e) {
                log.error("工作流调度报错:" + e.getMessage());
            }
        }
    }

    //旧逻辑
    private void runCode(List<WorkJobModel> listRedis, long time) {
        for (WorkJobModel workJobModel : listRedis) {
            boolean useSuccess = redisTemplate.opsForValue().setIfAbsent(WorkJobUtil.REDIS_KEY + "_key:" + workJobModel.getTaskId(), System.currentTimeMillis(), 300, TimeUnit.SECONDS);
            if (!useSuccess) continue;
            List<FlowTaskOperatorEntity> operatorList = workJobModel.getFlowMsgModel().getOperatorList();
//                    System.out.println("每个对象：" + workJobModel);
            boolean isNext = operatorList.stream().filter(t -> t.getCreatorTime().getTime() > time).count() == 0;
            UserInfo userInfo = workJobModel.getUserInfo();
            FlowMsgModel flowMsgModel = workJobModel.getFlowMsgModel();
            if (configValueUtil.isMultiTenancy()) {
                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
            }
            if (isNext) {
                redisUtil.removeHash(WorkJobUtil.REDIS_KEY, workJobModel.getTaskId());
                redisUtil.remove(WorkJobUtil.REDIS_KEY + "_key:" + workJobModel.getTaskId());
            }
        }
    }
}
