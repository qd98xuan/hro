package com.linzen.aop;

import com.linzen.constant.PermissionConstant;
import com.linzen.permission.entity.SysOrganizeRelationEntity;
import com.linzen.permission.entity.SysRoleEntity;
import com.linzen.permission.model.role.RoleCrForm;
import com.linzen.permission.model.role.RoleUpForm;
import com.linzen.permission.service.OrganizeRelationService;
import com.linzen.permission.service.RoleService;
import com.linzen.util.PermissionAspectUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 角色操作权限
 *
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
public class PermissionRoleAspect implements PermissionAdminBase {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeRelationService organizeRelationService;

    /**
     * 分级管理切点
     */
    @Pointcut("@annotation(com.linzen.annotation.RolePermission)")
    public void pointcut() {
    }

    /**
     * 分级管理切点
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return PermissionAdminBase.permissionCommon(pjp, userProvider, this);
    }

    @Override
    public Boolean detailPermission(ProceedingJoinPoint pjp, String operatorUserId, String methodName) {
        boolean flag = false;
        switch (methodName) {
            case PermissionConstant.METHOD_CREATE:
                RoleCrForm roleCrForm = (RoleCrForm) pjp.getArgs()[0];
                if (!checkAdminGlobal(roleCrForm.getGlobalMark(), userProvider)) {
                    return PermissionAspectUtil.getPermitByOrgId(
                            // 操作目标对象组织ID集合
                            getOrganize(roleCrForm.getOrganizeIdsTree()),
                            operatorUserId,
                            PermissionConstant.METHOD_CREATE);
                }
                return true;
            case PermissionConstant.METHOD_UPDATE:
                RoleUpForm roleUpForm = (RoleUpForm) pjp.getArgs()[0];
                // 非管理员情况下
                if (!checkAdminGlobal(roleUpForm.getGlobalMark(), userProvider)) {
                    // 得到以前的组织id
                    String roleId = (String) pjp.getArgs()[1];
                    List<String> relationListByRoleId = organizeRelationService.getRelationListByRoleId(roleId).stream().map(SysOrganizeRelationEntity::getOrganizeId).collect(Collectors.toList());
                    StringJoiner stringJoiners = new StringJoiner(",");
                    relationListByRoleId.forEach(t -> {
                        stringJoiners.add(t);
                    });
                    if (PermissionAspectUtil.getPermitByOrgId(
                            // 操作目标对象组织ID集合
                            stringJoiners.toString(),
                            operatorUserId,
                            PermissionConstant.METHOD_UPDATE)) {
                        return PermissionAspectUtil.getPermitByOrgId(
                                // 操作目标对象组织ID集合
                                getOrganize(roleUpForm.getOrganizeIdsTree()),
                                operatorUserId,
                                PermissionConstant.METHOD_UPDATE);
                    }
                    return false;
                }
                return true;
            case PermissionConstant.METHOD_DELETE:
                String roleId = pjp.getArgs()[0].toString();
                SysRoleEntity roleEntity = roleService.getInfo(roleId);
                // 获取角色关联的组织信息
                List<SysOrganizeRelationEntity> relationListByRoleId = organizeRelationService.getRelationListByRoleId(roleId);
                StringBuilder orgId = new StringBuilder();
                relationListByRoleId.stream().forEach(t->{
                    orgId.append(t.getOrganizeId() + ",");
                });
                if (roleEntity != null && !checkAdminGlobal(roleEntity.getGlobalMark(), userProvider)) {
                    return PermissionAspectUtil.getPermitByOrgId(
                            // 操作目标对象组织ID集合
                            orgId.toString(),
                            operatorUserId,
                            PermissionConstant.METHOD_DELETE);
                }
                return true;
            default:
                break;
        }
        return true;
    }

    /**
     * 转成组织id字符串
     * @param orgIdsTree
     * @return
     */
    private String getOrganize(List<List<String>> orgIdsTree) {
        StringBuilder orgIds = new StringBuilder();
        for (List<String> list : orgIdsTree) {
            if (list.size() > 0) {
                String orgId = list.get(list.size() - 1);
                orgIds.append(orgId + ",");
            }
        }
        return orgIds.toString();
    }

    /**
     * 全局角色只能超管来操作
     *
     * @param globalMark   全局标识 1：全局 0: 非全局
     * @param userProvider 操作者
     */
    private Boolean checkAdminGlobal(Integer globalMark, UserProvider userProvider) {
        if (globalMark != null && globalMark == 1) {
            return userProvider.get().getIsAdministrator();
        }
        return false;
    }

}
