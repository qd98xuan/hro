package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualMapEntity;
import com.linzen.visualdata.mapper.VisualMapMapper;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.service.VisualMapService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏地图配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualMapServiceImpl extends SuperServiceImpl<VisualMapMapper, VisualMapEntity> implements VisualMapService {

    @Override
    public List<VisualMapEntity> getList(VisualPagination pagination) {
        return getListWithColnums(pagination);
    }


    @SafeVarargs
    @Override
    public final List<VisualMapEntity> getListWithColnums(VisualPagination pagination, SFunction<VisualMapEntity, ?>... columns) {
        QueryWrapper<VisualMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(columns).orderByAsc(VisualMapEntity::getId);
        Page<VisualMapEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualMapEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public VisualMapEntity getInfo(String id) {
        QueryWrapper<VisualMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualMapEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualMapEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualMapEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualMapEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
