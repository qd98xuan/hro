package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linzen.scheduletask.entity.XxlJobLog;
import com.linzen.scheduletask.model.TaskPage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * xxljoblog
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface XxlJobLogService extends IService<XxlJobLog> {

    /**
     * 分页获取调用日志
     *
     * @param taskId 任务id
     * @param taskPage
     * @return
     */
    List<XxlJobLog> getList(String taskId, TaskPage taskPage);

    /**
     * 通过jobid获取日志总数
     *
     * @param jobId 任务id
     * @return
     */
    Long queryCountByJobId(String jobId);


    // exist jobId not use jobGroup, not exist use jobGroup
    List<XxlJobLog> pageList(int offset,
                                    int pagesize,
                                    String jobGroup,
                                    String jobId,
                                    Date triggerTimeStart,
                                    Date triggerTimeEnd,
                                    int logStatus);

    int pageListCount(int offset,
                             int pagesize,
                             String jobGroup,
                             String jobId,
                             Date triggerTimeStart,
                             Date triggerTimeEnd,
                             int logStatus);

    XxlJobLog load(String id);

    long create(XxlJobLog xxlJobLog);

    int updateTriggerInfo(XxlJobLog xxlJobLog);

    int updateHandleInfo(XxlJobLog xxlJobLog);

    int delete(String jobId);

    Map<String, Object> findLogReport(Date from,
                                             Date to);

    List<String> findClearLogIds(String jobGroup,
                                    String jobId,
                                      Date clearBeforeTime,
                                      int clearBeforeNum,
                                      int pagesize);

    int clearLog(List<String> logIds);

    List<String> findFailJobLogIds(int pagesize);

    int updateAlarmStatus(String logId,
                                 int oldAlarmStatus,
                                 int newAlarmStatus);

    List<String> findLostJobIds(Date losedTime);

    boolean deleteByTaskId(String taskId);

}
