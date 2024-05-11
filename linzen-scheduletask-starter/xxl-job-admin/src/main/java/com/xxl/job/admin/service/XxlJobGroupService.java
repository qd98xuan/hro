package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linzen.scheduletask.entity.XxlJobGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxlJobGroupService extends IService<XxlJobGroup> {

    List<XxlJobGroup> findAll();

    List<XxlJobGroup> findByAddressType(int addressType);

    int create(XxlJobGroup xxlJobGroup);

    int update(XxlJobGroup xxlJobGroup);

    int remove(long id);

    XxlJobGroup load(String id);

    List<XxlJobGroup> pageList(int offset,
                               int pagesize,
                               String appname,
                               String title);

    int pageListCount(int offset,
                      int pagesize,
                      String appname,
                      String title);

    /**
     * 通过Appname查询分组
     *
     * @param appname
     * @return
     */
    List<XxlJobGroup> findByAppname(String appname);

    /**
     * 通过appname修改数据
     *
     * @param xxlJobGroup
     * @return
     */
    int updateByAppname(XxlJobGroup xxlJobGroup);

}
