package com.linzen.engine.service;

import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowRejectDataEntity;

/**
 * 冻结审批
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowRejectDataService extends SuperService<FlowRejectDataEntity> {

    /**
     * 新增
     *
     * @param rejectEntity
     */
    void createOrUpdate(FlowRejectDataEntity rejectEntity);

    /**
     * 新增
     *
     * @param rejectEntity
     */
    void create(FlowRejectDataEntity rejectEntity);

    /**
     * 更新
     *
     * @param rejectEntity
     */
    void update(String id, FlowRejectDataEntity rejectEntity);

    /**
     * 获取信息
     *
     * @param id
     * @return
     */
    FlowRejectDataEntity getInfo(String id);

}
