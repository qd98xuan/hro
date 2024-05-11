package com.linzen.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.Pagination;
import com.linzen.base.entity.VisualDataMapEntity;
import com.linzen.base.mapper.DataMapMapper;
import com.linzen.base.service.DataMapService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏地图
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class DataMapServiceImpl extends SuperServiceImpl<DataMapMapper, VisualDataMapEntity> implements DataMapService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<VisualDataMapEntity> getList(Pagination pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(VisualDataMapEntity::getFullName, pagination.getKeyword())
                            .or().like(VisualDataMapEntity::getEnCode, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByAsc(VisualDataMapEntity::getSortCode)
                .orderByDesc(VisualDataMapEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(VisualDataMapEntity::getUpdateTime);
        }
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<VisualDataMapEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages.getRecords(), page.getTotal());
    }

    @Override
    public List<VisualDataMapEntity> getList() {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDataMapEntity::getSortCode)
                .orderByDesc(VisualDataMapEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualDataMapEntity getInfo(String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualDataMapEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUser(userProvider.get().getUserId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualDataMapEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualDataMapEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(VisualDataMapEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(VisualDataMapEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

}
