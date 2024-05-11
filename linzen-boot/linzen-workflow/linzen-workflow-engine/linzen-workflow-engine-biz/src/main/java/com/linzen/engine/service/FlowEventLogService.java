package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowEventLogEntity;

import java.util.List;

/**
 * 流程事件日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
public interface FlowEventLogService extends SuperService<FlowEventLogEntity> {

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowEventLogEntity entity);

    /**
     * 获取日志列表
     *
     * @param taskNodeId
     * @return
     */
    List<FlowEventLogEntity> getList(List<String> taskNodeId);

}
