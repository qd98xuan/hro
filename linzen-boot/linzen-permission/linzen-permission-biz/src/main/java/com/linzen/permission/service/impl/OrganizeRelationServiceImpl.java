package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.*;
import com.linzen.permission.mapper.OrganizeRelationMapper;
import com.linzen.permission.model.organize.OrganizeConditionModel;
import com.linzen.permission.model.organize.OrganizeModel;
import com.linzen.permission.service.*;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织关系 服务实现类
 * </p>
 *
 * @author FHNP
 * @since 2022-01-19
 */
@Service
public class OrganizeRelationServiceImpl extends SuperServiceImpl<OrganizeRelationMapper, SysOrganizeRelationEntity> implements OrganizeRelationService {

    @Autowired
    RoleService roleService;
    @Autowired
    PositionService positionService;
    @Autowired
    UserRelationService userRelationService;
    @Autowired
    UserService userService;
    @Autowired
    AuthorizeService authorizeService;
    @Autowired
    OrganizeService organizeService;
    @Autowired
    OrganizeAdministratorService organizeAdministratorService;
    @Autowired
    PermissionGroupService permissionGroupService;

    @Override
    public List<SysOrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds) {
        if (organizeIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeIds);
        query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysOrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds, String objectType) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        // 查询组织关系表集合
        if(StringUtil.isNotEmpty(objectType)) {
            query.lambda().eq(SysOrganizeRelationEntity::getObjectType, objectType);
        }
        if(organizeIds.size() > 0){
            query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeIds);
        } else {
            organizeIds.add("");
            query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeIds);
        }
        query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<String> getPositionListByOrganizeId(List<String> organizeIds) {
        if(organizeIds.size() > 0){
            QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
            // 查询组织关系表集合
            query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.POSITION);
            query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeIds);
            query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
            return this.list(query).stream().map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<SysOrganizeRelationEntity> getRelationListByRoleId(String roleId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        // 查询组织关系表集合
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.ROLE);
        query.lambda().in(SysOrganizeRelationEntity::getObjectId, roleId);
        query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysOrganizeRelationEntity> getRelationListByRoleIdList(List<String> roleId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        // 查询组织关系表集合
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.ROLE);
        query.lambda().in(SysOrganizeRelationEntity::getObjectId, roleId);
        query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysOrganizeRelationEntity> getRelationListByObjectIdAndType(String objectType, String objectId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysOrganizeRelationEntity::getObjectId, objectId);
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, objectType);
        return this.list(query);
    }

    @Override
    public Boolean existByRoleIdAndOrgId(String roleId, String organizeId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.ROLE);
        query.lambda().in(SysOrganizeRelationEntity::getObjectId, roleId);
        query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeId);
        return count(query) > 0;
    }

    @Override
    public Boolean existByObjTypeAndOrgId(String objectType, String organizeId) {
        return existByObjAndOrgId(objectType, null, organizeId);
    }

    @Override
    public Boolean existByObjAndOrgId(String objectType, String objId, String organizeId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, objectType);
        if(StringUtil.isNotEmpty(objId)){
            query.lambda().eq(SysOrganizeRelationEntity::getObjectId, objId);
        }
        query.lambda().in(SysOrganizeRelationEntity::getOrganizeId, organizeId);
        return count(query) > 0;
    }


    @Override
    public List<SysOrganizeRelationEntity> getRelationListByType(String objectType) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        // 查询组织关系表集合
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, objectType);
        query.lambda().orderByDesc(SysOrganizeRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysOrganizeRelationEntity> getListByTypeAndOrgId(String objectType, String orgId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        // 查询组织关系表集合
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, objectType)
                .eq(SysOrganizeRelationEntity::getOrganizeId, orgId);
        return this.list(query);
    }

    @Override
    public Boolean deleteAllByRoleId(String roleId) {
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.ROLE);
        query.lambda().eq(SysOrganizeRelationEntity::getObjectId, roleId);
        return this.remove(query);
    }


    /*========================== 自动切换岗位，组织相关 ==============================*/


    @Override
    public String autoGetMajorPositionId(String userId, String currentMajorOrgId, String currentMajorPosId){
        // 属于该该组织底下的岗位
        List<SysPositionEntity> positionList = positionService.getListByOrgIdAndUserId(currentMajorOrgId, userId);
        if(positionList.size() > 0){
            // 默认岗位是否在此组织内，若存在不做切换
            if(positionList.stream().anyMatch(p -> p.getId().equals(currentMajorPosId))){
                return currentMajorPosId;
            }else{
                // 默认第一个岗位
                return positionList.get(0).getId();
            }
        }
        return "";
    }

    @Override
    public String autoGetMajorOrganizeId(String userId, List<String> userAllOrgIds, String currentMajorOrgId, String systemId){
        if(userAllOrgIds.size() > 0){
            if (userAllOrgIds.contains(currentMajorOrgId) && checkBasePermission(userId, currentMajorOrgId, systemId).size() > 0) {
                // 保持原默认组织不切换
                return currentMajorOrgId;
            }else{
                // 去除原本默认组织ID
                List<String> selectOrgIds = userAllOrgIds.stream().filter(usi-> !usi.equals(currentMajorOrgId)).collect(Collectors.toList());
                // 若不存在，强制切换有基本登录权限的角色
                for (String orgId : selectOrgIds) {
                    if (this.checkBasePermission(userId, orgId, systemId).size() > 0) {
                        // 这个组织ID底下角色存在基础登录权限
                        return orgId;
                    }
                }
            }
            // 随机赋值一个
            return userAllOrgIds.get(0);
        }else {
            return "";
        }
    }




    /*== 自动key.equals ==*/

    @Override
    public void autoSetOrganize(List<String> allUpdateUserIds){
        if(allUpdateUserIds.size() > 0){
            for (SysUserEntity userEntity : userService.listByIds(allUpdateUserIds)) {
                String useId = userEntity.getId();
                String majorOrgId = userEntity.getOrganizeId();
                List<String> orgList = userRelationService.getListByObjectType(useId, PermissionConst.ORGANIZE)
                        .stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
                String changeOrgId = this.autoGetMajorOrganizeId(useId, orgList, majorOrgId, null);
                if(!changeOrgId.equals(majorOrgId)){
                    // 切换默认组织
                    SysUserEntity updateUserEntity = new SysUserEntity();
                    updateUserEntity.setId(useId);
                    updateUserEntity.setOrganizeId(changeOrgId);
                    userService.updateById(updateUserEntity);
                }
            }
        }
    }

    @Override
    public void autoSetPosition(List<String> allUpdateUserIds){
        if(allUpdateUserIds.size() > 0){
            for (SysUserEntity user : userService.listByIds(allUpdateUserIds)) {
                String majorPosId = user.getPositionId();
                String changePositionId = this.autoGetMajorPositionId(user.getId(), user.getOrganizeId(), majorPosId);
                if(!changePositionId.equals(majorPosId)){
                    SysUserEntity updateUser = new SysUserEntity();
                    updateUser.setId(user.getId());
                    updateUser.setPositionId(changePositionId);
                    userService.updateById(updateUser);
                }
            }
        }
    }


    /*===================== 权限判断 =======================*/

    @Override
    public List<SysPermissionGroupEntity> checkBasePermission(String userId, String orgId, String systemId){
        List<SysPermissionGroupEntity> permissionGroupByUserId = permissionGroupService.getPermissionGroupByUserId(userId, orgId, false, systemId);
        return permissionGroupByUserId;
    }

    @Override
    public List<String> getOrgIds(List<String> departIds, String type) {
        List<String> idList = new ArrayList<>(16);
        // 获取所有组织
        if (departIds.size() > 0) {
            List<String> collect = departIds.stream().filter(LinzenConst.SYSTEM_PARAM.keySet()::contains).collect(Collectors.toList());
            String organizeId = UserProvider.getUser().getOrganizeId();
            collect.forEach(t -> {
                if (LinzenConst.CURRENT_ORG.equals(t) || LinzenConst.CURRENT_ORG_TYPE.equals(t)) {
                    idList.add(organizeId + "--" + PermissionConst.COMPANY);
                    idList.add(organizeId);
                } else if (LinzenConst.CURRENT_ORG_SUB.equals(t) || LinzenConst.CURRENT_ORG_SUB_TYPE.equals(t)) {
                    List<String> underOrganizations = organizeService.getUnderOrganizations(organizeId, true);
                    underOrganizations.add(organizeId);
                    underOrganizations.forEach(orgId -> {
                        idList.add(orgId + "--" + PermissionConst.COMPANY);
                        idList.add(orgId);
                    });
                } else if (LinzenConst.CURRENT_GRADE.equals(t) || LinzenConst.CURRENT_GRADE_TYPE.equals(t)) {
                    List<String> organizeUserList = organizeAdministratorService.getOrganizeUserList(LinzenConst.CURRENT_ORG_SUB);
                    organizeUserList.forEach(orgId -> {
                        idList.add(orgId + "--" + PermissionConst.COMPANY);
                        idList.add(orgId);
                    });
                }
            });
            departIds.removeAll(collect);
            idList.addAll(departIds);
            for (String departId : departIds) {
                String[] split = departId.split("--");
                if (split.length == 1 || split.length == 0) {
                    continue;
                }
                if (split.length > 1) {
                    if (PermissionConst.ORGANIZE.equals(split[1])) {
                        departId= split[0];
                    }
                    if (PermissionConst.DEPARTMENT.equals(split[1])) {
                        departId = split[0];
                    }
                }
                if (!PermissionConst.ROLE.equals(type)) {
                    List<String> underOrganizations = organizeService.getUnderOrganizations(departId, true);
                    if (underOrganizations.size() > 0) {
                        idList.addAll(underOrganizations);
                        idList.add(organizeId);
                    }
                }
            }
        }
        return idList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<OrganizeModel> getOrgIdsList(OrganizeConditionModel organizeConditionModel) {
        List<String> ids = new ArrayList<>();
        List<String> orgIds = getOrgIds(organizeConditionModel.getDepartIds(), null);
        Map<String, String> orgIdNameMaps = Optional.ofNullable(organizeConditionModel.getOrgIdNameMaps()).orElse(organizeService.getInfoList());
        orgIds.forEach(t -> ids.add(t.split("--")[0]));
        List<SysOrganizeEntity> listAll = organizeService.getListAll(ids, organizeConditionModel.getKeyword());
        List<OrganizeModel> organizeList = JsonUtil.createJsonToList(listAll, OrganizeModel.class);
        organizeList.forEach(t->{
            t.setIcon("department".equals(t.getType()) ? "icon-linzen icon-linzen-tree-department1" : "icon-linzen icon-linzen-tree-organization3");
            t.setLastFullName(t.getFullName());
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                t.setOrganizeIds(Arrays.asList(t.getOrganizeIdTree().split(",")));
                t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
                String[] split = t.getOrganizeIdTree().split(",");
                List<String> list = Arrays.asList(split);
                Collections.reverse(list);
                for (int i = 1; i < list.size(); i++) {
                    String orgId = list.get(i);
                    List<OrganizeModel> collect1 = organizeList.stream().filter(tt -> orgId.equals(tt.getId())).collect(Collectors.toList());
                    if (collect1.size() > 0) {
                        String[] split1 = StringUtil.isNotEmpty(t.getOrganizeIdTree()) ? t.getOrganizeIdTree().split(orgId) : new String[0];
                        if (split1.length > 0) {
                            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                        }
                        t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
                        t.setParentId(orgId);
                        break;
                    }
                }
            }
        });
        return organizeList;
    }

}
