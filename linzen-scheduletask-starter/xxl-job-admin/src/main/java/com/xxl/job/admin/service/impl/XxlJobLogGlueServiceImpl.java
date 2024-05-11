package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.admin.service.XxlJobLogGlueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class XxlJobLogGlueServiceImpl extends ServiceImpl<XxlJobLogGlueDao, XxlJobLogGlue> implements XxlJobLogGlueService {

    @Override
    public int create(XxlJobLogGlue xxlJobLogGlue) {
        return this.save(xxlJobLogGlue) ? 1 : 0;
    }

    @Override
    public List<XxlJobLogGlue> findByJobId(String jobId) {
        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        queryWrapper.lambda().orderByDesc(XxlJobLogGlue::getId);
        return this.list(queryWrapper);
    }

    @Override
    public int removeOld(String jobId, int limit) {
        QueryWrapper<XxlJobLogGlue> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        queryWrapper1.lambda().orderByDesc(XxlJobLogGlue::getUpdateTime);
        Page<XxlJobLogGlue> page = new Page<>(0, limit);
        IPage<XxlJobLogGlue> iPage = this.page(page, queryWrapper1);
        List<String> ids = iPage.getRecords().stream().map(XxlJobLogGlue::getId).collect(Collectors.toList());

        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().notIn(XxlJobLogGlue::getId, ids);
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        return this.remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public int deleteByJobId(String jobId) {
        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        return this.remove(queryWrapper) ? 1 : 0;
    }
}
