package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowRejectDataEntity;
import com.linzen.engine.mapper.FlowRejectDataMapper;
import com.linzen.engine.service.FlowRejectDataService;
import com.linzen.util.RandomUtil;
import org.springframework.stereotype.Service;

/**
 * 冻结审批
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowRejectDataDataServiceImpl extends SuperServiceImpl<FlowRejectDataMapper, FlowRejectDataEntity> implements FlowRejectDataService {

    @Override
    public void createOrUpdate(FlowRejectDataEntity rejectEntity) {
        this.saveOrUpdate(rejectEntity);
    }

    @Override
    public void create(FlowRejectDataEntity rejectEntity) {
        if (rejectEntity.getId() == null) {
            rejectEntity.setId(RandomUtil.uuId());
        }
        this.save(rejectEntity);
    }

    @Override
    public void update(String id, FlowRejectDataEntity rejectEntity) {
        rejectEntity.setId(id);
        this.updateById(rejectEntity);
    }

    @Override
    public FlowRejectDataEntity getInfo(String id) {
        QueryWrapper<FlowRejectDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowRejectDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

}
