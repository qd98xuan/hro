package com.linzen.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.ModuleDataAuthorizeEntity;
import com.linzen.base.mapper.ModuleDataAuthorizeMapper;
import com.linzen.base.service.ModuleDataAuthorizeService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.util.DateUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据权限配置
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class ModuleDataAuthorizeServiceImpl extends SuperServiceImpl<ModuleDataAuthorizeMapper, ModuleDataAuthorizeEntity> implements ModuleDataAuthorizeService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleDataAuthorizeEntity> getList() {
        QueryWrapper<ModuleDataAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        // 排序
        queryWrapper.lambda().orderByDesc(ModuleDataAuthorizeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleDataAuthorizeEntity> getList(String moduleId) {
        QueryWrapper<ModuleDataAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getModuleId, moduleId);
        // 排序
        queryWrapper.lambda().orderByDesc(ModuleDataAuthorizeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public ModuleDataAuthorizeEntity getInfo(String id) {
        QueryWrapper<ModuleDataAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ModuleDataAuthorizeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(1);
        entity.setSortCode(RandomUtil.parses());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ModuleDataAuthorizeEntity entity) {
        entity.setId(id);
        entity.setEnabledMark(1);
        entity.setUpdateTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(ModuleDataAuthorizeEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByEnCode(String moduleId, String enCode, String id) {
        QueryWrapper<ModuleDataAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getModuleId, moduleId);
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getEnCode, enCode);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isExistByFullName(String moduleId, String fullName, String id) {
        QueryWrapper<ModuleDataAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getModuleId, moduleId);
        queryWrapper.lambda().eq(ModuleDataAuthorizeEntity::getFullName, fullName);
        return this.count(queryWrapper) > 0;
    }


}
