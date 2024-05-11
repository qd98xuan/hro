package com.linzen.base.service.impl;


import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.mapper.DbLinkMapper;
import com.linzen.base.model.dblink.PaginationDbLink;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.MsgCode;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.source.DbBase;
import com.linzen.database.util.*;
import com.linzen.exception.DataBaseException;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.TenantHolder;
import lombok.Cleanup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * 数据连接
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class DbLinkServiceImpl extends SuperServiceImpl<DbLinkMapper, DbLinkEntity> implements DbLinkService, InitializingBean {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;

    @Override
    public List<DbLinkEntity> getList() {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(DbLinkEntity::getSortCode)
                .orderByDesc(DbLinkEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DbLinkEntity> getList(PaginationDbLink pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(DbLinkEntity::getFullName, pagination.getKeyword())
                            .or().like(DbLinkEntity::getHost, pagination.getKeyword())
            );
        }
        if (StringUtil.isNotEmpty(pagination.getDbType())) {
            flag = true;
            queryWrapper.lambda().eq(DbLinkEntity::getDbType, pagination.getDbType());
        }
        queryWrapper.lambda().orderByAsc(DbLinkEntity::getSortCode)
                .orderByDesc(DbLinkEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(DbLinkEntity::getUpdateTime);
        }
        Page<DbLinkEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DbLinkEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public DbLinkEntity getInfo(String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DbLinkEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public void create(DbLinkEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, DbLinkEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(DbLinkEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DbLinkEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(DbLinkEntity::getSortCode, upSortCode)
                .orderByDesc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DbLinkEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(DbLinkEntity::getSortCode, upSortCode)
                .orderByAsc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            downEntity.setUpdateTime(new Date());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public boolean testDbConnection(DbLinkEntity entity) {
        //判断字典数据类型编码是否错误，大小写不敏感
        DbBase db = DbTypeUtil.getDb(entity);
        if(db == null){
            throw new DataBaseException(MsgCode.DB001.get());
        }
        try{
            @Cleanup Connection conn = ConnUtil.getConn(entity.getUserName(), entity.getPassword(), ConnUtil.getUrl(entity));
            return conn != null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置数据源
     * @param dbLinkId 数据连接id
     * @throws DataBaseException ignore
     */
    @Override
    public DbLinkEntity getResource(String dbLinkId) throws Exception {
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        //多租户是否开启
        if("0".equals(dbLinkId)){
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                // 默认数据库, 租户管理指定租户数据源
                dbLinkEntity = TenantDataSourceUtil.getTenantAssignDataSource(TenantHolder.getDatasourceId()).toDbLink(new DbLinkEntity());
                dbLinkEntity.setId("0");
            }else {
                // 默认数据库查询，从配置获取数据源信息
                BeanUtils.copyProperties(dataSourceUtils, dbLinkEntity);
                dbLinkEntity.setId("0");
                // 是系统默认的多租户
                TenantDataSourceUtil.initDataSourceTenantDbName(dbLinkEntity);
            }
        }else {
            try {
                DynamicDataSourceUtil.switchToDataSource(null);
                dbLinkEntity = dblinkService.getInfo(dbLinkId);
            }finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        }
        // 添加并且切换数据源
        return dbLinkEntity;
    }

    @Override
    public void afterPropertiesSet(){
        PrepSqlDTO.DB_LINK_FUN = (dbLinkId)-> {
            try {
                return (DbLinkEntity) getResource(dbLinkId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }

}
