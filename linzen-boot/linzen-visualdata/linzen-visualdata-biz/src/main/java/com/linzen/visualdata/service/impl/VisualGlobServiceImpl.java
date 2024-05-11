package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualGlobEntity;
import com.linzen.visualdata.mapper.VisualGlobMapper;
import com.linzen.visualdata.model.visualglob.VisualGlobPaginationModel;
import com.linzen.visualdata.service.VisualGlobService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 全局变量
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualGlobServiceImpl extends SuperServiceImpl<VisualGlobMapper, VisualGlobEntity> implements VisualGlobService {

    @Override
    public List<VisualGlobEntity> getList(VisualGlobPaginationModel pagination) {
        QueryWrapper<VisualGlobEntity> queryWrapper = new QueryWrapper<>();
        Page<VisualGlobEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualGlobEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualGlobEntity> getList() {
        QueryWrapper<VisualGlobEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public VisualGlobEntity getInfo(String id) {
        QueryWrapper<VisualGlobEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualGlobEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualGlobEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualGlobEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualGlobEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }


}
