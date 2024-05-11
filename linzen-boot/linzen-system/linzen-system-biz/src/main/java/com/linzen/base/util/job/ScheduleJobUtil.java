package com.linzen.base.util.job;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.model.schedule.ScheduleJobModel;
import com.linzen.config.ConfigValueUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.RedisUtil;
import com.linzen.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 流程设计
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Component
@Slf4j
@DependsOn("threadPoolTaskExecutor")
public class ScheduleJobUtil {
    /**
     * 缓存key
     */
    public static final String WORKTIMEOUT_REDIS_KEY = "idgenerator_Schedule";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    ScheduleJobUtil(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2, threadPoolTaskExecutor.getThreadPoolExecutor().getThreadFactory());
    }

    /**
     * 将数据放入缓存
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public void insertRedis(List<ScheduleJobModel> scheduleJobList, RedisUtil redisUtil) {
        for (ScheduleJobModel jobModel : scheduleJobList) {
            String id = jobModel.getId();
            String objectToString = JsonUtil.createObjectToString(jobModel);
            redisUtil.insertHash(WORKTIMEOUT_REDIS_KEY, id, objectToString);
        }
    }

    /**
     * 定时器取用数据调用创建方法
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public List<ScheduleJobModel> getListRedis(RedisUtil redisUtil) {
        List<ScheduleJobModel> scheduleJobList = new ArrayList<>();
        if (redisUtil.exists(WORKTIMEOUT_REDIS_KEY)) {
            Map<String, Object> map = redisUtil.getMap(WORKTIMEOUT_REDIS_KEY);
            for (String object : map.keySet()) {
                if (map.get(object) instanceof String) {
                    ScheduleJobModel scheduleJobModel = JsonUtil.createJsonToBean(String.valueOf(map.get(object)), ScheduleJobModel.class);
                    if(StringUtil.isNotEmpty(scheduleJobModel.getId())) {
                        scheduleJobList.add(scheduleJobModel);
                    }else {
                        redisUtil.removeHash(WORKTIMEOUT_REDIS_KEY,object);
                    }
                }else {
                    redisUtil.removeHash(WORKTIMEOUT_REDIS_KEY,object);
                }
            }
        }
        return scheduleJobList;
    }


}
