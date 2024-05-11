package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowEventLogEntity;
import com.linzen.engine.mapper.FlowEventLogMapper;
import com.linzen.engine.service.FlowEventLogService;
import com.linzen.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程事件日志
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Service
public class FlowEventLogServiceImpl extends SuperServiceImpl<FlowEventLogMapper, FlowEventLogEntity> implements FlowEventLogService {


    @Override
    public void create(FlowEventLogEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public List<FlowEventLogEntity> getList(List<String> nodeIdList) {
        QueryWrapper<FlowEventLogEntity> queryWrapper = new QueryWrapper<>();
        if (nodeIdList.size() > 0) {
            queryWrapper.lambda().in(FlowEventLogEntity::getTaskNodeId, nodeIdList);
        }
        return this.list(queryWrapper);
    }
}
