package com.linzen.permission.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.PermissionConst;
import com.linzen.message.entity.SynThirdInfoEntity;
import com.linzen.message.service.SynThirdInfoService;
import com.linzen.message.util.SynThirdConsts;
import com.linzen.permission.entity.PermissionBaseEntity;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.mapper.UserRelationMapper;
import com.linzen.permission.model.permission.PermissionModel;
import com.linzen.permission.model.userrelation.UserRelationForm;
import com.linzen.permission.service.*;
import com.linzen.permission.util.PermissionUtil;
import com.linzen.util.RandomUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.linzen.util.Constants.ADMIN_KEY;

/**
 * 用户关系
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class UserRelationServiceImpl extends SuperServiceImpl<UserRelationMapper, SysUserRelationEntity> implements UserRelationService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private SynThirdInfoService synThirdInfoService;

    @Override
    public List<SysUserRelationEntity> getListByUserId(String userId) {
        return getListByUserIdAll(Collections.singletonList(userId));
    }

    @Override
    public List<SysUserRelationEntity> getListByUserIdAndObjType(String userId, String objectType) {
        QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
        query.lambda().in(SysUserRelationEntity::getUserId, userId);
        query.lambda().in(SysUserRelationEntity::getObjectType, objectType);
        query.lambda().orderByAsc(SysUserRelationEntity::getSortCode).orderByDesc(SysUserRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysUserRelationEntity> getListByUserIdAll(List<String> userId) {
        if (userId.size() > 0) {
            QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysUserRelationEntity::getUserId, userId);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SysUserRelationEntity> getListByObjectId(String objectId) {
        return getListByObjectId(objectId, null);
    }

    @Override
    public List<SysUserRelationEntity> getListByObjectType(String objectType) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getObjectType, objectType);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysUserRelationEntity> getListByObjectId(String objectId, String objectType) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getObjectId, objectId);
        if(objectType != null){
            queryWrapper.lambda().eq(SysUserRelationEntity::getObjectType, objectType);
        }
        queryWrapper.lambda().orderByAsc(SysUserRelationEntity::getSortCode).orderByDesc(SysUserRelationEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysUserRelationEntity> getListByObjectIdAll(List<String> objectId) {
        List<SysUserRelationEntity> list = new ArrayList<>();
        if (objectId.size() > 0) {
            QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysUserRelationEntity::getObjectId, objectId);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public void deleteAllByObjId(String objId) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getObjectId, objId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteAllByUserId(String userId) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getUserId, userId);
        queryWrapper.lambda().ne(SysUserRelationEntity::getObjectType, PermissionConst.GROUP);
        userRelationService.remove(queryWrapper);
    }

    @Override
    public void createByList(List<SysUserRelationEntity> userRelationEntityList) {
        userRelationEntityList.forEach(t -> {
            this.save(t);
        });
    }

    @Override
    public SysUserRelationEntity getInfo(String id) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String objectId, List<SysUserRelationEntity> entitys) {
        List<SysUserRelationEntity> existList = this.getListByObjectId(objectId);
        List<SysUserRelationEntity> relationList = new ArrayList<>();
        for (int i = 0; i < entitys.size(); i++) {
            SysUserRelationEntity entity = entitys.get(i);
            entity.setId(RandomUtil.uuId());
            entity.setSortCode(Long.parseLong(i + ""));
            entity.setCreatorUserId(userProvider.get().getUserId());
            if (existList.stream().filter(t -> t.getUserId().equals(entity.getUserId())).count() == 0) {
                relationList.add(entity);
            }
        }
        for (SysUserRelationEntity entity : relationList) {
            this.save(entity);
        }
    }

    @Override
    public void save(List<SysUserRelationEntity> list) {
        for (SysUserRelationEntity entity : list) {
            this.save(entity);
        }
    }

    @Override
    @DSTransactional
    public void delete(String[] ids) {
        for (String item : ids) {
            QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUserRelationEntity::getId, item);
            this.remove(queryWrapper);
        }
    }


    @Override
    @DSTransactional
    public void saveObjectId(String objectId, UserRelationForm userRelationForm) {
        // 修改前的岗位绑定人员ID
        List<String> beforeUserIds = userRelationService.getListByObjectId(objectId)
                .stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
        //清除原有成员数据
        deleteAllByObjId(objectId);
        UserInfo userInfo = userProvider.get();
        List<SysUserRelationEntity> list = new ArrayList<>();
        int i = 0;
        for (String userId : userRelationForm.getUserIds()) {
            SysUserRelationEntity entity = new SysUserRelationEntity();
            entity.setId(RandomUtil.uuId());
            entity.setSortCode(Long.parseLong(i + ""));
            entity.setObjectId(objectId);
            entity.setObjectType(userRelationForm.getObjectType());
            entity.setCreatorUserId(userInfo.getUserId());
            entity.setUserId(userId);
            list.add(entity);
            i++;
        }
        save(objectId, list);

        // 并集：所有未修改的人员
        List<String> unUpdateUserId = beforeUserIds.stream().filter(b-> userRelationForm.getUserIds()
                .contains(b)).collect(Collectors.toList());
        // 差集：所有修改过的人员(包括：删除此岗位、添加此岗位的人员)
        beforeUserIds.addAll(userRelationForm.getUserIds());
        List<String> allUpdateIds = beforeUserIds.stream().filter(u-> !unUpdateUserId.contains(u))
                .collect(Collectors.toList());

        if (PermissionConst.POSITION.equals(userRelationForm.getObjectType())) {
            // 自动切换岗位
            organizeRelationService.autoSetPosition(allUpdateIds);
        }
    }

    @Override
    public void roleSaveByUserIds(String roleId, List<String> userIds) {
        //清除原有成员数据
        deleteAllByObjId(roleId);
        String currentUserId = userProvider.get().getUserId();
        List<SysUserRelationEntity> userRelationList = new ArrayList<>();
        for (String userId : userIds) {
            SysUserRelationEntity entity = new SysUserRelationEntity();
            entity.setId(RandomUtil.uuId());
            entity.setObjectId(roleId);
            entity.setObjectType(PermissionConst.ROLE);
            entity.setCreatorUserId(currentUserId);
            entity.setUserId(userId);
            userRelationList.add(entity);
        }
        this.saveBatch(userRelationList);
    }

    @Override
    public List<SysUserRelationEntity> getRelationByUserIds(List<String> userIds) {
        if (userIds.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
        query.lambda().in(SysUserRelationEntity::getUserId, userIds);
        query.lambda().eq(SysUserRelationEntity::getObjectType, PermissionConst.ORGANIZE);
        return this.list(query);
    }

    @Override
    public List<SysUserRelationEntity> getListByObjectType(String userId, String objectType) {
        QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRelationEntity::getUserId, userId).eq(SysUserRelationEntity::getObjectType, objectType);
        query.lambda().orderByAsc(SysUserRelationEntity::getSortCode).orderByDesc(SysUserRelationEntity::getCreatorTime);
        return this.list(query);
    }

    @Override
    public List<SysUserRelationEntity> getAllOrgRelationByUserId(String userId){
        return this.getListByObjectType(userId,PermissionConst.ORGANIZE);
    }

    @Override
    public List<PermissionModel> getObjectVoList(String objectType) {
        String userId = userProvider.get().getUserId();
        SysUserEntity userEntity = userService.getInfo(userId);
        String majorOrgId = userProvider.get().getOrganizeId();

        // 组装对应组织/岗位/角色对象
        switch (objectType) {
            case PermissionConst.ORGANIZE:
                // 使用in查询减少数据库查询次数
                List<String> ids = new ArrayList<>();
                this.getListByObjectType(userId, objectType).forEach(r -> ids.add(r.getObjectId()));
                List<PermissionModel> permissionModels = setModel(organizeService.getOrgEntityList(ids, false), majorOrgId);
                permissionModels.forEach(p->p.setFullName(PermissionUtil.getLinkInfoByOrgId(p.getId(), organizeService, false)));
                return permissionModels;
            case PermissionConst.POSITION:
                // 岗位遵循一对多关系
                List<SysPositionEntity> positionList = positionService.getListByUserId(userId);
                if (positionList.size() > 0) {
                    return setModel(positionList.stream().filter(p -> p.getOrganizeId().equals(majorOrgId))
                            .collect(Collectors.toList()), userEntity.getPositionId());
                }
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 设置返回模型
     *
     * @param permissionList
     * @param majorId
     */
    private <T extends PermissionBaseEntity> List<PermissionModel> setModel (List<T> permissionList, String majorId){
        List<PermissionModel> voList = new ArrayList<>();
        permissionList.forEach(p -> {
            PermissionModel model = new PermissionModel();
            if (p.getId().equals(majorId)) {
                model.setIsDefault(true);
            } else {
                model.setIsDefault(false);
            }
            model.setFullName(p.getFullName());
            model.setId(p.getId());
            model.setFullName(p.getFullName());
            voList.add(model);
        });
        return voList;
    }

    @Override
    public Boolean existByObj(String objectType, String objectId) {
        QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
        query.lambda()
                .eq(SysUserRelationEntity::getObjectType, objectType)
                .eq(SysUserRelationEntity::getObjectId, objectId);
        return this.count(query) > 0;
    }

    @Override
    public List<SysUserRelationEntity> getListByRoleId(String roleId) {
        List<SysUserRelationEntity> list = new ArrayList<>();
        organizeRelationService.getRelationListByRoleId(roleId).forEach(o->{
            QueryWrapper<SysUserRelationEntity> query = new QueryWrapper<>();
            query.lambda()
                    .eq(SysUserRelationEntity::getObjectType, PermissionConst.ORGANIZE)
                    .eq(SysUserRelationEntity::getObjectId, o.getOrganizeId());
            list.addAll(this.list(query));
        });
        return list;
    }

    @Override
    public List<SysUserRelationEntity> getListByUserId(String userId, String objectType) {
        QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRelationEntity::getUserId, userId);
        queryWrapper.lambda().eq(SysUserRelationEntity::getObjectType, objectType);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysUserRelationEntity> getListByOrgId(List<String> orgIdList) {
        if (orgIdList.size() > 0) {
            QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUserRelationEntity::getObjectType, PermissionConst.ORGANIZE).in(SysUserRelationEntity::getObjectId, orgIdList);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public void syncDingUserRelation(String sysObjId, List<Long> deptIdList) {
        List<SysUserRelationEntity> list = new ArrayList<>();
        SysUserRelationEntity entity = null;
        // 查询对应的中间表，获取到对应的本地组织id
        QueryWrapper<SynThirdInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(SynThirdInfoEntity::getThirdObjId,deptIdList);
        wrapper.lambda().eq(SynThirdInfoEntity::getThirdType, SynThirdConsts.THIRD_TYPE_DING_To_Sys);
        List<SynThirdInfoEntity> synThirdInfoLists = synThirdInfoService.getBaseMapper().selectList(wrapper);
        HashMap<String,String> map = new HashMap<>();
        for (SynThirdInfoEntity obj : synThirdInfoLists) {
            map.put(obj.getThirdObjId(),obj.getSysObjId());
        }
        // 已经存在的数据
        List<String> collect = deptIdList.stream().map(t -> String.valueOf(t)).collect(Collectors.toList());
        QueryWrapper<SysUserRelationEntity> relationWrapper = new QueryWrapper<>();
        relationWrapper.lambda().in(SysUserRelationEntity::getObjectId,collect);
        relationWrapper.lambda().eq(SysUserRelationEntity::getObjectType,"Organize");
        relationWrapper.lambda().eq(SysUserRelationEntity::getUserId,sysObjId);
        List<SysUserRelationEntity> userRelationEntities = this.getBaseMapper().selectList(relationWrapper);
        List<String> deleteIdS = userRelationEntities.stream().map(t -> t.getId()).collect(Collectors.toList());
        this.removeByIds(deleteIdS);

        SysUserEntity adminAccount = userService.getUserByAccount(ADMIN_KEY);
        String adminId = adminAccount == null ? null : adminAccount.getId();
        for (Long id : deptIdList) {
            String objectId = String.valueOf(id);
            entity= new SysUserRelationEntity();
            entity.setId(RandomUtil.uuId());
            entity.setObjectId(map.get(objectId));
            entity.setCreatorUserId(adminId);
            entity.setSortCode(0L);
            entity.setUserId(sysObjId);
            entity.setObjectType("Organize");
            list.add(entity);
        }
        this.saveBatch(list);
    }

}
