package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.XxlJobGroupService;
import com.linzen.scheduletask.entity.XxlJobGroup;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobGroupServiceImpl extends ServiceImpl<XxlJobGroupDao, XxlJobGroup> implements XxlJobGroupService {

    @Override
    public List<XxlJobGroup> findAll() {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(XxlJobGroup::getAppname)
                .orderByAsc(XxlJobGroup::getTitle)
                .orderByAsc(XxlJobGroup::getId);
        return this.list(queryWrapper);
    }

    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobGroup::getAddressType, addressType);
        queryWrapper.lambda().orderByAsc(XxlJobGroup::getAppname)
                .orderByAsc(XxlJobGroup::getTitle)
                .orderByAsc(XxlJobGroup::getId);
        return this.list(queryWrapper);
    }

    @Override
    public int create(XxlJobGroup xxlJobGroup) {
        return this.save(xxlJobGroup) ? 1 : 0;
    }

    @Override
    public int update(XxlJobGroup xxlJobGroup) {
        return this.updateById(xxlJobGroup) ? 1 : 0;
    }

    @Override
    public int remove(long id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public XxlJobGroup load(String id) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobGroup::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(appname)) {
            queryWrapper.lambda().like(XxlJobGroup::getAppname, appname);
        }
        if (StringUtil.isNotEmpty(title)) {
            queryWrapper.lambda().like(XxlJobGroup::getTitle, title);
        }
        queryWrapper.lambda().orderByAsc(XxlJobGroup::getAppname)
                .orderByAsc(XxlJobGroup::getTitle)
                .orderByAsc(XxlJobGroup::getId);
        Page<XxlJobGroup> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobGroup> iPage = this.page(page, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String appname, String title) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(appname)) {
            queryWrapper.lambda().like(XxlJobGroup::getAppname, appname);
        }
        if (StringUtil.isNotEmpty(title)) {
            queryWrapper.lambda().like(XxlJobGroup::getTitle, title);
        }
        return this.list(queryWrapper).size();
    }

    @Override
    public List<XxlJobGroup> findByAppname(String appname) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobGroup::getAppname, appname);
        return this.list(queryWrapper);
    }

    @Override
    public int updateByAppname(XxlJobGroup xxlJobGroup) {
        UpdateWrapper<XxlJobGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobGroup::getAppname, xxlJobGroup.getAppname());
        updateWrapper.lambda().set(XxlJobGroup::getTitle, xxlJobGroup.getTitle());
        updateWrapper.lambda().set(XxlJobGroup::getAddressType, xxlJobGroup.getAddressType());
        updateWrapper.lambda().set(XxlJobGroup::getAddressList, xxlJobGroup.getAddressList());
        updateWrapper.lambda().set(XxlJobGroup::getUpdateTime, xxlJobGroup.getUpdateTime());
        return this.update(updateWrapper) ? 1 : 0;
    }
}
