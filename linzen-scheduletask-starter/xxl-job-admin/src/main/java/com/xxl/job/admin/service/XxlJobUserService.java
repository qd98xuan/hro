package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.core.model.XxlJobUser;

import java.util.List;

public interface XxlJobUserService extends IService<XxlJobUser> {

    List<XxlJobUser> pageList(int offset,
                              int pagesize,
                              String username,
                              int role);

    int pageListCount(int offset,
                      int pagesize,
                      String username,
                      int role);

    XxlJobUser loadByUserName(String username);

    int create(XxlJobUser xxlJobUser);

    int update(XxlJobUser xxlJobUser);

    int delete(String id);

}
