package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface XxlJobLogReportService extends IService<XxlJobLogReport> {

    int create(XxlJobLogReport xxlJobLogReport);

    int update(XxlJobLogReport xxlJobLogReport);

    List<XxlJobLogReport> queryLogReport(Date triggerDayFrom,
                                         Date triggerDayTo);

    XxlJobLogReport queryLogReportTotal();
}
