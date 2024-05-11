package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.core.model.XxlJobLogGlue;

import java.util.List;

public interface XxlJobLogGlueService extends IService<XxlJobLogGlue> {

    int create(XxlJobLogGlue xxlJobLogGlue);

    List<XxlJobLogGlue> findByJobId(String jobId);

    int removeOld(String jobId, int limit);

    int deleteByJobId(String jobId);
}
