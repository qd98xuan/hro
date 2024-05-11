package com.linzen.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.linzen.base.service.SuperServiceImpl;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.*;
import com.linzen.permission.mapper.RoleMapper;
import com.linzen.permission.model.role.RolePagination;
import com.linzen.permission.service.*;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统角色
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, SysRoleEntity> implements RoleService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeAdministratorService organizeAdministratorService;

    @Override
    public List<SysRoleEntity> getList(boolean filterdelFlag) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        if (filterdelFlag) {
            queryWrapper.lambda().eq(SysRoleEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByAsc(SysRoleEntity::getSortCode).orderByDesc(SysRoleEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysRoleEntity> getList(RolePagination pagination, Integer globalMark) {
        // 需要查询哪些组织
        List<String> orgIds = new ArrayList<>();
        // 所有有权限的组织
        Set<String> orgId = new HashSet<>(16);
        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            // 判断自己是哪些组织的管理员
            listss.forEach(t -> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgId.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        orgId.addAll(underOrganizations);
                    }
                }
            });
        } else {
            orgId.addAll(organizeService.getOrgMapsAll(SysOrganizeEntity::getId).keySet());
        }

        if (!StringUtil.isEmpty(pagination.getOrganizeId())) {
            List<String> underOrganizations = organizeService.getUnderOrganizations(pagination.getOrganizeId(), false);
            // 判断哪些组织时有权限的
            List<String> collect = underOrganizations.stream().filter(orgId::contains).collect(Collectors.toList());
            orgIds.add(pagination.getOrganizeId());
            orgIds.addAll(collect);
            orgIds.add(pagination.getOrganizeId());
        } else {
            if (orgId.size() == 0) {
                return new ArrayList<>();
            }
            QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
            if (StringUtil.isNotEmpty(pagination.getKeyword())) {
                queryWrapper.lambda().and(
                        t -> t.like(SysRoleEntity::getFullName, pagination.getKeyword())
                                .or().like(SysRoleEntity::getEnCode, pagination.getKeyword())
                );
            }
            if (globalMark > -1) {
                queryWrapper.lambda().eq(SysRoleEntity::getGlobalMark, globalMark);
            }
            if (!userProvider.get().getIsAdministrator()) {
                queryWrapper.lambda().ne(SysRoleEntity::getGlobalMark, 1);
                List<String> collect = organizeRelationService.getRelationListByOrganizeId(new ArrayList<>(orgId), PermissionConst.ROLE).stream().map(SysOrganizeRelationEntity::getObjectId).collect(Collectors.toList());
                if (collect.size() == 0) {
                    collect.add("");
                }
                queryWrapper.lambda().in(SysRoleEntity::getId, collect);
            }
            if (pagination.getEnabledMark() != null) {
                queryWrapper.lambda().eq(SysRoleEntity::getEnabledMark, pagination.getEnabledMark());
            }
            long count = this.count(queryWrapper);
            queryWrapper.lambda().orderByAsc(SysRoleEntity::getSortCode).orderByDesc(SysRoleEntity::getCreatorTime);
            Page<SysRoleEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize(), count, false);
            page.setOptimizeCountSql(false);
            IPage<SysRoleEntity> iPage = this.page(page, queryWrapper);
            return pagination.setData(iPage.getRecords(), page.getTotal());
        }

        String keyword = "";
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            keyword = "%" + pagination.getKeyword() + "%";
        }
        PageHelper.startPage((int) pagination.getCurrentPage(), (int) pagination.getPageSize(), false);
        PageMethod.getLocalPage().keepOrderBy(true);
        List<String> query = this.baseMapper.query(orgIds, keyword, globalMark, pagination.getEnabledMark());
        Long count = this.baseMapper.count(orgIds, keyword, globalMark, pagination.getEnabledMark());
        PageInfo pageInfo = new PageInfo(query);
        // 赋值分页参数
        pagination.setTotal(count);
        pagination.setCurrentPage(pageInfo.getPageNum());
        pagination.setPageSize(pageInfo.getPageSize());
        if (pageInfo.getList() != null && pageInfo.getList().size() > 0) {
            QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysRoleEntity::getId, pageInfo.getList());
            queryWrapper.lambda().orderByAsc(SysRoleEntity::getSortCode).orderByDesc(SysRoleEntity::getCreatorTime);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SysRoleEntity> getListByUserId(String userId) {
        QueryWrapper<SysRoleEntity> query = new QueryWrapper<>();
        List<String> roleRelations = userRelationService.getListByObjectType(userId, PermissionConst.ROLE).stream()
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        if(roleRelations.size() > 0){
            query.lambda().in(SysRoleEntity::getId, roleRelations);
            return this.list(query);
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SysRoleEntity> getListByUserIdAndOrgId(String userId, String orgId) {
        return getListByUserId(userId).stream()
                .filter(role-> organizeRelationService.existByRoleIdAndOrgId(role.getId(), orgId))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleIdsByCurrentUser() {
        SysUserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        return getAllRoleIdsByUserIdAndOrgId(userEntity.getId(), userEntity.getOrganizeId());
    }

    @Override
    public List<String> getRoleIdsByCurrentUser(String orgId) {
        SysUserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        return getAllRoleIdsByUserIdAndOrgId(userEntity.getId(), orgId);
    }

    @Override
    public List<String> getAllRoleIdsByUserIdAndOrgId(String userId, String orgId) {
        // 用户当前组织下的角色
        List<String> roleIds = getListByUserIdAndOrgId(userId, orgId).stream()
                .map(SysRoleEntity::getId).collect(Collectors.toList());
        // 用户绑定的全局角色
        List<String> globalRoleIds = userRelationService.getListByUserIdAndObjType(userId, PermissionConst.ROLE).stream()
                .map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
        globalRoleIds = roleService.getListByIds(globalRoleIds, null, false).stream().filter(r -> r.getGlobalMark() != null && r.getGlobalMark() == 1)
                .map(SysRoleEntity::getId).collect(Collectors.toList());
        roleIds.addAll(globalRoleIds);
        return roleIds.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public SysRoleEntity getInfo(String id) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean isExistByFullName(String fullName, String id, Integer globalMark) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleEntity::getFullName, fullName);
        queryWrapper.lambda().eq(SysRoleEntity::getGlobalMark, globalMark);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SysRoleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public Boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SysRoleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public Boolean update(String id, SysRoleEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void create(SysRoleEntity entity) {
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    @Transactional
    public void delete(SysRoleEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
            QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, entity.getId());
            authorizeService.remove(queryWrapper);
            QueryWrapper<SysUserRelationEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(SysUserRelationEntity::getObjectId, entity.getId());
            userRelationService.remove(wrapper);
        }
    }
    @Override
    public List<SysRoleEntity> getListByIds(List<String> id, String keyword, boolean filterdelFlag) {
        List<SysRoleEntity> roleList = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysRoleEntity::getId, id);
            if (filterdelFlag) {
                queryWrapper.lambda().eq(SysRoleEntity::getEnabledMark, 1);
            }
            if (StringUtil.isNotEmpty(keyword)) {
                queryWrapper.lambda().and(
                        t -> t.like(SysRoleEntity::getFullName, keyword)
                                .or().like(SysRoleEntity::getEnCode, keyword)
                );
            }
            roleList = this.list(queryWrapper);
        }
        return roleList;
    }

    @Override
    public List<SysRoleEntity> getSwaptListByIds(Set<String> roleIds) {
        if (roleIds.size()>0){
            QueryWrapper<SysRoleEntity> roleWrapper = new QueryWrapper<>();
            roleWrapper.lambda().select(SysRoleEntity::getFullName, SysRoleEntity::getId).in(SysRoleEntity::getId,roleIds);
            List<SysRoleEntity> list = roleService.list(roleWrapper);
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getRoleMap() {
        QueryWrapper<SysRoleEntity> roleWrapper = new QueryWrapper<>();
        roleWrapper.lambda().select(SysRoleEntity::getFullName, SysRoleEntity::getId);
        List<SysRoleEntity> list = roleService.list(roleWrapper);
        return list.stream().collect(Collectors.toMap(SysRoleEntity::getId, SysRoleEntity::getFullName));
    }

    @Override
    public Map<String, Object> getRoleNameAndIdMap() {
        QueryWrapper<SysRoleEntity> roleWrapper = new QueryWrapper<>();
        List<SysRoleEntity> list = roleService.list(roleWrapper);
        Map<String, Object> roleNameMap = new HashMap<>();
        list.stream().forEach(role->roleNameMap.put(role.getFullName() + "/" + role.getEnCode(),role.getId()));
        return roleNameMap;
    }

    @Override
    public SysRoleEntity getInfoByFullName(String fullName) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleEntity::getFullName, fullName);
        return this.getOne(queryWrapper);
    }

    @Override
    public SysRoleEntity getInfoByFullName(String fullName, String enCode) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleEntity::getFullName, fullName);
        queryWrapper.lambda().eq(SysRoleEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SysRoleEntity> getGlobalList() {
        QueryWrapper<SysRoleEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysRoleEntity::getGlobalMark, 1).eq(SysRoleEntity::getEnabledMark, 1);
        return this.list(query);
    }

    @Override
    public List<SysRoleEntity> getGlobalList(List<String> ids) {
        if (ids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysRoleEntity> query = new QueryWrapper<>();
        query.lambda().in(SysRoleEntity::getId, ids);
        query.lambda().eq(SysRoleEntity::getGlobalMark, 1).eq(SysRoleEntity::getEnabledMark, 1);
        return this.list(query);
    }

    @Override
    public Boolean existCurRoleByOrgId(String orgId) {
        List<SysUserRelationEntity> roleRelationList = userRelationService
                .getListByObjectType(userProvider.get().getUserId(), PermissionConst.ROLE);
        for(SysUserRelationEntity userRelationEntity : roleRelationList){
            if(organizeRelationService.existByRoleIdAndOrgId(userRelationEntity.getObjectId(), orgId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SysRoleEntity> getCurRolesByOrgId(String orgId) {
        String userId = userProvider.get().getUserId();
        List<SysUserRelationEntity> userRelations = userRelationService.getListByObjectType(userId, PermissionConst.ROLE);
        List<SysRoleEntity> roles = new ArrayList<>();
        userRelations.forEach(ur->{
            // 获取全局角色
            SysRoleEntity roleEntity = this.getInfo(ur.getObjectId());
            if(roleEntity != null && roleEntity.getGlobalMark() != null && roleEntity.getGlobalMark() == 1 && roleEntity.getEnabledMark() == 1) {
                roles.add(roleEntity);
            }else {
                organizeRelationService.getRelationListByRoleId(ur.getObjectId()).forEach(or -> {
                    if (roleEntity.getEnabledMark() == 1 && or.getOrganizeId().equals(orgId)) {
                        roles.add(roleEntity);
                    }
                });
            }
        });
        return roles;
    }

    @Override
    public List<SysRoleEntity> getRolesByOrgId(String orgId) {
        List<String> ids = new ArrayList<>();
        organizeRelationService.getListByTypeAndOrgId(PermissionConst.ROLE, orgId).forEach(o->{
            ids.add(o.getObjectId());
        });
        QueryWrapper<SysRoleEntity> query = new QueryWrapper<>();
        if(ids.size() > 0){
            query.lambda().in(SysRoleEntity::getId, ids);
            return this.list(query);
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getBindInfo(String roleId, List<String> reduceOrgIds){
        if(reduceOrgIds.size() > 0){
            StringBuilder info = new StringBuilder();
            SysRoleEntity roleEntity = this.getInfo(roleId);
            List<SysUserRelationEntity> bingUserByRoleList = userRelationService.getListByObjectId(roleId, PermissionConst.ROLE);
            if(bingUserByRoleList.size() < 1){
                return null;
            }
            info.append("已绑定用户：");
            boolean bindFlag = false;
            for (SysUserRelationEntity bingUser : bingUserByRoleList) {
                String userId = bingUser.getUserId();
                if(roleEntity.getGlobalMark() == 1){
                    SysUserEntity user = userService.getInfo(userId);
                    info.append("[ ").append(user.getRealName()).append("/").append(user.getAccount()).append(" ] ");
                    bindFlag = true;
                }else {
                    // 这个用户所绑定的组织
                    List<SysUserRelationEntity> bingUserByOrg = userRelationService.getListByObjectType(userId, PermissionConst.ORGANIZE);
                    for (SysUserRelationEntity bingOrg : bingUserByOrg) {
                        String orgId = bingOrg.getObjectId();
                        if(reduceOrgIds.contains(orgId)){
                            SysOrganizeEntity org = organizeService.getInfo(orgId);
                            SysUserEntity user = userService.getInfo(bingOrg.getUserId());
                            info.append("[").append(org.getFullName()).append("：用户（").append(user.getRealName()).append("）]; ");
                            bindFlag = true;
                        }
                    }
                }
            }

            if(bindFlag){
                return info.toString();
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
