package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.ProductclassifyEntity;
import com.linzen.mapper.ProductclassifyMapper;
import com.linzen.service.ProductclassifyService;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * 产品分类
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Service
public class ProductclassifyServiceImpl extends SuperServiceImpl<ProductclassifyMapper, ProductclassifyEntity> implements ProductclassifyService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ProductclassifyEntity> getList(){
        QueryWrapper<ProductclassifyEntity> queryWrapper=new QueryWrapper<>();
        return list(queryWrapper);
    }

    @Override
    public ProductclassifyEntity getInfo(String id){
        QueryWrapper<ProductclassifyEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(ProductclassifyEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ProductclassifyEntity entity){
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ProductclassifyEntity entity){
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(new Date());
        return this.updateById(entity);
    }
    @Override
    public void delete(ProductclassifyEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }

}
