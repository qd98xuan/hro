package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import com.linzen.visualdata.entity.VisualConfigEntity;
import com.linzen.visualdata.entity.VisualEntity;
import com.linzen.visualdata.mapper.VisualMapper;
import com.linzen.visualdata.model.visual.VisualPaginationModel;
import com.linzen.visualdata.service.VisualConfigService;
import com.linzen.visualdata.service.VisualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 大屏基本信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualServiceImpl extends SuperServiceImpl<VisualMapper, VisualEntity> implements VisualService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private VisualConfigService configService;

    @Override
    public List<VisualEntity> getList(VisualPaginationModel pagination) {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualEntity::getCategory, pagination.getCategory());
        queryWrapper.lambda().orderByDesc(VisualEntity::getCreateTime);
        Page<VisualEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualEntity> getList() {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualEntity::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualEntity getInfo(String id) {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualEntity entity, VisualConfigEntity configEntity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreateTime(new Date());
        entity.setUpdateUser(UserProvider.getLoginUserId());
        entity.setStatus(1);
        entity.setIsDeleted(0);
        this.save(entity);
        configEntity.setVisualId(entity.getId());
        configService.create(configEntity);
    }

    @Override
    public boolean update(String id, VisualEntity entity, VisualConfigEntity configEntity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(UserProvider.getLoginUserId());
        boolean flag = this.updateById(entity);
        if (configEntity != null) {
            configService.update(configEntity.getId(), configEntity);
        }
        return flag;
    }

    @Override
    public void delete(VisualEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
            VisualConfigEntity config = configService.getInfo(entity.getId());
            configService.delete(config);
        }
    }

    @Override
    public void createImport(VisualEntity entity, VisualConfigEntity configEntity) throws DataBaseException {
        try {
            this.save(entity);
            configService.create(configEntity);
        }catch (Exception e){
            throw new DataBaseException(MsgCode.IMP003.get());
        }

    }
}
