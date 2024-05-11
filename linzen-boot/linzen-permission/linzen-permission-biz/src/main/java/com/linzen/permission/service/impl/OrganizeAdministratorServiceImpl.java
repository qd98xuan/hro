package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.service.DbLinkService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.base.service.SystemService;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.database.model.entity.DbLinkEntity;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.model.tenant.TenantAuthorizeModel;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.mapper.OrganizeAdminIsTratorMapper;
import com.linzen.permission.model.organizeadministrator.OrganizeAdministratorListVo;
import com.linzen.permission.model.organizeadministrator.OrganizeAdministratorModel;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.UserRelationService;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构分级管理员
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class OrganizeAdministratorServiceImpl extends SuperServiceImpl<OrganizeAdminIsTratorMapper, SysOrganizeAdministratorEntity> implements OrganizeAdministratorService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private DbLinkService dbLinkService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ConfigValueUtil configValueUtil;


    @Override
    public SysOrganizeAdministratorEntity getOne(String userId, String organizeId) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", userId);
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, userId);
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, userId);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getOrganizeId, organizeId);
        // 排序
        queryWrapper.lambda().orderByAsc(SysOrganizeAdministratorEntity::getSortCode)
                .orderByDesc(SysOrganizeAdministratorEntity::getCreatorTime);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SysOrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId) {
        return getOrganizeAdministratorEntity(userId, PermissionConst.ORGANIZE, false);
    }

    @Override
    public List<SysOrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId, String type, boolean filterMain) {
        List<SysOrganizeAdministratorEntity> list = new ArrayList<>();
        SysUserEntity entity1 = userService.getInfo(userId);
        if ((entity1 != null && entity1.getIsAdministrator() == 1) && !PermissionConst.ORGANIZE.equals(type)) {
            List<String> collect = new ArrayList<>();
            List<String> moduleAuthorize = new ArrayList<>();
            List<String> moduleUrlAddressAuthorize = new ArrayList<>();
            if (configValueUtil.isMultiTenancy()) {
                TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
                moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
                moduleUrlAddressAuthorize = tenantAuthorizeModel.getUrlAddressList();
            }
            if (PermissionConst.SYSTEM.equals(type)) {
                collect = systemService.getList(null, false, false, filterMain, false, moduleAuthorize).stream().map(SysSystemEntity::getId).collect(Collectors.toList());
            } else if (PermissionConst.MODULE.equals(type)) {
                collect = moduleService.getList(false, moduleAuthorize, moduleUrlAddressAuthorize).stream().map(ModuleEntity::getId).collect(Collectors.toList());
            }
            for (String t : collect) {
                SysOrganizeAdministratorEntity entity = new SysOrganizeAdministratorEntity();
                entity.setOrganizeId(t);
                entity.setId(RandomUtil.uuId());
                entity.setOrganizeType(type);
                entity.setUserId(userId);
                list.add(entity);
            }
            return list;
        }
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        if (PermissionConst.ORGANIZE.equals(type)) {
            queryWrapper.lambda().isNull(SysOrganizeAdministratorEntity::getOrganizeType);
        } else if (StringUtil.isNotEmpty(type)) {
            queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getOrganizeType, type);
        }

        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", userId);
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, userId);
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, userId);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        // 排序
        queryWrapper.lambda().orderByAsc(SysOrganizeAdministratorEntity::getSortCode)
                .orderByDesc(SysOrganizeAdministratorEntity::getCreatorTime);
        list = this.list(queryWrapper);
        return list;
    }

    @Override
    @Transactional
    public void create(SysOrganizeAdministratorEntity entity) {
        // 判断是新建还是删除
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getOrganizeId, entity.getOrganizeId());
        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", entity.getUserId());
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, entity.getUserId());
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, entity.getUserId());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        // 查出数据是否重复
        SysOrganizeAdministratorEntity administratorEntity = this.getOne(queryWrapper);
        if (administratorEntity == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setCreatorTime(new Date());
        } else {
            entity.setId(administratorEntity.getId());
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setUpdateTime(new Date());
        }
        this.saveOrUpdate(entity);
    }

    @Override
    @Transactional
    public void createList(List<SysOrganizeAdministratorEntity> list, String userId) {
        Date creatorTime = new Date();
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", userId);
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, userId);
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, userId);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        queryWrapper.lambda().select(SysOrganizeAdministratorEntity::getCreatorTime);
        List<SysOrganizeAdministratorEntity> list1 = this.list(queryWrapper);
        if (list1.size() > 0) {
            Date creatorTime1 = list1.get(0).getCreatorTime();
            if (creatorTime1 != null) {
                creatorTime = creatorTime1;
            }
        }
        // 手动设置userId
        Date finalCreatorTime = creatorTime;
        list.forEach(t -> {
            t.setUserId(userId);
            t.setCreatorTime(finalCreatorTime);
        });
        this.remove(queryWrapper);
        for (SysOrganizeAdministratorEntity entity : list) {
//            // 查出数据是否重复
//            OrganizeAdministratorEntity administratorEntity = this.getOne(queryWrapper);
//            if (administratorEntity == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(UserProvider.getLoginUserId());
            entity.setCreatorTime(finalCreatorTime);
//            } else {
//                entity.setId(administratorEntity.getId());
//                entity.setCreatorUserId(UserProvider.getLoginUserId());
//                entity.setUpdateTime(new Date());
//            }
            this.saveOrUpdate(entity);
        }
        userService.delCurUser(null, userId);
    }

    @Override
    public boolean update(String organizeId, SysOrganizeAdministratorEntity entity) {
        entity.setId(entity.getId());
        entity.setUpdateTime(DateUtil.getNowDate());
        entity.setUpdateUserId(UserProvider.getLoginUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean deleteByUserId(String userId) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", userId);
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, userId);
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, userId);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        boolean remove = this.remove(queryWrapper);
        userService.delCurUser(null, userId);
        return remove;
    }

    @Override
    public List<SysOrganizeAdministratorEntity> getInfoByUserId(String userId) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        try {
            DbLinkEntity dbLinkEntity = dbLinkService.getResource("0");
            if (DbTypeUtil.checkOracle(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity)) {
                queryWrapper.eq("dbms_lob.substr(F_USER_ID)", userId);
            } else if (DbTypeUtil.checkSQLServer(dbLinkEntity)) {
                queryWrapper.lambda().like(SysOrganizeAdministratorEntity::getUserId, userId);
            } else {
                queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getUserId, userId);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }

    @Override
    public SysOrganizeAdministratorEntity getInfo(String id) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(SysOrganizeAdministratorEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public SysOrganizeAdministratorEntity getInfoByOrganizeId(String organizeId) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganizeAdministratorEntity::getOrganizeId, organizeId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SysOrganizeAdministratorEntity> getListByOrganizeId(List<String> organizeIdList) {
        QueryWrapper<SysOrganizeAdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SysOrganizeAdministratorEntity::getOrganizeId, organizeIdList);
        return this.list(queryWrapper);
    }

    @Override
    public List<OrganizeAdministratorListVo> getList(Pagination pagination) {
        List<SysOrganizeAdministratorEntity> list = getOrganizeAdministratorEntity(UserProvider.getLoginUserId(), PermissionConst.ORGANIZE, false);
        List<String> organizeIdList = new ArrayList<>(16);
        // 存放所有的有资格管理的组织id
        if (userProvider.get().getIsAdministrator()) {
            organizeIdList = organizeService.getListById(true).stream().map(SysOrganizeEntity::getId).collect(Collectors.toList());
        } else {
            Set<String> orgId = new HashSet<>(16);
            // 判断自己是哪些组织的管理员
            list.stream().forEach(t-> {
                if (t != null) {
                    // t.getThisLayerAdd() == 1 || t.getThisLayerEdit() == 1 || t.getThisLayerDelete() == 1 || (StringUtil.isNotEmpty(String.valueOf(t.getSubLayerSelect())) && t.getThisLayerSelect() == 1)
                    if (t.getSubLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgId.add(t.getOrganizeId());
                    }
                    // t.getSubLayerAdd() == 1 || t.getSubLayerEdit() == 1 || t.getSubLayerDelete() == 1 || (StringUtil.isNotEmpty(String.valueOf(t.getSubLayerSelect())) && t.getSubLayerSelect() == 1)
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), true);
                        orgId.addAll(underOrganizations);
                    }
                }
            });
            organizeIdList = new ArrayList<>(orgId);
        }
        if (organizeIdList.size() < 1) {
            organizeIdList.add("");
        }
        List<SysOrganizeAdministratorEntity> list1 = getListByOrganizeId(organizeIdList);
        List<String> userIdList = list1.stream().map(SysOrganizeAdministratorEntity::getUserId).distinct().collect(Collectors.toList());
        List<String> finalOrganizeIdList = organizeIdList;
        List<String> userLists = new ArrayList<>();
        List<String> finalUserLists = userLists;
        userIdList.forEach(t -> {
            List<String> collect = userRelationService.getListByUserId(t).stream().filter(ur -> PermissionConst.ORGANIZE.equals(ur.getObjectType())).map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> collect1 = finalOrganizeIdList.stream().filter(collect::contains).collect(Collectors.toList());
            if (collect1.size() > 0) {
                finalUserLists.add(t);
            }
        });
        userLists = userLists.stream().distinct().collect(Collectors.toList());
        // 验证这些人是否有权限
        if (list.stream().anyMatch(t -> PermissionConst.SYSTEM.equals(t.getOrganizeType()))
                || list.stream().anyMatch(t -> PermissionConst.SYSTEM.equals(t.getOrganizeType()))) {

        }
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        List<SysUserEntity> userList = userService.getUserNames(userLists, pagination, true, false);
        userList.forEach(t -> {
            // 创建时间
            Date date = getOrganizeAdministratorEntity(t.getId()).stream().sorted(Comparator.comparing(SysOrganizeAdministratorEntity::getCreatorTime)).map(SysOrganizeAdministratorEntity::getCreatorTime).findFirst().orElse(null);
            t.setCreatorTime(date);
            // 所属组织
            List<SysUserRelationEntity> orgRelationByUserId = userRelationService.getAllOrgRelationByUserId(t.getId());
            StringBuilder orgName = new StringBuilder();
            orgRelationByUserId.stream().forEach(or -> {
                SysOrganizeEntity organizeEntity = organizeService.getInfo(or.getObjectId());
                if (organizeEntity != null && StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree())) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/");
                    orgName.append("," + fullNameByOrgIdTree);
                }
            });
            // 组织名称
            String org = orgName.length() > 0 ? orgName.toString().replaceFirst(",", "") : "";
            t.setOrganizeId(org);
        });
        // 处理所属组织和创建时间
        List<OrganizeAdministratorListVo> jsonToList = JsonUtil.createJsonToList(userList, OrganizeAdministratorListVo.class);
        jsonToList = jsonToList.stream().filter(t -> t != null && t.getCreatorTime() != null).sorted(Comparator.comparing(OrganizeAdministratorListVo::getCreatorTime).reversed()).collect(Collectors.toList());
        return jsonToList;
    }

    @Override
    public List<String> getOrganizeUserList(String type) {
        if (UserProvider.getUser().getIsAdministrator()) {
            return organizeService.getList(true).stream().map(SysOrganizeEntity::getId).collect(Collectors.toList());
        }
        List<SysOrganizeAdministratorEntity> list = getOrganizeAdministratorEntity(UserProvider.getLoginUserId());
        Set<String> orgId = new HashSet<>(16);
        // 判断自己是哪些组织的管理员
        list.stream().forEach(t -> {
            if (t != null) {
                if (t.getSubLayerSelect() != null && t.getThisLayerSelect() == 1) {
                    orgId.add(t.getOrganizeId());
                }
                if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                    List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), true);
                    orgId.addAll(underOrganizations);
                }
            }
        });
        List<String> orgIds = new ArrayList<>(orgId);
        if (LinzenConst.CURRENT_ORG_SUB.equals(type)) {
            return orgIds;
        }
        List<String> userList = userRelationService.getListByObjectIdAll(orgIds).stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
        return userList;
    }

    @Override
    public List<SysOrganizeEntity> getListByAuthorize() {
        // 通过权限转树
        List<SysOrganizeAdministratorEntity> listss = getOrganizeAdministratorEntity(UserProvider.getLoginUserId());
        Set<String> orgIds = new HashSet<>(16);
        // 判断自己是哪些组织的管理员
        listss.stream().forEach(t-> {
            if (t != null) {
                if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                    orgIds.add(t.getOrganizeId());
                }
                if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                    List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), true);
                    orgIds.addAll(underOrganizations);
                }
            }
        });
        List<String> list1 = new ArrayList<>(orgIds);
        // 得到所有有权限的组织
        List<SysOrganizeEntity> organizeName = organizeService.getOrganizeName(list1);
        return organizeName;
    }

    @Override
    public OrganizeAdministratorModel getOrganizeAdministratorList() {
        // 通过权限转树
        List<SysOrganizeAdministratorEntity> list = getOrganizeAdministratorEntity(userProvider.get().getUserId());
        List<String> addList = new ArrayList<>();
        List<String> editList = new ArrayList<>();
        List<String> deleteList = new ArrayList<>();
        List<String> selectList = new ArrayList<>();
        // 判断自己是哪些组织的管理员
        list.forEach(t -> {
            if (t != null) {
                //查询
                if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                    selectList.add(t.getOrganizeId());
                    //修改
                    if (t.getThisLayerEdit() != null && t.getThisLayerEdit() == 1) {
                        editList.add(t.getOrganizeId());
                    }
                    //删除
                    if (t.getThisLayerDelete() != null && t.getThisLayerDelete() == 1) {
                        deleteList.add(t.getOrganizeId());
                    }
                    //新增
                    if (t.getThisLayerAdd() != null && t.getThisLayerAdd() == 1) {
                        addList.add(t.getOrganizeId());
                    }
                }
                //查询
                if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                    List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                    selectList.addAll(underOrganizations);
                    //修改
                    if (t.getSubLayerEdit() != null && t.getSubLayerEdit() == 1) {
                        editList.addAll(underOrganizations);
                    }
                    //删除
                    if (t.getSubLayerDelete() != null && t.getSubLayerDelete() == 1) {
                        deleteList.addAll(underOrganizations);
                    }
                    //新增
                    if (t.getSubLayerAdd() != null && t.getSubLayerAdd() == 1) {
                        addList.addAll(underOrganizations);
                    }
                }
            }
        });
        OrganizeAdministratorModel model = new OrganizeAdministratorModel(addList,editList,deleteList,selectList);
        return model;
    }

}
