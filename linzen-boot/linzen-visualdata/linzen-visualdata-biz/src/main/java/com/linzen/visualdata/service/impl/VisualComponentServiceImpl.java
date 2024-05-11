package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualComponentEntity;
import com.linzen.visualdata.mapper.VisualComponentMapper;
import com.linzen.visualdata.model.visualcomponent.VisualComponentPaginationModel;
import com.linzen.visualdata.service.VisualComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏组件库
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualComponentServiceImpl extends SuperServiceImpl<VisualComponentMapper, VisualComponentEntity> implements VisualComponentService {

    @Override
    public List<VisualComponentEntity> getList(VisualComponentPaginationModel pagination) {
        QueryWrapper<VisualComponentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualComponentEntity::getType, pagination.getType());
        Page<VisualComponentEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualComponentEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualComponentEntity> getList() {
        QueryWrapper<VisualComponentEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public VisualComponentEntity getInfo(String id) {
        QueryWrapper<VisualComponentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualComponentEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualComponentEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualComponentEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualComponentEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }


}
