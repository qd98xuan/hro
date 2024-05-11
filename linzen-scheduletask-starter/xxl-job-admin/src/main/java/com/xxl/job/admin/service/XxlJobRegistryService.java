package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.core.model.XxlJobRegistry;

import java.util.Date;
import java.util.List;

public interface XxlJobRegistryService extends IService<XxlJobRegistry> {

    List<String> findDead(int timeout,
                                  Date nowTime);

    int removeDead(List<String> ids);

    List<XxlJobRegistry> findAll(int timeout,
                                        Date nowTime);

    int registryUpdate(String registryGroup,
                              String registryKey,
                              String registryValue,
                              Date updateTime);

    int registrySave(String registryGroup,
                            String registryKey,
                            String registryValue,
                            Date updateTime);

    int registryDelete(String registryGroup,
                              String registryKey,
                              String registryValue);
}
