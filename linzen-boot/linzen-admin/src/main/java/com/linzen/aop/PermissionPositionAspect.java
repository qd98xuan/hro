package com.linzen.aop;

import com.linzen.constant.PermissionConstant;
import com.linzen.permission.entity.SysPositionEntity;
import com.linzen.permission.model.position.PositionCrForm;
import com.linzen.permission.model.position.PositionUpForm;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.PositionService;
import com.linzen.util.PermissionAspectUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
public class PermissionPositionAspect implements PermissionAdminBase{

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrganizeService organizeService;

    /**
     * 分级管理切点
     */
    @Pointcut("@annotation(com.linzen.annotation.PositionPermission)")
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
                return PermissionAspectUtil.getPermitByOrgId(
                        // 操作目标对象组织ID集合
                        ((PositionCrForm) pjp.getArgs()[0]).getOrganizeId(),
                        operatorUserId,
                        methodName);
            case PermissionConstant.METHOD_UPDATE:
                // 得到岗位信息后，判断是否有修改前的权限
                SysPositionEntity info = positionService.getInfo(((String) pjp.getArgs()[0]));
                if (PermissionAspectUtil.getPermitByOrgId(
                        // 操作目标对象组织ID集合
                        info.getOrganizeId(),
                        operatorUserId,
                        methodName)) {
                    return PermissionAspectUtil.getPermitByOrgId(
                            // 操作目标对象组织ID集合
                            ((PositionUpForm) pjp.getArgs()[1]).getOrganizeId(),
                            operatorUserId,
                            methodName);
                }
                return false;
            case PermissionConstant.METHOD_DELETE:
                // 获取岗位所关联的组织ID字符串
                String positionId = String.valueOf(pjp.getArgs()[0]);
                String orgIds = organizeService.getInfo(positionService.getInfo(positionId).getOrganizeId()).getId();
                return PermissionAspectUtil.getPermitByOrgId(
                        // 操作目标对象组织ID集合
                        orgIds,
                        operatorUserId,
                        PermissionConstant.METHOD_DELETE);
            default:
                return false;
        }
    }
}
