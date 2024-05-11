package com.xxl.job.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.core.model.XxlJobLogReport;

/**
 * job log
 * @author FHNP
 */
public interface XxlJobLogReportDao extends BaseMapper<XxlJobLogReport> {

	XxlJobLogReport queryLogReportTotal();

}
