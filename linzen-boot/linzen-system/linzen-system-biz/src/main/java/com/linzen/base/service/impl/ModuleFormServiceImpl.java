package com.linzen.base.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleFormEntity;
import com.linzen.base.mapper.ModuleFormMapper;
import com.linzen.base.service.ModuleFormService;
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
 * 表单权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ModuleFormServiceImpl extends SuperServiceImpl<ModuleFormMapper, ModuleFormEntity> implements ModuleFormService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleFormEntity> getList() {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleFormEntity::getSortCode)
                .orderByDesc(ModuleFormEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleFormEntity> getEnabledMarkList(String delFlag) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getEnabledMark,delFlag);
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleFormEntity::getSortCode)
                .orderByDesc(ModuleFormEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleFormEntity> getList(String moduleId, Pagination pagination) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getModuleId, moduleId);
        if(!StringUtil.isEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t-> t.like(ModuleFormEntity::getEnCode,pagination.getKeyword()).or().like(ModuleFormEntity::getFullName,pagination.getKeyword())
            );
        }
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleFormEntity::getSortCode)
                .orderByDesc(ModuleFormEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleFormEntity> getList(String moduleId) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getModuleId, moduleId);
        // 排序
        queryWrapper.lambda().orderByAsc(ModuleFormEntity::getSortCode)
                .orderByDesc(ModuleFormEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public ModuleFormEntity getInfo(String id) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public ModuleFormEntity getInfo(String id, String moduleId) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getId, id);
        queryWrapper.lambda().eq(ModuleFormEntity::getModuleId, moduleId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String moduleId, String fullName, String id) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getFullName, fullName).eq(ModuleFormEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleFormEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String moduleId, String enCode, String id) {
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleFormEntity::getEnCode, enCode).eq(ModuleFormEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleFormEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(ModuleFormEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    @DSTransactional
    public void create(List<ModuleFormEntity> entitys) {
        Long sortCode = RandomUtil.parses();
        String userId = userProvider.get().getUserId();
        for (ModuleFormEntity entity : entitys) {
            entity.setId(RandomUtil.uuId());
            entity.setSortCode(sortCode++);
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark()))?0:1);
            entity.setCreatorUserId(userId);
            this.save(entity);
        }
    }

    @Override
    public boolean update(String id, ModuleFormEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(ModuleFormEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public List<ModuleFormEntity> getListByModuleId(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleFormEntity::getModuleId, list);
        }
        queryWrapper.lambda().eq(ModuleFormEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(ModuleFormEntity::getSortCode).orderByDesc(ModuleFormEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleFormEntity> getListByIds(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleFormEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleFormEntity::getId, list);
        }
        queryWrapper.lambda().eq(ModuleFormEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }


}
