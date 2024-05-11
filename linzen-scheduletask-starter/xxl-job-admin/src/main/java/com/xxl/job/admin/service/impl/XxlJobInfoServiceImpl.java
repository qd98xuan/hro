package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobInfoService;
import com.linzen.scheduletask.entity.XxlJobInfo;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobInfoServiceImpl extends ServiceImpl<XxlJobInfoDao, XxlJobInfo> implements XxlJobInfoService {

    @Override
    public List<XxlJobInfo> pageList(int offset, int pagesize, long jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        if (jobGroup > 0) {
            queryWrapper.lambda().eq(XxlJobInfo::getJobGroup, jobGroup);
        }
        if (triggerStatus >= 0) {
            queryWrapper.lambda().eq(XxlJobInfo::getTriggerStatus, triggerStatus);
        }
        if (StringUtil.isNotEmpty(jobDesc)) {
            queryWrapper.lambda().like(XxlJobInfo::getJobDesc, jobDesc);
        }
        if (StringUtil.isNotEmpty(executorHandler)) {
            queryWrapper.lambda().like(XxlJobInfo::getExecutorHandler, executorHandler);
        }
        if (StringUtil.isNotEmpty(author)) {
            queryWrapper.lambda().like(XxlJobInfo::getAuthor, author);
        }
        queryWrapper.lambda().orderByDesc(XxlJobInfo::getId);
        Page<XxlJobInfo> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobInfo> iPage = this.page(page, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public int pageListCount(int offset, int pagesize, long jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        if (jobGroup > 0) {
            queryWrapper.lambda().eq(XxlJobInfo::getJobGroup, jobGroup);
        }
        if (triggerStatus >= 0) {
            queryWrapper.lambda().eq(XxlJobInfo::getTriggerStatus, triggerStatus);
        }
        if (StringUtil.isNotEmpty(jobDesc)) {
            queryWrapper.lambda().like(XxlJobInfo::getJobDesc, jobDesc);
        }
        if (StringUtil.isNotEmpty(executorHandler)) {
            queryWrapper.lambda().like(XxlJobInfo::getExecutorHandler, executorHandler);
        }
        if (StringUtil.isNotEmpty(author)) {
            queryWrapper.lambda().like(XxlJobInfo::getAuthor, author);
        }
        Page<XxlJobInfo> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobInfo> iPage = this.page(page, queryWrapper);
        return (int) iPage.getTotal();
    }

    @Override
    public int create(XxlJobInfo info) {
        return this.save(info) ? 1 : 0;
    }

    @Override
    public XxlJobInfo loadById(String id) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public int update(XxlJobInfo xxlJobInfo) {
        return this.updateById(xxlJobInfo) ? 1 : 0;
    }

    @Override
    public int delete(String id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public List<XxlJobInfo> getJobsByGroup(String jobGroup) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getJobGroup, jobGroup);
        return this.list(queryWrapper);
    }

    @Override
    public int findAllCount() {
        return this.list().size();
    }

    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getTriggerStatus, 1);
        queryWrapper.lambda().le(XxlJobInfo::getTriggerNextTime, maxNextTime);
        queryWrapper.lambda().orderByAsc(XxlJobInfo::getId);
        return this.list(queryWrapper);
    }

    @Override
    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        UpdateWrapper<XxlJobInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobInfo::getId, xxlJobInfo.getId());
        updateWrapper.lambda().set(XxlJobInfo::getTriggerLastTime, xxlJobInfo.getTriggerLastTime());
        updateWrapper.lambda().set(XxlJobInfo::getTriggerNextTime, xxlJobInfo.getTriggerNextTime());
        updateWrapper.lambda().set(XxlJobInfo::getTriggerStatus, xxlJobInfo.getTriggerStatus());
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public XxlJobInfo queryByTaskId(String taskId) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getTaskId, taskId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean deleteByTaskId(String taskId) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getTaskId, taskId);
        return this.remove(queryWrapper);
    }

}
