package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linzen.scheduletask.entity.XxlJobInfo;

import java.util.List;

public interface XxlJobInfoService extends IService<XxlJobInfo> {

    List<XxlJobInfo> pageList(int offset,
                              int pagesize,
                              long jobGroup,
                              int triggerStatus,
                              String jobDesc,
                              String executorHandler,
                              String author);

    int pageListCount(int offset,
                             int pagesize,
                             long jobGroup,
                             int triggerStatus,
                             String jobDesc,
                             String executorHandler,
                             String author);

    int create(XxlJobInfo info);

    XxlJobInfo loadById(String id);

    int update(XxlJobInfo xxlJobInfo);

    int delete(String id);

    List<XxlJobInfo> getJobsByGroup(String jobGroup);

    int findAllCount();

    List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize );

    int scheduleUpdate(XxlJobInfo xxlJobInfo);

    /**
     * 通过taskId获取任务信息
     *
     * @param taskId
     * @return
     */
    XxlJobInfo queryByTaskId(String taskId);

    /**
     * 通过taskId删除任务
     *
     * @param taskId
     * @return
     */
    boolean deleteByTaskId(String taskId);

}
