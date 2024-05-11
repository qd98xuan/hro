package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowTaskCirculateEntity;

import java.util.List;

/**
 * 流程传阅
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowTaskCirculateService extends SuperService<FlowTaskCirculateEntity> {

    /**
     * 删除（根据实例Id）
     *
     * @param taskId 任务主键
     * @return
     */
    void deleteByTaskId(String taskId);

    /**
     * 创建
     *
     * @param entitys 实体对象
     * @return
     */
    void create(List<FlowTaskCirculateEntity> entitys);

    /**
     * 获取列表
     *
     * @param taskId
     * @return
     */
    List<FlowTaskCirculateEntity> getList(String taskId);
}
