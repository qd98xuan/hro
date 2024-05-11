package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linzen.scheduletask.entity.HandlerNameEntity;

import java.util.List;

/**
 * 任务名称业务类
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface HandlerNameService extends IService<HandlerNameEntity> {

    /**
     * 创建任务名称
     *
     * @param entity
     * @return
     */
    boolean create(HandlerNameEntity entity);

    /**
     * 创建任务名称
     *
     * @param entity
     * @return
     */
    boolean delete(HandlerNameEntity entity);

    /**
     * 获取本地方法
     *
     * @return
     */
    List<HandlerNameEntity> queryList();

    /**
     * 删除所有数据
     *
     * @return
     */
    boolean removeAll();

    /**
     * 获取本地方法
     *
     * @param localHostTaskId
     * @return
     */
    HandlerNameEntity getInfo(String localHostTaskId);
}
