package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.FlowFormRelationEntity;
import com.linzen.mapper.FlowFormRelationMapper;
import com.linzen.service.FlowFormRelationService;
import com.linzen.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程表单关联
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class FlowFormRelationServiceImpl extends SuperServiceImpl<FlowFormRelationMapper, FlowFormRelationEntity> implements FlowFormRelationService {

    @Override
    public void saveFlowIdByFormIds(String flowId, List<String> formIds) {
        QueryWrapper<FlowFormRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowFormRelationEntity::getFlowId, flowId);
        List<FlowFormRelationEntity> list = this.list(queryWrapper);
        this.removeBatchByIds(list);
        if(CollectionUtils.isNotEmpty(formIds)){
            for(String formId:formIds){
                FlowFormRelationEntity entity=new FlowFormRelationEntity();
                entity.setFlowId(flowId);
                entity.setId(RandomUtil.uuId());
                entity.setFormId(formId);
                this.save(entity);
            }
        }
    }

    @Override
    public List<FlowFormRelationEntity> getListByFormId(String formId) {
        QueryWrapper<FlowFormRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowFormRelationEntity::getFormId, formId);
        List<FlowFormRelationEntity> list = this.list(queryWrapper);
        return list;
    }
}
