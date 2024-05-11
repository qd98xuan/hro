package com.linzen.visualdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.util.ConnUtil;
import com.linzen.database.util.JdbcUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import com.linzen.visualdata.entity.VisualDbEntity;
import com.linzen.visualdata.mapper.VisualDbMapper;
import com.linzen.visualdata.model.VisualPagination;
import com.linzen.visualdata.service.VisualDbService;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class VisualDbServiceImpl extends SuperServiceImpl<VisualDbMapper, VisualDbEntity> implements VisualDbService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<VisualDbEntity> getList(VisualPagination pagination) {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDbEntity::getCreateTime);
        Page<VisualDbEntity> page = new Page<>(pagination.getCurrent(), pagination.getSize());
        IPage<VisualDbEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualDbEntity> getList() {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDbEntity::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualDbEntity getInfo(String id) {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDbEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualDbEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreateTime(new Date());
        entity.setUpdateUser(UserProvider.getLoginUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualDbEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(UserProvider.getLoginUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualDbEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean dbTest(VisualDbEntity entity) {
        boolean flag = false;
        try {
            @Cleanup Connection conn = ConnUtil.getConn(entity.getUsername(), entity.getPassword(), entity.getUrl());
            flag = conn != null;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Map<String, Object>> query(VisualDbEntity entity, String sql) {
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = JdbcUtil.queryList(new PrepSqlDTO(sql).withConn(entity.getUsername(), entity.getPassword(), entity.getUrl())).setIsLowerCase(true).get();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return data;
    }

}
