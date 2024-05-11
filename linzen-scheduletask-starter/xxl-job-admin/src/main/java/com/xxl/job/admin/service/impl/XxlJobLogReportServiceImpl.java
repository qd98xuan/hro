package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.dao.XxlJobLogReportDao;
import com.xxl.job.admin.service.XxlJobLogReportService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class XxlJobLogReportServiceImpl extends ServiceImpl<XxlJobLogReportDao, XxlJobLogReport> implements XxlJobLogReportService {

    @Override
    public int create(XxlJobLogReport xxlJobLogReport) {
        return this.save(xxlJobLogReport) ? 1 : 0;
    }

    @Override
    public int update(XxlJobLogReport xxlJobLogReport) {
        UpdateWrapper<XxlJobLogReport> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobLogReport::getTriggerDay, xxlJobLogReport.getTriggerDay());
        updateWrapper.lambda().set(XxlJobLogReport::getRunningCount, xxlJobLogReport.getRunningCount());
        updateWrapper.lambda().set(XxlJobLogReport::getSucCount, xxlJobLogReport.getSucCount());
        updateWrapper.lambda().set(XxlJobLogReport::getFailCount, xxlJobLogReport.getFailCount());
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        QueryWrapper<XxlJobLogReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(XxlJobLogReport::getTriggerDay, triggerDayFrom, triggerDayTo);
        queryWrapper.lambda().orderByAsc(XxlJobLogReport::getTriggerDay);
        return this.list(queryWrapper);
    }

    @Override
    public XxlJobLogReport queryLogReportTotal() {
        return null;
    }
}
