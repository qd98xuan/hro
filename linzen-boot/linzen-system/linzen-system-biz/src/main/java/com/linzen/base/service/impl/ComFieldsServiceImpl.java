package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.ComFieldsEntity;
import com.linzen.base.mapper.BaseComFieldsMapper;
import com.linzen.base.service.ComFieldsService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ComFieldsServiceImpl extends SuperServiceImpl<BaseComFieldsMapper, ComFieldsEntity> implements ComFieldsService {

	@Autowired
    private UserProvider userProvider;


    @Override
    public List<ComFieldsEntity> getList() {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ComFieldsEntity::getSortCode).orderByDesc(ComFieldsEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public ComFieldsEntity getInfo(String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getFieldName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ComFieldsEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }



    @Override
    public void create(ComFieldsEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, ComFieldsEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(ComFieldsEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}
