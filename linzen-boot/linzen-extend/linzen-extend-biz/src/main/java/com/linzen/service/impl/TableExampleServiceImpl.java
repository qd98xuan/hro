package com.linzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.entity.TableExampleEntity;
import com.linzen.mapper.TableExampleMapper;
import com.linzen.model.tableexample.PaginationTableExample;
import com.linzen.service.TableExampleService;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 表格示例数据
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class TableExampleServiceImpl extends SuperServiceImpl<TableExampleMapper, TableExampleEntity> implements TableExampleService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<TableExampleEntity> getList() {
        QueryWrapper<TableExampleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(TableExampleEntity::getProjectType).orderByAsc(TableExampleEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<TableExampleEntity> getList(String keyword) {
        QueryWrapper<TableExampleEntity> queryWrapper = new QueryWrapper<>();
        //关键字查询
        if (!StringUtils.isEmpty(keyword)){
            queryWrapper.lambda().and(t->t.like(TableExampleEntity::getCustomerName,keyword)
                .or().like(TableExampleEntity::getProjectName,keyword));
        }
        queryWrapper.lambda().orderByAsc(TableExampleEntity::getProjectType).orderByAsc(TableExampleEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<TableExampleEntity> getList(String typeId, PaginationTableExample paginationTableExample) {
        QueryWrapper<TableExampleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TableExampleEntity::getProjectType, typeId);
        //关键字（项目编码、项目名称、客户名称）
        String keyWord = paginationTableExample.getKeyword() != null ? paginationTableExample.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(TableExampleEntity::getProjectCode, keyWord)
                            .or().like(TableExampleEntity::getProjectName, keyWord)
                            .or().like(TableExampleEntity::getCustomerName, keyWord)
            );
        }
        //标签查询
        String sign = paginationTableExample.getF_Sign() != null ? paginationTableExample.getF_Sign() : null;
        if (!StringUtils.isEmpty(sign)) {
            String[] arraySign = sign.split(",");
            for (int i = 0; i < arraySign.length; i++) {
                String item = arraySign[i];
                if (i == 0) {
                    queryWrapper.lambda().like(TableExampleEntity::getProjectCode, item);
                } else {
                    queryWrapper.lambda().or(t -> t.like(TableExampleEntity::getProjectCode, item));
                }
            }
        }
        //排序
        if (StringUtils.isEmpty(paginationTableExample.getSidx())) {
            queryWrapper.lambda().orderByDesc(TableExampleEntity::getRegisterDate);
        } else {
            queryWrapper = "asc".equals(paginationTableExample.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTableExample.getSidx()) : queryWrapper.orderByDesc(paginationTableExample.getSidx());
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<TableExampleEntity> getList(PaginationTableExample paginationTableExample) {
        QueryWrapper<TableExampleEntity> queryWrapper = new QueryWrapper<>();
//                Map<String, Object> queryParam = OptimizeUtil.queryParam(pagination);
        //关键字（项目编码、项目名称、客户名称）
        String keyWord = paginationTableExample.getKeyword() != null ? paginationTableExample.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(TableExampleEntity::getProjectCode, keyWord)
                            .or().like(TableExampleEntity::getProjectName, keyWord)
                            .or().like(TableExampleEntity::getCustomerName, keyWord)
            );
        }
        //标签查询
        String sign = paginationTableExample.getF_Sign() != null ? paginationTableExample.getF_Sign() : null;
        if (!StringUtils.isEmpty(sign)) {
            String[] arraySign = sign.split(",");
            for (int i = 0; i < arraySign.length; i++) {
                String item = arraySign[i];
                if (i == 0) {
                    queryWrapper.lambda().like(TableExampleEntity::getProjectCode, item);
                } else {
                    queryWrapper.lambda().or(t -> t.like(TableExampleEntity::getProjectCode, item));
                }
            }
        }
        //排序
        if (StringUtils.isEmpty(paginationTableExample.getSidx())) {
            queryWrapper.lambda().orderByDesc(TableExampleEntity::getRegisterDate);
        } else {
            queryWrapper = "asc".equalsIgnoreCase(paginationTableExample.getSort()) ? queryWrapper.orderByAsc(paginationTableExample.getSidx()) : queryWrapper.orderByDesc(paginationTableExample.getSidx());
        }
        Page page=new Page(paginationTableExample.getCurrentPage(),paginationTableExample.getPageSize());
        IPage<TableExampleEntity> exampleEntityIPage=this.page(page,queryWrapper);
        return paginationTableExample.setData(exampleEntityIPage.getRecords(),page.getTotal());
    }

    @Override
    public TableExampleEntity getInfo(String id) {
        QueryWrapper<TableExampleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TableExampleEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(TableExampleEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(TableExampleEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setRegisterDate(new Date());
        entity.setRegistrant(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, TableExampleEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean rowEditing(TableExampleEntity entity) {
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

}