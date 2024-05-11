package com.linzen.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.ContractEntity;
import com.linzen.mapper.ContractMapper;
import com.linzen.service.ContractService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ContractServiceImpl extends SuperServiceImpl<ContractMapper, ContractEntity> implements ContractService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ContractEntity> getlist(Pagination pagination){
        //通过UserProvider获取用户信息
        QueryWrapper<ContractEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                t -> t.like(ContractEntity::getContractName, pagination.getKeyword())
                .or().like(ContractEntity::getMytelePhone, pagination.getKeyword())
            );
        }
        //排序
        if (StringUtil.isEmpty(pagination.getSidx())) {
        } else {
            queryWrapper = "asc".equals(pagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pagination.getSidx()) : queryWrapper.orderByDesc(pagination.getSidx());
        }
        Page<ContractEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<ContractEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public ContractEntity getInfo(String id){
        QueryWrapper<ContractEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ContractEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void create(ContractEntity entity){
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    @DSTransactional
    public void update(String id, ContractEntity entity){
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(ContractEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
