package com.linzen.engine.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.linzen.base.service.SuperService;
import com.linzen.engine.entity.FlowAuthorizeEntity;

import java.util.List;


/**
 * 流程权限表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
public interface FlowAuthorizeService extends SuperService<FlowAuthorizeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<FlowAuthorizeEntity> getList(String taskId, String nodeCode, SFunction<FlowAuthorizeEntity, ?>... columns);

    /**
     * 创建
     *
     * @param list 实体对象
     */
    void create(List<FlowAuthorizeEntity> list);

}
