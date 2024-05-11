package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.ProductEntryEntity;
import com.linzen.mapper.ProductEntryMapper;
import com.linzen.service.ProductEntryService;
import com.linzen.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 销售订单明细
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class ProductEntryServiceImpl extends SuperServiceImpl<ProductEntryMapper, ProductEntryEntity> implements ProductEntryService {

    @Override
    public List<ProductEntryEntity> getProductentryEntityList(String id) {
        QueryWrapper<ProductEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProductEntryEntity::getProductId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProductEntryEntity> getProductentryEntityList(Pagination pagination) {
        QueryWrapper<ProductEntryEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtil.isNotEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(ProductEntryEntity::getProductName, pagination.getKeyword())
                        .or().like(ProductEntryEntity::getProductCode, pagination.getKeyword())
                        .or().like(ProductEntryEntity::getProductSpecification, pagination.getKeyword())
            );
        }
        Page<ProductEntryEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<ProductEntryEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

}
