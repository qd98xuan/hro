package com.linzen.integrate.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.integrate.job.Integrate;
import com.linzen.integrate.job.IntegrateJobUtil;
import com.linzen.integrate.job.QuartzUtil;
import com.linzen.integrate.model.nodeJson.IntegrateModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.RedisUtil;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class IntegrateListener implements ApplicationRunner {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<String> keysList = redisTemplate.keys(IntegrateJobUtil.WORKTIMEOUT_REDIS_KEY + "_key*");
        for (String id : keysList) {
            redisTemplate.delete(id);
        }
        List<String> hashValues = redisUtil.getHashValues(IntegrateJobUtil.IDGENERATOR_REDIS_KEY);
        for (String integrateModelJson : hashValues) {
            IntegrateModel integrateModel = JsonUtil.createJsonToBean(integrateModelJson, IntegrateModel.class);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(JsonUtil.entityToMap(integrateModel));
            Date startTime = new Date(integrateModel.getStartTime());
            Date endTime = ObjectUtil.isNotEmpty(integrateModel.getEndTime()) ? new Date(integrateModel.getEndTime()) : null;
            boolean isAdd = ObjectUtil.isNotEmpty(endTime) ? endTime.getTime() > System.currentTimeMillis() : true;
            if (isAdd) {
                QuartzUtil.addJob(integrateModel.getId(), integrateModel.getCron(), Integrate.class, jobDataMap, startTime, endTime);
            }
        }
    }

}
