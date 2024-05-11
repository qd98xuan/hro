package com.linzen.aop;

import com.linzen.constant.PermissionConstant;
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
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
public class PermissionAdminAspect implements PermissionAdminBase{

    @Autowired
    private UserProvider userProvider;

    /**
     * 分级管理切点
     */
    @Pointcut("@annotation(com.linzen.annotation.OrganizeAdminIsTrator)")
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
    public Boolean detailPermission(ProceedingJoinPoint pjp, String operatorUserId, String methodName){
        switch (methodName) {
            case PermissionConstant.METHOD_SAVE:
            case PermissionConstant.METHOD_SAVE_BATCH:
                return true;
            case PermissionConstant.METHOD_UPDATE:
                //判断是否有当前组织的修改权限
                String organizeId = String.valueOf(pjp.getArgs()[0]);
                return PermissionAspectUtil.containPermission(organizeId, operatorUserId, methodName);
            default:
                return false;
        }
    }

}
