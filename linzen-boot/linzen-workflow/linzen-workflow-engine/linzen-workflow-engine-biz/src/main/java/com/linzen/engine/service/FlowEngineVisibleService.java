package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowEngineVisibleEntity;
import com.linzen.engine.model.flowtask.FlowAssistModel;

import java.util.List;

/**
 * 流程可见
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowEngineVisibleService extends SuperService<FlowEngineVisibleEntity> {

    /**
     * 列表
     *
     * @param flowIdList 流程主键
     * @return
     */
    List<FlowEngineVisibleEntity> getList(List<String> flowIdList);

    /**
     * 列表
     *
     * @return
     */
    List<FlowEngineVisibleEntity> getList();

    /**
     * 可见流程列表
     *
     * @param userId 用户主键
     * @return
     */
    List<FlowEngineVisibleEntity> getVisibleFlowList(String userId);

    /**
     * 可见流程列表
     *
     * @param userId 用户主键
     * @return
     */
    List<FlowEngineVisibleEntity> getVisibleFlowList(String userId, Integer type);

    /**
     * 删除流程可见
     */
    void deleteVisible(String flowId);

    /**
     * 保存协管数据
     */
    void assistList(FlowAssistModel assistModel);
}
