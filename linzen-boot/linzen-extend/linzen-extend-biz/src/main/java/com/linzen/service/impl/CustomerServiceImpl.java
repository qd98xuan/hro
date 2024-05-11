package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.CustomerEntity;
import com.linzen.mapper.CustomerMapper;
import com.linzen.service.CustomerService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 客户信息
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service

public class CustomerServiceImpl extends SuperServiceImpl<CustomerMapper, CustomerEntity> implements CustomerService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<CustomerEntity> getList(Pagination pagination) {
        QueryWrapper<CustomerEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t->t.like(CustomerEntity::getAddress, pagination.getKeyword())
                            .or().like(CustomerEntity::getName, pagination.getKeyword())
                            .or().like(CustomerEntity::getCode, pagination.getKeyword())
            );
        }
        Page<CustomerEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<CustomerEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Override
    public CustomerEntity getInfo(String id) {
        QueryWrapper<CustomerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerEntity::getId, id);
        return this.getOne(queryWrapper);

    }

    @Override
    public void create(CustomerEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        this.save(entity);
    }

    @Override
    public boolean update(String id, CustomerEntity entity) {
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(new Date());
        return this.updateById(entity);
    }

    @Override
    public void delete(CustomerEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
