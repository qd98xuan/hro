package com.linzen.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.mapper.SystemMapper;
import com.linzen.base.model.base.SystemBaeModel;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.SystemService;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.PermissionGroupService;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class SystemServiceImpl extends SuperServiceImpl<SystemMapper, SysSystemEntity> implements SystemService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AuthorizeService authorizeApi;
    @Autowired
    private OrganizeAdministratorService organizeAdminTratorApi;
    @Autowired
    private PermissionGroupService permissionGroupApi;

    @Override
    public List<SysSystemEntity> getList() {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(SysSystemEntity::getSortCode)
                .orderByDesc(SysSystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysSystemEntity> getList(String keyword, Boolean filterEnableMark, boolean verifyAuth, Boolean filterMain, boolean isList, List<String> moduleAuthorize) {
        List<String> systemIds = new ArrayList<>();
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        // 是否为列表，特殊处理
        if (isList) {
            systemIds.addAll(authorizeApi.getAuthorizeByUser(true).getSystemList().stream().map(SystemBaeModel::getId).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(keyword)) {
            queryWrapper.lambda().and(t ->
                    t.like(SysSystemEntity::getFullName, keyword).or().like(SysSystemEntity::getEnCode, keyword)
                            .or().like(SysSystemEntity::getDescription, keyword)
            );
        }
        if (filterEnableMark == null) {
            queryWrapper.lambda().eq(SysSystemEntity::getEnabledMark, 0);
        } else if (filterEnableMark) {
            queryWrapper.lambda().eq(SysSystemEntity::getEnabledMark, 1);
        }
        if (verifyAuth) {
            List<String> systemList = authorizeApi.getAuthorizeByUser(false).getSystemList()
                    .stream().map(SystemBaeModel::getId).collect(Collectors.toList());
            if (systemList.size() == 0) {
                return new ArrayList<>();
            }
            systemIds.addAll(systemList);
            queryWrapper.lambda().in(SysSystemEntity::getId, systemIds);
        }
        // 过滤掉开发平台
        if (filterMain != null && filterMain) {
            queryWrapper.lambda().ne(SysSystemEntity::getEnCode, LinzenConst.MAIN_SYSTEM_CODE);
        }
        if (moduleAuthorize.size() > 0) {
            queryWrapper.lambda().notIn(SysSystemEntity::getId, moduleAuthorize);
        }
        queryWrapper.lambda().orderByAsc(SysSystemEntity::getSortCode).orderByDesc(SysSystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public SysSystemEntity getInfo(String id) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysSystemEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean isExistFullName(String id, String fullName) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysSystemEntity::getFullName, fullName);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SysSystemEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean isExistEnCode(String id, String enCode) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysSystemEntity::getEnCode, enCode);
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SysSystemEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public Boolean create(SysSystemEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setIsMain(0);
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        boolean save = this.save(entity);
        if (!userProvider.get().getIsAdministrator() && save) {
            // 当前用户创建的组织要赋予权限
            SysOrganizeAdministratorEntity organizeAdministratorEntity = new SysOrganizeAdministratorEntity();
            organizeAdministratorEntity.setUserId(userProvider.get().getUserId());
            organizeAdministratorEntity.setOrganizeId(entity.getId());
            organizeAdministratorEntity.setOrganizeType(PermissionConst.SYSTEM);
            organizeAdminTratorApi.save(organizeAdministratorEntity);
        }
        return save;
    }

    @Override
    @Transactional
    public Boolean update(String id, SysSystemEntity entity) {
        entity.setId(id);
        if (entity.getIsMain() == null) {
            entity.setIsMain(0);
        }
        entity.setUpdateUserId(userProvider.get().getUserId());
        entity.setUpdateTime(new Date());
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public Boolean delete(String id) {
        moduleService.deleteBySystemId(id);
        return this.removeById(id);
    }

    @Override
    public List<SysSystemEntity> getListByIds(List<String> list, List<String> moduleAuthorize) {
        List<SysSystemEntity> systemList = new ArrayList<>(16);
        if (list.size() > 0) {
            QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
            if (moduleAuthorize != null && moduleAuthorize.size() > 0) {
                queryWrapper.lambda().notIn(SysSystemEntity::getId, moduleAuthorize);
            }
            queryWrapper.lambda().in(SysSystemEntity::getId, list);
            queryWrapper.lambda().eq(SysSystemEntity::getEnabledMark, 1);
            queryWrapper.lambda().orderByAsc(SysSystemEntity::getSortCode).orderByDesc(SysSystemEntity::getCreatorTime);
            return this.list(queryWrapper);
        }
        return systemList;
    }

    @Override
    public SysSystemEntity getInfoByEnCode(String enCode) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysSystemEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SysSystemEntity> findSystemAdmin(int mark, String mainSystemCode, List<String> moduleAuthorize) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        if (mark == 1) {
            queryWrapper.lambda().eq(SysSystemEntity::getEnabledMark, mark)
                    .ne(SysSystemEntity::getEnCode, mainSystemCode);
        }
        if (moduleAuthorize != null && moduleAuthorize.size() > 0) {
            queryWrapper.lambda().notIn(SysSystemEntity::getId, moduleAuthorize);
        }
        queryWrapper.lambda().orderByAsc(SysSystemEntity::getSortCode).orderByDesc(SysSystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysSystemEntity> findSystemAdmin(int mark, String mainSystemCode) {
        QueryWrapper<SysSystemEntity> queryWrapper = new QueryWrapper<>();
        if (mark == 1) {
            queryWrapper.lambda().eq(SysSystemEntity::getEnabledMark, mark)
                    .ne(SysSystemEntity::getEnCode, mainSystemCode);
        }
        queryWrapper.lambda().orderByAsc(SysSystemEntity::getSortCode).orderByDesc(SysSystemEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

}
