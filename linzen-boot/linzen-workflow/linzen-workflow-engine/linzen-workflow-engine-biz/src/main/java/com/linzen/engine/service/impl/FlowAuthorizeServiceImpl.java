package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowAuthorizeEntity;
import com.linzen.engine.mapper.FlowAuthorizeMapper;
import com.linzen.engine.service.FlowAuthorizeService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程权限表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Service
public class FlowAuthorizeServiceImpl extends SuperServiceImpl<FlowAuthorizeMapper, FlowAuthorizeEntity> implements FlowAuthorizeService {

    @Override
    public List<FlowAuthorizeEntity> getList(String taskId, String nodeCode, SFunction<FlowAuthorizeEntity, ?>... columns) {
        QueryWrapper<FlowAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowAuthorizeEntity::getTaskId, taskId);
        if (StringUtil.isNotEmpty(nodeCode)) {
            queryWrapper.lambda().eq(FlowAuthorizeEntity::getNodeCode, nodeCode);
        }
        queryWrapper.lambda().select(columns);
        return this.list(queryWrapper);
    }

    @Override
    public void create(List<FlowAuthorizeEntity> list) {
        for (FlowAuthorizeEntity entity : list) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
        }
    }
}
