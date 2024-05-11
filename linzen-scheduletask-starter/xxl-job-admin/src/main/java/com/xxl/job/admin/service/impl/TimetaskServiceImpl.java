package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.mapper.TimeTaskMapper;
import com.xxl.job.admin.service.HandlerNameService;
import com.xxl.job.admin.service.TimetaskService;
import com.xxl.job.admin.service.XxlJobService;
import com.linzen.base.Pagination;
import com.linzen.base.UserInfo;
import com.linzen.scheduletask.entity.*;
import com.linzen.scheduletask.model.ContentNewModel;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 定时任务
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class TimetaskServiceImpl extends ServiceImpl<TimeTaskMapper, TimeTaskEntity> implements TimetaskService {

    @Autowired
    private HandlerNameService handlerNameService;
    @Autowired
    private XxlJobService xxlJobService;

    @Override
    public List<TimeTaskEntity> getList(Pagination pagination, UserInfo userInfo) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        if (pagination.getKeyword() != null) {
            queryWrapper.lambda().and(
                    t -> t.like(TimeTaskEntity::getEnCode, pagination.getKeyword())
                            .or().like(TimeTaskEntity::getFullName, pagination.getKeyword())
            );
        }
        // 验证是否为多租户
        if (StringUtil.isNotEmpty(userInfo.getTenantId())) {
            queryWrapper.lambda().eq(TimeTaskEntity::getTenantId, userInfo.getTenantId());
        }
        //排序
        queryWrapper.lambda().orderByAsc(TimeTaskEntity::getSortCode).orderByDesc(TimeTaskEntity::getCreatorTime);
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<TimeTaskEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public TimeTaskEntity getInfo(String id, UserInfo userInfo) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getId, id);
        // 验证是否为多租户
        if (StringUtil.isNotEmpty(userInfo.getTenantId())) {
            queryWrapper.lambda().eq(TimeTaskEntity::getTenantId, userInfo.getTenantId());
        }
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(TimeTaskEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(TimeTaskEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean create(TimeTaskEntity entity, UserInfo userInfo) {
        entity.setId(StringUtil.isNotEmpty(entity.getId()) ? entity.getId() : RandomUtil.uuId());
        ContentNewModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentNewModel.class);
        model.setUserInfo(userInfo);
        // 得到token
        model.setToken(userInfo.getToken());
        // 添加时间
        Date date = new Date();
        entity.setCreatorTime(date);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setTenantId(userInfo.getTenantId());
        // 将任务添加到info表中
        // 构造模型
        XxlJobInfo xxlJobInfo = buildModel(entity, model, new XxlJobInfo(), date, userInfo);
        // 如果是本地方法
        boolean flag = true;
        if ("3".equals(entity.getExecuteType())) {
            // 获取本地方法对应的executor和handlerName
            HandlerNameEntity handlerNameEntity = handlerNameService.getInfo(model.getLocalHostTaskId());
            if (handlerNameEntity == null) {
                flag = false;
            } else {
                // 获取执行器
                List<XxlJobGroup> xxlJobGroup = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupService().findByAppname(handlerNameEntity.getExecutor());
                xxlJobInfo.setJobGroup(xxlJobGroup.size() == 1 ? xxlJobGroup.get(0).getId() : null);
                xxlJobInfo.setScheduleConf(model.getCron());
                xxlJobInfo.setExecutorHandler(handlerNameEntity.getHandlerName());
            }
        }
        // 保存到linzen的表中
        this.save(entity);
        if (flag) {
            XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().create(xxlJobInfo);
            // 开始调度
            xxlJobService.start(xxlJobInfo.getId());
        }
        return flag;
    }

    @Override
    public boolean schedule(TimeTaskEntity entity) {
        String id = entity.getId();
        boolean flag = true;
        ContentNewModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentNewModel.class);
        UserInfo userInfo = model.getUserInfo();
        TimeTaskEntity info = getInfo(id, userInfo);
        if (info == null) {
            create(entity, userInfo);
        }
        return flag;
    }

    @Override
    public boolean update(String id, TimeTaskEntity entity, UserInfo userInfo) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        entity.setUpdateUserId(userInfo.getUserId());
        entity.setTenantId(userInfo.getTenantId());

        ContentNewModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentNewModel.class);
        model.setUserInfo(userInfo);
        // 得到token
        model.setToken(userInfo.getToken());
        entity.setExecuteContent(JsonUtil.getObjectToString(model));
        // 获取当前时间
        Date date = new Date();
        entity.setUpdateTime(date);
        entity.setUpdateUserId(userInfo.getUserId());
        // 修改任务
        // 通过任务id得到任务
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().queryByTaskId(id);
        if (jobInfo == null) {
            jobInfo = new XxlJobInfo();
        }
        // 构造模型
        XxlJobInfo xxlJobInfo = buildModel(entity, model, jobInfo, date, userInfo);
        // 如果是本地方法
        boolean flag = true;
        if ("3".equals(entity.getExecuteType())) {
            // 获取本地方法对应的executor和handlerName
            HandlerNameEntity handlerNameEntity = handlerNameService.getInfo(model.getLocalHostTaskId());
            if (handlerNameEntity == null) {
                flag = false;
            } else {
                // 获取执行器
                List<XxlJobGroup> xxlJobGroup = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupService().findByAppname(handlerNameEntity.getExecutor());
                xxlJobInfo.setJobGroup(xxlJobGroup.size() == 1 ? xxlJobGroup.get(0).getId() : null);
                xxlJobInfo.setScheduleConf(model.getCron());
                xxlJobInfo.setExecutorHandler(handlerNameEntity.getHandlerName());
            }
        }
        if (flag) {
            XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().update(xxlJobInfo);
        }

        // return修改结果
        return this.updateById(entity);
    }

    @Override
    public void delete(TimeTaskEntity entity) {
        this.removeById(entity.getId());
        // 删除任务
        XxlJobAdminConfig.getAdminConfig().getXxlJobInfoService().deleteByTaskId(entity.getId());

        XxlJobAdminConfig.getAdminConfig().getXxlJobLogService().deleteByTaskId(entity.getId());
    }

    @Override
    public void updateTask(String taskId, TimeTaskEntity entity) {
        entity.setId(taskId);
        this.updateById(entity);
    }

    /**
     * 构造任务并启动
     *
     * @param entity
     * @param date
     * @param model
     */
    private XxlJobInfo buildModel(TimeTaskEntity entity, ContentNewModel model, XxlJobInfo xxlJobInfo, Date date, UserInfo userInfo) {

        // 默认一个执行器主键ID
        xxlJobInfo.setJobGroup("8");

        // 默认一个执行器描述
        xxlJobInfo.setJobDesc(entity.getFullName());
        // 负责人
        xxlJobInfo.setAuthor(userInfo.getUserId());
        // 调度类型
        xxlJobInfo.setScheduleType("CRON");
        // 添加时间
        xxlJobInfo.setAddTime(date);
        // 调度配置，值含义取决于调度类型
        xxlJobInfo.setScheduleConf(model.getCron());
        // 调度过期策略（默认忽略）
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        // 执行器路由策略（默认第一个）
        xxlJobInfo.setExecutorRouteStrategy("FIRST");

        // 执行器任务handler（执行任务的handler）
        xxlJobInfo.setExecutorHandler("defaultHandler");
        // 执行参数
        // 将ContentNewModel当做参数传递给任务执行器
        model.setExecuteType(entity.getExecuteType());
        xxlJobInfo.setExecutorParam(JsonUtil.getObjectToString(model));

        // 阻塞处理策略（默认单行串行）
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        // 任务执行超时时间，单位秒（默认0秒）
        xxlJobInfo.setExecutorTimeout(0);
        // 失败重试次数（默认0）
        xxlJobInfo.setExecutorFailRetryCount(0);
        // GLUE类型（默认BEAN）
        xxlJobInfo.setGlueType("BEAN");
        // GLUE源代码
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        // 调度状态：0-停止，1-运行（默认运行）
        xxlJobInfo.setTriggerStatus(entity.getEnabledMark());
        // 上次调度时间
        xxlJobInfo.setTriggerLastTime(0L);
        // 下次调度时间
        xxlJobInfo.setTriggerNextTime(0L);
        // 创建时间GLUE
        xxlJobInfo.setGlueUpdatetime(date);
        // GLUE源代码
        xxlJobInfo.setGlueSource("");
        // 子任务ID，多个逗号分隔
        xxlJobInfo.setChildJobId("");
        // 租户id
        xxlJobInfo.setTenantId(userInfo.getTenantId());
        // 创建时增加任务id
        xxlJobInfo.setTaskId(entity.getId());

        // 修改时间
        if (entity.getUpdateTime() != null) {
            xxlJobInfo.setUpdateTime(entity.getUpdateTime());
        }

        return xxlJobInfo;
    }

}
