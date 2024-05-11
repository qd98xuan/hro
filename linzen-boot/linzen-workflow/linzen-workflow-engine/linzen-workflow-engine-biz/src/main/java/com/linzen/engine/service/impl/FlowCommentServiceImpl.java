package com.linzen.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.engine.entity.FlowCommentEntity;
import com.linzen.engine.mapper.FlowCommentMapper;
import com.linzen.engine.model.flowcomment.FlowCommentPagination;
import com.linzen.engine.service.FlowCommentService;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 流程评论
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 */
@Service
public class FlowCommentServiceImpl extends SuperServiceImpl<FlowCommentMapper, FlowCommentEntity> implements FlowCommentService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<FlowCommentEntity> getlist(FlowCommentPagination pagination) {
        QueryWrapper<FlowCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowCommentEntity::getTaskId, pagination.getTaskId());
        queryWrapper.lambda().orderByDesc(FlowCommentEntity::getCreatorTime);
        Page<FlowCommentEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<FlowCommentEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public FlowCommentEntity getInfo(String id) {
        QueryWrapper<FlowCommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowCommentEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(FlowCommentEntity entity) {
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowCommentEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(FlowCommentEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
