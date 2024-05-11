package com.linzen.integrate.job;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.UserInfo;
import com.linzen.config.ConfigValueUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.integrate.entity.IntegrateEntity;
import com.linzen.integrate.entity.IntegrateQueueEntity;
import com.linzen.integrate.model.nodeJson.IntegrateModel;
import com.linzen.integrate.service.IntegrateQueueService;
import com.linzen.integrate.service.IntegrateService;
import com.linzen.util.RandomUtil;
import com.linzen.util.RedisUtil;
import com.linzen.util.context.SpringContext;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.Objects;


@DisallowConcurrentExecution
public class Integrate extends QuartzJobBean {


    private static RedisUtil redisUtil;
    private static ConfigValueUtil configValueUtil;
    private static IntegrateService integrateService;
    private static IntegrateQueueService integrateQueueService;

    static {
        redisUtil = SpringContext.getBean(RedisUtil.class);
        configValueUtil = SpringContext.getBean(ConfigValueUtil.class);
        integrateService = SpringContext.getBean(IntegrateService.class);
        integrateQueueService = SpringContext.getBean(IntegrateQueueService.class);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        String jobName = jobDetail.getKey().getName();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        IntegrateModel model = IntegrateJobUtil.getModel(BeanUtil.toBean(jobDataMap, IntegrateModel.class), redisUtil);
        boolean isModel = model == null;
        IntegrateModel integrateModel = isModel ? BeanUtil.toBean(jobDataMap, IntegrateModel.class) : model;
        if (integrateModel != null) {
            UserInfo userInfo = integrateModel.getUserInfo();
            if (configValueUtil.isMultiTenancy()) {
                TenantDataSourceUtil.switchTenant(userInfo.getTenantId());
            }
            IntegrateEntity info = integrateService.getInfo(integrateModel.getId());
            if (info != null) {
                integrateModel.setTime(System.currentTimeMillis());
                Integer num = integrateModel.getNum();
                Integer endTimeType = integrateModel.getEndTimeType();
                Integer endLimit = integrateModel.getEndLimit();
                boolean isNext = Objects.equals(endTimeType, 1) ? num + 1 <= endLimit : Objects.equals(endTimeType, 2) ? integrateModel.getTime() <= integrateModel.getEndTime() : true;
                integrateModel.setNum(++num);
                if (isNext) {
                    IntegrateJobUtil.insertModel(integrateModel, redisUtil);
                    IntegrateQueueEntity entity = new IntegrateQueueEntity();
                    entity.setState(0);
                    entity.setId(RandomUtil.uuId());
                    entity.setIntegrateId(integrateModel.getId());
                    entity.setExecutionTime(new Date());
                    entity.setFullName(info.getFullName());
                    IntegrateModel integrate  = new IntegrateModel();
                    integrate.setId(entity.getId());
                    integrate.setUserInfo(userInfo);
                    IntegrateJobUtil.insertIntegrate(integrate,redisUtil);
                    integrateQueueService.create(entity);
                } else {
                    IntegrateJobUtil.removeModel(integrateModel, redisUtil);
                    QuartzUtil.deleteJob(jobName);
                }
            } else {
                IntegrateJobUtil.removeModel(integrateModel, redisUtil);
                QuartzUtil.deleteJob(jobName);
            }
        }
    }


}
