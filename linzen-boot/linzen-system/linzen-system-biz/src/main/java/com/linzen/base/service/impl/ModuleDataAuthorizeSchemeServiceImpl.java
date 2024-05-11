package com.linzen.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;
import com.linzen.base.mapper.ModuleDataAuthorizeSchemeMapper;
import com.linzen.base.service.ModuleDataAuthorizeSchemeService;
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
 * 数据权限方案
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ModuleDataAuthorizeSchemeServiceImpl extends SuperServiceImpl<ModuleDataAuthorizeSchemeMapper, ModuleDataAuthorizeSchemeEntity> implements ModuleDataAuthorizeSchemeService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getList() {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        // 排序
        queryWrapper.lambda().orderByDesc(ModuleDataAuthorizeSchemeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getEnabledMarkList(String delFlag) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getEnabledMark, delFlag);
        // 排序
        queryWrapper.lambda().orderByDesc(ModuleDataAuthorizeSchemeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getList(String moduleId) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getModuleId, moduleId);
        // 排序
        queryWrapper.lambda().orderByDesc(ModuleDataAuthorizeSchemeEntity::getCreatorTime);
        // 排序
        return this.list(queryWrapper);
    }

    @Override
    public ModuleDataAuthorizeSchemeEntity getInfo(String id) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ModuleDataAuthorizeSchemeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(1);
        entity.setSortCode(RandomUtil.parses());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ModuleDataAuthorizeSchemeEntity entity) {
        entity.setId(id);
        entity.setEnabledMark(1);
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(ModuleDataAuthorizeSchemeEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public Boolean isExistByFullName(String id, String fullName, String moduleId) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getModuleId, moduleId);
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getFullName, fullName);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(ModuleDataAuthorizeSchemeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean isExistByEnCode(String id, String enCode, String moduleId) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getModuleId, moduleId);
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getEnCode, enCode);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(ModuleDataAuthorizeSchemeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean isExistAllData(String moduleId) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getModuleId, moduleId);
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getAllData, 1);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getListByModuleId(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleDataAuthorizeSchemeEntity::getModuleId, list);
        }
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(ModuleDataAuthorizeSchemeEntity::getSortCode).orderByDesc(ModuleDataAuthorizeSchemeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getListByIds(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        List<List<String>> lists = Lists.partition(ids, 1000);
        for (List<String> list : lists) {
            queryWrapper.lambda().or().in(ModuleDataAuthorizeSchemeEntity::getId, list);
        }
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }
}
