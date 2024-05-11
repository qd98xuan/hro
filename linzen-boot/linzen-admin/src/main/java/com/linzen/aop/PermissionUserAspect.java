package com.linzen.aop;

import com.linzen.constant.PermissionConst;
import com.linzen.constant.PermissionConstant;
import com.linzen.permission.entity.SysOrganizeRelationEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.model.user.form.UserCreateForm;
import com.linzen.permission.model.user.form.UserUpdateForm;
import com.linzen.permission.model.userrelation.UserRelationForm;
import com.linzen.permission.service.OrganizeRelationService;
import com.linzen.permission.service.PositionService;
import com.linzen.permission.service.UserRelationService;
import com.linzen.util.PermissionAspectUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
public class PermissionUserAspect implements PermissionAdminBase{

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserRelationService userRelationService;

    /**
     * 分级管理切点
     */
    @Pointcut("@annotation(com.linzen.annotation.UserPermission)")
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
        switch (methodName){
            case PermissionConstant.METHOD_CREATE:
                UserCreateForm userCrForm = (UserCreateForm) pjp.getArgs()[0];
                return PermissionAspectUtil.getPermitByOrgId(
                                        // 操作目标对象组织ID集合
                                        userCrForm.getOrganizeId(),
                                        operatorUserId,
                                        PermissionConstant.METHOD_CREATE);
            case PermissionConstant.METHOD_UPDATE:
                // 得到修改的用户以前的信息
                String userId = (String) pjp.getArgs()[0];
                List<String> collect = userRelationService.getListByUserId(userId, PermissionConst.ORGANIZE).stream().map(SysUserRelationEntity::getObjectId).collect(Collectors.toList());
                StringJoiner stringJoiner = new StringJoiner(",");
                collect.forEach(t -> {
                    stringJoiner.add(t);
                });
                if (PermissionAspectUtil.getPermitByOrgId(
                                // 操作目标对象组织ID集合
                                stringJoiner.toString(),
                                operatorUserId,
                                PermissionConstant.METHOD_UPDATE)) {
                    return PermissionAspectUtil.getPermitByOrgId(
                                    // 操作目标对象组织ID集合
                                    ((UserUpdateForm) pjp.getArgs()[1]).getOrganizeId(),
                                    operatorUserId,
                                    PermissionConstant.METHOD_UPDATE);
                }
                return false;
            case PermissionConstant.METHOD_MODIFY_PW:
                return PermissionAspectUtil.getPermitByUserId(
                                        // 操作目标对象的ID
                                        String.valueOf(pjp.getArgs()[0]),
                                        operatorUserId,
                                        PermissionConstant.METHOD_UPDATE);
            case PermissionConstant.METHOD_DELETE:
                return PermissionAspectUtil.getPermitByUserId(
                                        // 操作目标对象的ID
                                        pjp.getArgs()[0].toString(),
                                        operatorUserId,
                                        PermissionConstant.METHOD_DELETE);
            case PermissionConstant.METHOD_SAVE:
                String objId = pjp.getArgs()[0].toString();
                UserRelationForm userRelationForm = (UserRelationForm)pjp.getArgs()[1];

                List<String> orgIds = new ArrayList<>();
                if(userRelationForm.getObjectType().equals(PermissionConst.ROLE)){
                    // 角色目前修改为只有超管才能够修改
                    if(userProvider.get().getIsAdministrator()){
                        return true;
                    }
                    orgIds.addAll(organizeRelationService.getRelationListByRoleId(objId).stream().map(SysOrganizeRelationEntity::getOrganizeId).collect(Collectors.toList()));
                    return PermissionAspectUtil.getPermitByOrgId(
                            // 操作目标对象组织ID集合
                            String.join(",", orgIds),
                            operatorUserId,
                            PermissionConstant.METHOD_UPDATE);
                }else {
                    if(userRelationForm.getObjectType().equals(PermissionConst.GROUP)) {
                        return true;
                    }
                    if(userRelationForm.getObjectType().equals(PermissionConst.POSITION)) {
                        orgIds.add(positionService.getInfo(objId).getOrganizeId());
                    }
                    return PermissionAspectUtil.getPermitByOrgId(
                            String.join(",", orgIds),
                            operatorUserId,
                            PermissionConstant.METHOD_UPDATE);
                }
            case PermissionConstant.METHOD_DELETE_SOCIALS:
                if(pjp.getArgs()[0].toString().equals(operatorUserId)){return true;}
                return PermissionAspectUtil.getPermitByUserId(
                        // 操作目标对象的ID
                        pjp.getArgs()[0].toString(),
                        operatorUserId,
                        PermissionConstant.METHOD_UPDATE);
            default:
                return false;
        }
    }





}
