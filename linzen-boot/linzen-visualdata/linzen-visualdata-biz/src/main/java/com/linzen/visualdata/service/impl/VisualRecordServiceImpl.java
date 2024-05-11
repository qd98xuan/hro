package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualRecordEntity;
import com.linzen.visualdata.mapper.VisualRecordMapper;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.service.VisualRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏数据集
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualRecordServiceImpl extends SuperServiceImpl<VisualRecordMapper, VisualRecordEntity> implements VisualRecordService {

    @Override
    public List<VisualRecordEntity> getList(VisualPagination pagination) {
        QueryWrapper<VisualRecordEntity> queryWrapper = new QueryWrapper<>();
        Page<VisualRecordEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualRecordEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualRecordEntity> getList() {
        QueryWrapper<VisualRecordEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public VisualRecordEntity getInfo(String id) {
        QueryWrapper<VisualRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualRecordEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualRecordEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualRecordEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualRecordEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }


}
