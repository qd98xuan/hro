package com.linzen.service;

import com.linzen.base.service.SuperService;
import com.linzen.entity.FlowFormRelationEntity;

import java.util.List;

/**
 * 流程表单关联
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface FlowFormRelationService extends SuperService<FlowFormRelationEntity> {
    /**
     * 根据流程id保存关联表单
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    void saveFlowIdByFormIds(String flowId,   List<String> formIds);
    /**
     * 根据表单id查询是否存在引用
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    List<FlowFormRelationEntity> getListByFormId(String formId);
}
