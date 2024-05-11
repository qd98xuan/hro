package com.linzen.base.service;

import com.linzen.base.entity.ScheduleNewUserEntity;

import java.util.List;

/**
 * 日程
 *
 * @author FHNP
 * @copyright 领致信息技术有限公司
 */
public interface ScheduleNewUserService extends SuperService<ScheduleNewUserEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<ScheduleNewUserEntity> getList(String scheduleId,Integer type);

    /**
     * 列表
     *
     * @return
     */
    List<ScheduleNewUserEntity> getList();

    /**
     * 创建
     *
     * @param entity 实体
     */
    void create(ScheduleNewUserEntity entity);

    /**
     * 删除
     *
     */
    void deleteByScheduleId(List<String> scheduleIdList);

    /**
     * 删除
     *
     */
    void deleteByUserId(List<String> scheduleIdList);
}
