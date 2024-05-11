package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.*;
import com.linzen.permission.mapper.PermissionGroupMapper;
import com.linzen.permission.model.permissiongroup.PaginationPermissionGroup;
import com.linzen.permission.service.*;
import com.linzen.util.RandomUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.type.AuthorizeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionGroupServiceImpl extends SuperServiceImpl<PermissionGroupMapper, SysPermissionGroupEntity> implements PermissionGroupService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private OrganizeService organizeService;

    @Override
    public List<SysPermissionGroupEntity> list(PaginationPermissionGroup pagination) {
        boolean flag = false;
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        String keyword = pagination.getKeyword();
        if (StringUtil.isNotEmpty(keyword)) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(SysPermissionGroupEntity::getFullName, keyword)
                            .or().like(SysPermissionGroupEntity::getEnCode, keyword)
                            .or().like(SysPermissionGroupEntity::getDescription, keyword)
            );
        }
        if (pagination.getEnabledMark() != null) {
            queryWrapper.lambda().eq(SysPermissionGroupEntity::getEnabledMark, pagination.getEnabledMark());
        }
        queryWrapper.lambda().orderByAsc(SysPermissionGroupEntity::getSortCode).orderByDesc(SysPermissionGroupEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(SysPermissionGroupEntity::getUpdateTime);
        }
        Page<SysPermissionGroupEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<SysPermissionGroupEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public List<SysPermissionGroupEntity> list(boolean filterdelFlag, List<String> ids) {
        if (ids != null && ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        if (filterdelFlag) {
            queryWrapper.lambda().eq(SysPermissionGroupEntity::getEnabledMark, 1);
        }
        if (ids != null && ids.size() > 0) {
            queryWrapper.lambda().in(SysPermissionGroupEntity::getId, ids);
        }
        return this.list(queryWrapper);
    }

    @Override
    public SysPermissionGroupEntity info(String id) {
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermissionGroupEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean create(SysPermissionGroupEntity entity) {
        entity.setId(RandomUtil.uuId());
        return this.save(entity);
    }

    @Override
    public boolean update(String id, SysPermissionGroupEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public boolean delete(SysPermissionGroupEntity entity) {
        return this.removeById(entity);
    }

    @Override
    public boolean isExistByFullName(String id, SysPermissionGroupEntity entity) {
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermissionGroupEntity::getFullName, entity.getFullName());
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SysPermissionGroupEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isExistByEnCode(String id, SysPermissionGroupEntity entity) {
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermissionGroupEntity::getEnCode, entity.getEnCode());
        if (StringUtil.isNotEmpty(id)) {
            queryWrapper.lambda().ne(SysPermissionGroupEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public SysPermissionGroupEntity permissionMember(String id) {
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermissionGroupEntity::getId, id);
        queryWrapper.lambda().select(SysPermissionGroupEntity::getId, SysPermissionGroupEntity::getPermissionMember);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SysPermissionGroupEntity> getPermissionGroupByUserId(String userId, String organizeId, boolean singletonOrg, String systemId) {
        List<SysPermissionGroupEntity> list = new ArrayList<>();
        // 用户本身有没有权限
        SysUserEntity userEntity = userService.getInfo(userId);
        if (userEntity == null) {
            return list;
        }
        List<SysPermissionGroupEntity> permissionGroupEntities = this.list(true, null).stream().filter(t -> StringUtil.isNotEmpty(t.getPermissionMember())).collect(Collectors.toList());
        String finalUserId = userId + "--" + PermissionConst.USER;
        List<SysPermissionGroupEntity> collect = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(finalUserId)).collect(Collectors.toList());
        collect.forEach(permissionGroupEntity -> {
            if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                list.add(permissionGroupEntity);
            }
        });
        // 用户关系表
        List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(userEntity.getId()).stream().filter(r -> StringUtil.isNotEmpty(r.getObjectId())).collect(Collectors.toList());
        // 分组有没有权限
        List<String> groupIds = new ArrayList<>();
        List<String> groupId = listByUserId.stream().filter(t -> PermissionConst.GROUP.equals(t.getObjectType()))
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> groupName = groupService.getListByIds(groupId, true)
                .stream().map(GroupEntity::getId).collect(Collectors.toList());
        groupName.forEach(t -> groupIds.add(t + "--group"));
        for (String id : groupIds) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id)).collect(Collectors.toList());
            collect1.forEach(permissionGroupEntity -> {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                    list.add(permissionGroupEntity);
                }
            });
        }
        // 全局角色如果有权限
        List<String> roleAllList = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> globalList = roleService.getGlobalList(roleAllList)
                .stream().map(SysRoleEntity::getId).collect(Collectors.toList());
        for (String id : globalList) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream()
                    .filter(entity -> entity.getPermissionMember().contains(id + "--role")).collect(Collectors.toList());
            collect1.forEach(permissionGroupEntity -> {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                    list.add(permissionGroupEntity);
                }
            });
        }

        // 组织有权限
        List<String> organizeIds = new ArrayList<>();
        List<String> orgIds = new ArrayList<>();
        if(list.size() > 0) {
            // 当前组织及组织下岗位、角色权限组
            organizeIds.add(userEntity.getOrganizeId());
        } else {
            // 此情况下不找
            if (StringUtil.isNotEmpty(organizeId)) {
                organizeIds.add(organizeId);
            } else {
                // 找到一个有权限的组织、岗位、角色
                List<SysUserRelationEntity> listByObjectType = userRelationService.getListByObjectType(userId, PermissionConst.ORGANIZE);
                List<String> collect2 = listByObjectType.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
                organizeIds.addAll(collect2);
                organizeIds.add(userEntity.getOrganizeId());
            }
        }
        // 拼上后缀
        if (organizeIds.size() > 0) {
            List<String> collect1 = new ArrayList<>();
            collect1.addAll(organizeService.getOrgEntityList(organizeIds, true)
                    .stream().map(SysOrganizeEntity::getId).collect(Collectors.toList()));
            orgIds.addAll(collect1);
            collect1.forEach(t -> {
                orgIds.add(t + "--" + PermissionConst.COMPANY);
                orgIds.add(t + "--" + PermissionConst.DEPARTMENT);
            });
        }
        List<SysOrganizeRelationEntity> relationListByOrganizeId = organizeRelationService.getRelationListByOrganizeId(organizeIds);
        List<SysPermissionGroupEntity> orgList = new ArrayList<>();
        List<SysPositionEntity> listByOrganizeId = positionService.getListByOrganizeId(orgIds, false);
        for (String oId : orgIds) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(oId)).collect(Collectors.toList());
            collect1.forEach(permissionGroupEntity -> {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                    orgList.add(permissionGroupEntity);
                }
            });

            // 判断该组织下的岗位是否有权限
            List<String> positionListByTypeAndOrgId = listByOrganizeId.stream().filter(t -> t.getOrganizeId().equals(oId))
                    .map(SysPositionEntity::getId).collect(Collectors.toList());
            List<String> positionId = listByUserId.stream().filter(t -> PermissionConst.POSITION.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> containsPosition = positionListByTypeAndOrgId.stream().filter(positionId::contains).collect(Collectors.toList());
            List<String> positionName = positionService.getPositionName(containsPosition, true)
                    .stream().map(SysPositionEntity::getId).collect(Collectors.toList());
            for (String id : positionName) {
                List<SysPermissionGroupEntity> collect2 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id + "--position")).collect(Collectors.toList());
                collect2.forEach(permissionGroupEntity -> {
                    if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                        orgList.add(permissionGroupEntity);
                    }
                });
            }
            // 判断该组织下的角色是否有权限
            List<String> roleListByTypeAndOrgId = relationListByOrganizeId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> roleId = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<SysRoleEntity> roleName = roleService.getListByIds(roleId, null, true)
                    .stream().filter(t -> t.getGlobalMark() != 1).collect(Collectors.toList());
            List<String> containsRole = roleName.stream().filter(t -> roleListByTypeAndOrgId.contains(t.getId())).collect(Collectors.toList())
                    .stream().map(SysRoleEntity::getId).collect(Collectors.toList());;
            for (String id : containsRole) {
                List<SysPermissionGroupEntity> collect2 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id + "--role")).collect(Collectors.toList());
                collect2.forEach(permissionGroupEntity -> {
                    if (authorizeService.existAuthorize(permissionGroupEntity.getId(), systemId)) {
                        orgList.add(permissionGroupEntity);
                    }
                });
            }
            if (orgList.size() > 0) {
                if (!singletonOrg) {
                    break;
                }
            }
        }
        list.addAll(orgList);
        return list;
    }

    @Override
    public String getPermissionGroupByUserId(String userId) {
        // 用户本身有没有权限
        SysUserEntity userEntity = userService.getInfo(userId);
        if (userEntity == null) {
            return "";
        }
        List<SysPermissionGroupEntity> permissionGroupEntities = this.list(true, null).stream().filter(t -> StringUtil.isNotEmpty(t.getPermissionMember())).collect(Collectors.toList());
        String finalUserId = userId + "--" + PermissionConst.USER;
        List<SysPermissionGroupEntity> collect = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(finalUserId)).collect(Collectors.toList());
        for (SysPermissionGroupEntity permissionGroupEntity : collect) {
            if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                return "";
            }
        }
        // 用户关系表
        List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(userEntity.getId());
        // 分组有没有权限
        List<String> groupIds = new ArrayList<>();
        List<String> groupId = listByUserId.stream().filter(t -> PermissionConst.GROUP.equals(t.getObjectType()))
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> groupName = groupService.getListByIds(groupId, true)
                .stream().map(GroupEntity::getId).collect(Collectors.toList());
        groupName.forEach(t -> groupIds.add(t + "--group"));
        for (String id : groupIds) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id)).collect(Collectors.toList());
            for (SysPermissionGroupEntity permissionGroupEntity : collect1) {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                    return "";
                }
            }
        }
        // 全局角色如果有权限
        List<String> roleAllList = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> globalList = roleService.getGlobalList(roleAllList)
                .stream().map(SysRoleEntity::getId).collect(Collectors.toList());
        for (String id : globalList) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream()
                    .filter(entity -> entity.getPermissionMember().contains(id + "--role")).collect(Collectors.toList());
            for (SysPermissionGroupEntity permissionGroupEntity : collect1) {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                    return "";
                }
            }
        }

        // 组织有权限
        List<String> orgIds = new ArrayList<>();
        List<SysUserRelationEntity> listByObjectType = userRelationService.getListByObjectType(userId, PermissionConst.ORGANIZE);
        List<String> collect2 = new ArrayList<>();
        collect2.add(userEntity.getOrganizeId());
        collect2.addAll(listByObjectType.stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList()));
        orgIds.addAll(collect2);
        collect2.forEach(t -> {
            orgIds.add(t + "--" + PermissionConst.COMPANY);
            orgIds.add(t + "--" + PermissionConst.DEPARTMENT);
        });
        List<SysOrganizeRelationEntity> relationListByOrganizeId = organizeRelationService.getRelationListByOrganizeId(collect2);
        List<SysPositionEntity> listByOrganizeId = positionService.getListByOrganizeId(orgIds, false);
        for (String orgId : orgIds) {
            List<SysPermissionGroupEntity> collect1 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(orgId)).collect(Collectors.toList());
            for (SysPermissionGroupEntity permissionGroupEntity : collect1) {
                if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                    return orgId.split("--")[0];
                }
            }
            // 判断该组织下的岗位是否有权限
            List<String> positionListByTypeAndOrgId = listByOrganizeId.stream().filter(t -> t.getOrganizeId().equals(orgId))
                    .map(SysPositionEntity::getId).collect(Collectors.toList());
            List<String> positionId = listByUserId.stream().filter(t -> PermissionConst.POSITION.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> containsPosition = positionListByTypeAndOrgId.stream().filter(positionId::contains).collect(Collectors.toList());
            List<String> positionName = positionService.getPositionName(containsPosition, true)
                    .stream().map(SysPositionEntity::getId).collect(Collectors.toList());
            for (String id : positionName) {
                List<SysPermissionGroupEntity> collect3 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id + "--position")).collect(Collectors.toList());
                for (SysPermissionGroupEntity permissionGroupEntity : collect3) {
                    if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                        return orgId.split("--")[0];
                    }
                }
            }
            // 判断该组织下的角色是否有权限
            List<String> roleListByTypeAndOrgId = relationListByOrganizeId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> roleId = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<SysRoleEntity> roleName = roleService.getListByIds(roleId, null, true)
                    .stream().filter(t -> t.getGlobalMark() != 1).collect(Collectors.toList());
            List<String> containsRole = roleName.stream().filter(t -> roleListByTypeAndOrgId.contains(t.getId())).collect(Collectors.toList())
                    .stream().map(SysRoleEntity::getId).collect(Collectors.toList());;
            for (String id : containsRole) {
                List<SysPermissionGroupEntity> collect3 = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id + "--role")).collect(Collectors.toList());
                for (SysPermissionGroupEntity permissionGroupEntity : collect3) {
                    if (authorizeService.existAuthorize(permissionGroupEntity.getId(), null)) {
                        return orgId.split("--")[0];
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getOrgIdByUserIdAndSystemId(String userId, String systemId) {
        // 用户本身有没有权限
        SysUserEntity userEntity = userService.getInfo(userId);
        if (userEntity == null) {
            return "";
        }
        // 判断有这个应用的权限组有哪些
        List<String> collect = authorizeService.getListByObjectAndItemIdAndType(systemId, PermissionConst.SYSTEM).stream().map(SysAuthorizeEntity::getObjectId).collect(Collectors.toList());
        List<SysPermissionGroupEntity> list = this.list(true, collect).stream().filter(t -> StringUtil.isNotEmpty(t.getPermissionMember())).collect(Collectors.toList());

        List<String> objectIds = new ArrayList<>();
        objectIds.add(userId + "--" + PermissionConst.USER);
        // 用户关系
        List<SysUserRelationEntity> listByUserId = userRelationService.getListByUserId(userEntity.getId())
                .stream().filter(t -> userId.equals(t.getUserId())).collect(Collectors.toList());
        // 分组
        List<String> groupId = listByUserId.stream().filter(t -> PermissionConst.GROUP.equals(t.getObjectType())).map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> groupName = groupService.getListByIds(groupId, true)
                .stream().map(GroupEntity::getId).collect(Collectors.toList());
        groupName.forEach(t -> {
            objectIds.add(t + "--group");
        });
        // 角色
        List<String> roleAllList = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType())).map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<String> globalList = roleService.getGlobalList(roleAllList)
                .stream().map(SysRoleEntity::getId).collect(Collectors.toList());
        globalList.forEach(t -> {
            objectIds.add(t + "--role");
        });
        for (String objectId : objectIds) {
            List<SysPermissionGroupEntity> collect1 = list.stream().filter(t -> t.getPermissionMember().contains(objectId)).collect(Collectors.toList());
            if (collect1.size() > 0) {
                return "";
            }
        }
        // 组织
        List<String> orgIds = listByUserId.stream().filter(t -> PermissionConst.ORGANIZE.equals(t.getObjectType())).map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        List<SysOrganizeEntity> orgEntityList = organizeService.getOrgEntityList(orgIds, true);
        List<SysPositionEntity> listByOrganizeId = positionService.getListByOrganizeId(orgIds, false);
        List<SysOrganizeRelationEntity> relationListByOrganizeId = organizeRelationService.getRelationListByOrganizeId(orgEntityList.stream().map(SysOrganizeEntity::getId).collect(Collectors.toList()));
        for (SysOrganizeEntity organizeEntity : orgEntityList) {
            List<SysPermissionGroupEntity> collect1 = list.stream().filter(entity -> entity.getPermissionMember().contains(organizeEntity.getId())).collect(Collectors.toList());
            if (collect1.size() > 0) {
                return organizeEntity.getId();
            }
            // 角色
            List<String> roleListByTypeAndOrgId = relationListByOrganizeId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> roleId = listByUserId.stream().filter(t -> PermissionConst.ROLE.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<SysRoleEntity> roleName = roleService.getListByIds(roleId, null, true)
                    .stream().filter(t -> t.getGlobalMark() != 1).collect(Collectors.toList());
            List<String> containsRole = roleName.stream().filter(t -> roleListByTypeAndOrgId.contains(t.getId())).collect(Collectors.toList())
                    .stream().map(SysRoleEntity::getId).collect(Collectors.toList());
            for (String containsId : containsRole) {
                if (list.stream().anyMatch(entity -> entity.getPermissionMember().contains(containsId))) {
                    return organizeEntity.getId();
                }
            }
            // 岗位
            List<String> positionListByTypeAndOrgId = listByOrganizeId.stream().filter(t -> t.getOrganizeId().equals(organizeEntity.getId()))
                    .map(SysPositionEntity::getId).collect(Collectors.toList());
            List<String> positionId = listByUserId.stream().filter(t -> PermissionConst.POSITION.equals(t.getObjectType()))
                    .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
            List<String> containsPosition = positionListByTypeAndOrgId.stream().filter(positionId::contains).collect(Collectors.toList());
            List<String> positionName = positionService.getPositionName(containsPosition, true)
                    .stream().map(SysPositionEntity::getId).collect(Collectors.toList());
            for (String containsId : positionName) {
                if (list.stream().anyMatch(entity -> entity.getPermissionMember().contains(containsId))) {
                    return organizeEntity.getId();
                }
            }
        }
        return "";
    }

    @Override
    public List<SysPermissionGroupEntity> getPermissionGroupAllByUserId(String userId) {
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(SysPermissionGroupEntity::getPermissionMember, userId);
        queryWrapper.lambda().eq(SysPermissionGroupEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
//        UserEntity userEntity = userService.getInfo(userId);
//        // 通过用户id获取相关的组织、部门、岗位、角色、分组
//        if (userEntity == null) {
//            return new ArrayList<>();
//        }
//        if (userEntity.getIsAdministrator() == 1) {
//            return this.list(true, null);
//        }
//        Set<String> objIds = new HashSet<>();
//
//        // 用户与组织关系
//        List<String> orgIds = new ArrayList<>();
//        List<String> orgId = userRelationService.getListByObjectType(userEntity.getId(), PermissionConst.ORGANIZE).stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
//        orgId.forEach(t -> orgIds.add(t + "--" + PermissionConst.ORGANIZE));
//        // 用户与岗位关系
//        List<String> positionIds = new ArrayList<>();
//        List<String> positionId = userRelationService.getListByObjectType(userEntity.getId(), PermissionConst.POSITION).stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
//        positionId.forEach(t -> positionIds.add(t + "--" + PermissionConst.POSITION));
//        // 用户与角色关系
//        List<String> roleIds = new ArrayList<>();
//        List<String> roleId = userRelationService.getListByObjectType(userEntity.getId(), PermissionConst.ROLE).stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
//        roleId.forEach(t -> roleIds.add(t + "--" + PermissionConst.ROLE));
//        // 用户与跟分组关系
//        List<String> groupIds = new ArrayList<>();
//        List<String> groupId = userRelationService.getListByObjectType(userEntity.getId(), PermissionConst.GROUP).stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
//        groupId.forEach(t -> groupIds.add(t + "--" + PermissionConst.GROUP));
//
//        objIds.addAll(orgIds);
//        objIds.addAll(positionIds);
//        objIds.addAll(roleIds);
//        objIds.addAll(groupIds);
//
//        Set<String> permissionGroupIds = new HashSet<>();
//
//        List<PermissionGroupEntity> permissionGroupEntities = this.list(true, null);
//        objIds.forEach(objId -> {
//            List<PermissionGroupEntity> collect = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(objId)).collect(Collectors.toList());
//            if (collect.size() > 0) {
//                permissionGroupIds.addAll(collect.stream().map(PermissionGroupEntity::getId).collect(Collectors.toList()));
//            }
//        });
//        return this.list(true, new ArrayList<>(permissionGroupIds));
    }

    @Override
    @Transactional
    public boolean updateByUser(String fromId, String toId, List<String> permissionList) {
        if (StringUtil.isEmpty(fromId)) {
            return false;
        }
        String fromIds = fromId + "--" + PermissionConst.USER;
        List<SysPermissionGroupEntity> permissionGroupAllByUserId = this.getPermissionGroupAllByUserId(fromId);
        permissionGroupAllByUserId.forEach(t -> {
            if (permissionList.contains(t.getId())) {
                t.setPermissionMember(StringUtil.isNotEmpty(t.getPermissionMember()) ? t.getPermissionMember().replaceAll(fromIds, toId + "--" + PermissionConst.USER) : "");
                this.updateById(t);
            }
        });
        return true;
    }

    @Override
    public List<SysPermissionGroupEntity> getPermissionGroupByModuleId(String moduleId) {
        // 获取到菜单和权限组的关系
        List<SysAuthorizeEntity> authorizeEntityList = authorizeService.getListByObjectAndItem(moduleId, AuthorizeType.MODULE);
        // 获取权限组信息
        List<SysPermissionGroupEntity> list = this.list(true,
                authorizeEntityList.stream().map(SysAuthorizeEntity::getObjectId).collect(Collectors.toList()));
        return list;
    }

    @Override
    public List<SysPermissionGroupEntity> list(List<String> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysPermissionGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SysPermissionGroupEntity::getId, ids);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysPermissionGroupEntity> getPermissionGroupByObjectId(String objectId, String objectType) {
        List<SysPermissionGroupEntity> permissionGroupEntities = this.list(true, null).stream().filter(t -> StringUtil.isNotEmpty(t.getPermissionMember())).collect(Collectors.toList());
        String id = objectId + "--" + objectType;
        List<SysPermissionGroupEntity> collect = permissionGroupEntities.stream().filter(entity -> entity.getPermissionMember().contains(id)).collect(Collectors.toList());
        return collect;
    }

}
