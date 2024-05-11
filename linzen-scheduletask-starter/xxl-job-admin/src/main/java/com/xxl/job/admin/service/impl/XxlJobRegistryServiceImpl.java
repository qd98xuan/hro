package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.service.XxlJobRegistryService;
import com.linzen.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XxlJobRegistryServiceImpl extends ServiceImpl<XxlJobRegistryDao, XxlJobRegistry> implements XxlJobRegistryService {
    @Override
    public List<String> findDead(int timeout, Date nowTime) {
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        // 计算时间
        Date date = DateUtil.dateAddSeconds(nowTime, - timeout);
        queryWrapper.lambda().lt(XxlJobRegistry::getUpdateTime, date);
        return this.list(queryWrapper).stream().map(XxlJobRegistry::getId).collect(Collectors.toList());
    }

    @Override
    public int removeDead(List<String> ids) {
        if (ids.size() > 0) {
            QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(XxlJobRegistry::getId, ids);
            return this.remove(queryWrapper) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        // 计算时间
        Date date = DateUtil.dateAddSeconds(nowTime, -timeout);
        queryWrapper.lambda().gt(XxlJobRegistry::getUpdateTime, date);
        return this.list(queryWrapper);
    }

    @Override
    public int registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        UpdateWrapper<XxlJobRegistry> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XxlJobRegistry::getRegistryGroup, registryGroup);
        updateWrapper.lambda().eq(XxlJobRegistry::getRegistryKey, registryKey);
        updateWrapper.lambda().eq(XxlJobRegistry::getRegistryValue, registryValue);
        updateWrapper.lambda().set(XxlJobRegistry::getUpdateTime, updateTime);
        return this.update(updateWrapper) ? 1 : 0;
    }

    @Override
    public int registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        XxlJobRegistry xxlJobRegistry = new XxlJobRegistry();
        xxlJobRegistry.setRegistryGroup(registryGroup);
        xxlJobRegistry.setRegistryKey(registryKey);
        xxlJobRegistry.setRegistryValue(registryValue);
        xxlJobRegistry.setUpdateTime(updateTime);
        return this.save(xxlJobRegistry) ? 1 : 0;
    }

    @Override
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobRegistry::getRegistryGroup, registryGroup);
        queryWrapper.lambda().eq(XxlJobRegistry::getRegistryKey, registryKey);
        queryWrapper.lambda().eq(XxlJobRegistry::getRegistryValue, registryValue);
        return this.remove(queryWrapper) ? 1 : 0;
    }
}
