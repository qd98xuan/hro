package com.linzen.visualdata.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.visualdata.entity.VisualCategoryEntity;
import com.linzen.visualdata.mapper.VisualCategoryMapper;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.service.VisualCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏分类
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualCategoryServiceImpl extends SuperServiceImpl<VisualCategoryMapper, VisualCategoryEntity> implements VisualCategoryService {

    @Override
    public List<VisualCategoryEntity> getList(VisualPagination pagination) {
        QueryWrapper<VisualCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(VisualCategoryEntity::getCategoryKey);
        Page<VisualCategoryEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualCategoryEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualCategoryEntity> getList() {
        QueryWrapper<VisualCategoryEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public boolean isExistByValue(String value, String id) {
        QueryWrapper<VisualCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualCategoryEntity::getCategoryValue, value);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(VisualCategoryEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public VisualCategoryEntity getInfo(String id) {
        QueryWrapper<VisualCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualCategoryEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualCategoryEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualCategoryEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualCategoryEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
