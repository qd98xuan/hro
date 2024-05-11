package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.service.XxlJobUserService;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobUserServiceImpl extends ServiceImpl<XxlJobUserDao, XxlJobUser> implements XxlJobUserService {
    @Override
    public List<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
        QueryWrapper<XxlJobUser> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(username)) {
            queryWrapper.lambda().like(XxlJobUser::getUsername, username);
        }
        if (role > -1) {
            queryWrapper.lambda().eq(XxlJobUser::getRole, role);
        }
        queryWrapper.lambda().orderByAsc(XxlJobUser::getUsername);
        Page<XxlJobUser> page = new Page<>(offset/pagesize + 1, pagesize);
        IPage<XxlJobUser> iPage = this.page(page, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String username, int role) {
        QueryWrapper<XxlJobUser> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(username)) {
            queryWrapper.lambda().like(XxlJobUser::getUsername, username);
        }
        if (role > -1) {
            queryWrapper.lambda().eq(XxlJobUser::getRole, role);
        }
        queryWrapper.lambda().orderByAsc(XxlJobUser::getUsername);
        return this.list(queryWrapper).size();
    }

    @Override
    public XxlJobUser loadByUserName(String username) {
        QueryWrapper<XxlJobUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobUser::getUsername, username);
        return this.getOne(queryWrapper);
    }

    @Override
    public int create(XxlJobUser xxlJobUser) {
        return this.save(xxlJobUser) ? 1 : 0;
    }

    @Override
    public int update(XxlJobUser xxlJobUser) {
        UpdateWrapper<XxlJobUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobUser::getId, xxlJobUser.getId());
        if (StringUtil.isNotEmpty(xxlJobUser.getPassword())) {
            updateWrapper.lambda().set(XxlJobUser::getPassword, xxlJobUser.getPassword());
        }
        updateWrapper.lambda().set(XxlJobUser::getRole, xxlJobUser.getRole());
        updateWrapper.lambda().set(XxlJobUser::getPermission, xxlJobUser.getPermission());
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public int delete(String id) {
        return this.removeById(id) ? 1 : 0;
    }
}
