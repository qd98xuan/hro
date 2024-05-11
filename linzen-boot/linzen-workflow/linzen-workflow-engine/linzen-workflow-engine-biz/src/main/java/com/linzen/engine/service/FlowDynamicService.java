package com.linzen.engine.service;

import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.engine.model.flowengine.FlowModel;
import com.linzen.engine.model.flowengine.shuntjson.childnode.ChildNode;
import com.linzen.exception.WorkFlowException;

/**
 * 在线开发工作流
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowDynamicService {

    /**
     * 流程数据
     *
     * @param flowModel
     */
    void flowTask(FlowModel flowModel, FlowStatusEnum flowStatus, ChildNode childNode) throws WorkFlowException;

    /**
     * 保存流程
     *
     * @param flowModel
     * @throws WorkFlowException
     */
    void createOrUpdate(FlowModel flowModel) throws WorkFlowException;

    /**
     * 批量保存流程
     *
     * @param flowModel
     * @throws WorkFlowException
     */
    void batchCreateOrUpdate(FlowModel flowModel) throws WorkFlowException;
}
