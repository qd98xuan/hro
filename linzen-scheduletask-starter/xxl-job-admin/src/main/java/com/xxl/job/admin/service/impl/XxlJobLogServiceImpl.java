package com.xxl.job.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import com.linzen.scheduletask.entity.XxlJobLog;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.service.XxlJobLogService;
import com.linzen.scheduletask.model.TaskPage;
import com.linzen.util.DateUtil;
import com.linzen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XxJobLogServiceImpl
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class XxlJobLogServiceImpl extends ServiceImpl<XxlJobLogDao, XxlJobLog> implements XxlJobLogService {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<XxlJobLog> getList(String taskId, TaskPage taskPage) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLog::getJobId, taskId);
        //日期范围
        if (ObjectUtil.isNotEmpty(taskPage.getStartTime()) && ObjectUtil.isNotEmpty(taskPage.getEndTime())) {
            queryWrapper.lambda().between(XxlJobLog::getTriggerTime, new Date(taskPage.getStartTime()), new Date(taskPage.getEndTime()));
        }
        if (taskPage.getRunResult() != null) {
            if (taskPage.getRunResult() == 1) {
                queryWrapper.lambda().ne(XxlJobLog::getHandleCode, 200);
            } else {
                queryWrapper.lambda().eq(XxlJobLog::getHandleCode, 200);
            }
        }
        queryWrapper.lambda().orderByDesc(XxlJobLog::getTriggerTime);
        queryWrapper.select(XxlJobLog.class, t -> !"executor_param".equals(t.getColumn()));
        Page page = new Page(taskPage.getCurrentPage(), taskPage.getPageSize());
        IPage<XxlJobLog> iPage = this.page(page, queryWrapper);
        return taskPage.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public Long queryCountByJobId(String jobId) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLog::getJobId, jobId);
        return this.count(queryWrapper);
    }

    @Override
    public List<XxlJobLog> pageList(int offset, int pagesize, String jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(jobGroup) && !jobGroup.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getJobGroup, jobGroup);
        }
        if (StringUtil.isNotEmpty(jobGroup) && !jobId.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getId, jobId);
        }
        if (triggerTimeStart != null) {
            queryWrapper.lambda().ge(XxlJobLog::getTriggerTime, triggerTimeStart);
        }
        if (triggerTimeEnd != null) {
            queryWrapper.lambda().le(XxlJobLog::getTriggerTime, triggerTimeEnd);
        }
        if (logStatus == 1) {
            queryWrapper.lambda().eq(XxlJobLog::getHandleCode, 200);
        } else if (logStatus == 2) {
            queryWrapper.lambda().and(t -> t.notIn(XxlJobLog::getTriggerCode, 0, 200).or().notIn(XxlJobLog::getHandleCode, 0, 200));
        } else if (logStatus == 3) {
            queryWrapper.lambda().eq(XxlJobLog::getTriggerCode, 200).eq(XxlJobLog::getHandleCode, 200);
        }
        queryWrapper.lambda().orderByDesc(XxlJobLog::getTriggerTime);
        Page<XxlJobLog> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobLog> iPage = this.page(page, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(jobGroup) && !jobGroup.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getJobGroup, jobGroup);
        }
        if (StringUtil.isNotEmpty(jobGroup) && !jobId.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getId, jobId);
        }
        if (triggerTimeStart != null) {
            queryWrapper.lambda().ge(XxlJobLog::getTriggerTime, triggerTimeStart);
        }
        if (triggerTimeEnd != null) {
            queryWrapper.lambda().le(XxlJobLog::getTriggerTime, triggerTimeEnd);
        }
        if (logStatus == 1) {
            queryWrapper.lambda().eq(XxlJobLog::getHandleCode, 200);
        } else if (logStatus == 2) {
            queryWrapper.lambda().and(t -> t.notIn(XxlJobLog::getTriggerCode, 0, 200).or().notIn(XxlJobLog::getHandleCode, 0, 200));
        } else if (logStatus == 3) {
            queryWrapper.lambda().eq(XxlJobLog::getTriggerCode, 200).eq(XxlJobLog::getHandleCode, 200);
        }
        Page<XxlJobLog> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobLog> iPage = this.page(page, queryWrapper);
        return (int) iPage.getTotal();
    }

    @Override
    public XxlJobLog load(String id) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLog::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public long create(XxlJobLog xxlJobLog) {
        return this.save(xxlJobLog) ? 1 : 0;
    }

    @Override
    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        return this.updateById(xxlJobLog) ? 1 : 0;
    }

    @Override
    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        UpdateWrapper<XxlJobLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobLog::getId, xxlJobLog.getId());
        updateWrapper.lambda().set(XxlJobLog::getHandleTime, xxlJobLog.getHandleTime());
        updateWrapper.lambda().set(XxlJobLog::getHandleCode, xxlJobLog.getHandleCode());
        updateWrapper.lambda().set(XxlJobLog::getHandleMsg, xxlJobLog.getHandleMsg());
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public int delete(String jobId) {
        return this.removeById(jobId) ? 1 : 0;
    }

    @Override
    public Map<String, Object> findLogReport(Date from, Date to) {
        String url = null;
        try {
            url = ((HikariDataSource) dataSource).getJdbcUrl();
        } catch (Exception e) {
            log.error("获取连接连接失败：" + e.getMessage());
        }
        String dbType = getDbType(url);
        if(dbType.equals("Oracle")){
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return this.baseMapper.oracleFindLogReport(fmt.format(from), fmt.format(to), dbType);
        }
        return this.baseMapper.findLogReport(from, to, dbType);
    }

    /**
     * 得到当前库类型
     * @param url
     * @return
     */
    private static String getDbType(String url) {
        if (url == null) {
            return "";
        }
        if (url.contains("mysql")) {
            return "MySQL";
        } else if (url.contains("sqlserver")) {
            return "SQLServer";
        } else if (url.contains("oracle")) {
            return "Oracle";
        } else if (url.contains("dm")) {
            return "DM8";
        } else if (url.contains("kingbase8")) {
            return "KingbaseES";
        } else if (url.contains("postgresql")) {
            return "PostgreSQL";
        }
        return "";
    }

    @Override
    public List<String> findClearLogIds(String jobGroup, String jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(jobGroup) && !jobGroup.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getJobGroup, jobGroup);
        }
        if (StringUtil.isNotEmpty(jobGroup) && !jobId.equals("0")) {
            queryWrapper.lambda().eq(XxlJobLog::getId, jobId);
        }
        if (clearBeforeTime != null) {
            queryWrapper.lambda().eq(XxlJobLog::getJobId, jobId);
        }
        if (clearBeforeNum > 0) {
            // 子查询内容转换
            QueryWrapper<XxlJobLog> queryWrapper1 = new QueryWrapper<>();
            if (StringUtil.isNotEmpty(jobGroup) && !jobGroup.equals("0")) {
                queryWrapper.lambda().eq(XxlJobLog::getJobGroup, jobGroup);
            }
            if (StringUtil.isNotEmpty(jobGroup) && !jobId.equals("0")) {
                queryWrapper.lambda().eq(XxlJobLog::getId, jobId);
            }
            queryWrapper1.lambda().orderByDesc(XxlJobLog::getTriggerTime);
            Page<XxlJobLog> page = new Page<>(0, clearBeforeNum);
            IPage<XxlJobLog> iPage = this.page(page, queryWrapper1);
            List<String> collect = iPage.getRecords().stream().map(XxlJobLog::getId).collect(Collectors.toList());
            queryWrapper.lambda().notIn(XxlJobLog::getJobId, collect);
        }
        Page<XxlJobLog> page = new Page<>(0, pagesize);
        IPage<XxlJobLog> iPage = this.page(page, queryWrapper);
        return iPage.getRecords().stream().map(XxlJobLog::getId).collect(Collectors.toList());
    }

    @Override
    public int clearLog(List<String> logIds) {
        if (logIds.size() > 0) {
            QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(XxlJobLog::getId, logIds);
            return this.remove(queryWrapper) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public List<String> findFailJobLogIds(int pagesize) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLog::getAlarmStatus, 0);
        queryWrapper.lambda().orderByDesc(XxlJobLog::getJobId);
        queryWrapper.lambda().and(t->
                t.in(XxlJobLog::getTriggerCode, 201, 50000).and(tt -> tt.ne(XxlJobLog::getHandleCode, 0))
                        .or().ne(XxlJobLog::getHandleCode, 200)
        );
        Page<XxlJobLog> page = new Page<>(0, pagesize);
        IPage<XxlJobLog> iPage = this.page(page, queryWrapper);
        return iPage.getRecords().stream().map(XxlJobLog::getId).collect(Collectors.toList());
    }

    @Override
    public int updateAlarmStatus(String logId, int oldAlarmStatus, int newAlarmStatus) {
        UpdateWrapper<XxlJobLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobLog::getId, logId).eq(XxlJobLog::getAlarmStatus, oldAlarmStatus);
        updateWrapper.lambda().set(XxlJobLog::getAlarmStatus, newAlarmStatus);
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public List<String> findLostJobIds(Date losedTime) {
        String url = null;
        try {
            url = ((HikariDataSource) dataSource).getJdbcUrl();
        } catch (Exception e) {
            log.error("获取连接连接失败：" + e.getMessage());
        }
        String dbType = getDbType(url);
        return this.baseMapper.findLostJobIds(DateUtil.dateFormat(losedTime), dbType);
    }

    @Override
    public boolean deleteByTaskId(String taskId) {
        QueryWrapper<XxlJobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLog::getId, taskId);
        return this.remove(queryWrapper);
    }

}
