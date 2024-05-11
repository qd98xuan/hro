package com.linzen.form.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.form.entity.LeaveApplyEntity;
import com.linzen.form.mapper.LeaveApplyMapper;
import com.linzen.form.model.leaveapply.LeaveApplyForm;
import com.linzen.form.service.LeaveApplyService;
import com.linzen.util.JsonUtil;
import com.linzen.util.ServiceAllUtil;
import com.linzen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 流程表单【请假申请】
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class LeaveApplyServiceImpl extends SuperServiceImpl<LeaveApplyMapper, LeaveApplyEntity> implements LeaveApplyService {

    @Autowired
    private ServiceAllUtil serviceAllUtil;

    @Override
    public LeaveApplyEntity getInfo(String id) {
        QueryWrapper<LeaveApplyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LeaveApplyEntity::getId, id);
        return getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, LeaveApplyEntity entity, LeaveApplyForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            save(entity);
            serviceAllUtil.useBillNumber("WF_LeaveApplyNo");
        } else {
            entity.setId(id);
            updateById(entity);
        }
    }

    @Override
    @DSTransactional
    public void submit(String id, LeaveApplyEntity entity, LeaveApplyForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            save(entity);
            serviceAllUtil.useBillNumber("WF_LeaveApplyNo");
        } else {
            entity.setId(id);
            updateById(entity);
        }
    }

    @Override
    public void data(String id, String data) {
        LeaveApplyForm leaveApplyForm = JsonUtil.createJsonToBean(data, LeaveApplyForm.class);
        LeaveApplyEntity entity = BeanUtil.toBean(leaveApplyForm, LeaveApplyEntity.class);
        entity.setId(id);
        saveOrUpdate(entity);
    }

}
