package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.entity.InterfaceOauthEntity;
import com.linzen.base.mapper.InterfaceOauthMapper;
import com.linzen.base.model.InterfaceOauth.PaginationOauth;
import com.linzen.base.service.InterfaceOauthService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.exception.DataBaseException;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口认证服务
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class InterfaceOauthServiceImpl extends SuperServiceImpl<InterfaceOauthMapper, InterfaceOauthEntity> implements InterfaceOauthService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public boolean isExistByAppName(String appName, String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppName, appName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(InterfaceOauthEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByAppId(String appId, String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppId, appId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(InterfaceOauthEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public List<InterfaceOauthEntity> getList(PaginationOauth pagination) {
        boolean flag = false;
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        //查询关键字
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(InterfaceOauthEntity::getAppId, pagination.getKeyword())
                            .or().like(InterfaceOauthEntity::getAppName, pagination.getKeyword())
            );
        }
        if (pagination.getEnabledMark() != null) {
            queryWrapper.lambda().eq(InterfaceOauthEntity::getEnabledMark, pagination.getEnabledMark());
        }
        //排序
        queryWrapper.lambda().orderByAsc(InterfaceOauthEntity::getSortCode)
                .orderByDesc(InterfaceOauthEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(InterfaceOauthEntity::getUpdateTime);
        }
        Page<InterfaceOauthEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<InterfaceOauthEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public InterfaceOauthEntity getInfo(String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(InterfaceOauthEntity entity) {
        if (entity.getId() == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            entity.setCreatorTime(DateUtil.getNowDate());
        }
        this.save(entity);
    }

    @Override
    public boolean update(InterfaceOauthEntity entity, String id) throws DataBaseException {
        entity.setId(id);
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(InterfaceOauthEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public InterfaceOauthEntity getInfoByAppId(String appId) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppId, appId);
        return this.getOne(queryWrapper);
    }
}
