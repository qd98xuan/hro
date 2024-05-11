package com.linzen.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleButtonEntity;
import com.linzen.base.mapper.ModuleButtonMapper;
import com.linzen.base.service.ModuleButtonService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 按钮权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ModuleButtonServiceImpl extends SuperServiceImpl<ModuleButtonMapper, ModuleButtonEntity> implements ModuleButtonService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleButtonEntity> getList() {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode)
                .orderByDesc(ModuleButtonEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getEnabledMarkList(String delFlag) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getEnabledMark,delFlag);
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode)
                .orderByDesc(ModuleButtonEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getListByModuleIds(String moduleId) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId);
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode)
                .orderByDesc(ModuleButtonEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getListByModuleIds(String moduleId, Pagination pagination) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId);
        //关键字查询
        if(!StringUtil.isEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(ModuleButtonEntity::getFullName,pagination.getKeyword())
                            .or().like(ModuleButtonEntity::getEnCode,pagination.getKeyword())
            );
        }
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode)
                .orderByDesc(ModuleButtonEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public ModuleButtonEntity getInfo(String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public ModuleButtonEntity getInfo(String id, String moduleId) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getId, id);
        queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String moduleId, String fullName, String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getFullName, fullName).eq(ModuleButtonEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleButtonEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String moduleId, String enCode, String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getEnCode, enCode);
        if(moduleId!=null){
            queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId);
        }
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleButtonEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(ModuleButtonEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ModuleButtonEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
       return this.updateById(entity);
    }

    @Override
    public void delete(ModuleButtonEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public List<ModuleButtonEntity> getListByModuleIds(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleButtonEntity::getModuleId, list);
        }
        queryWrapper.lambda().eq(ModuleButtonEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode).orderByDesc(ModuleButtonEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getListByIds(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleButtonEntity::getId, list);
        }
        queryWrapper.lambda().eq(ModuleButtonEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }


}
