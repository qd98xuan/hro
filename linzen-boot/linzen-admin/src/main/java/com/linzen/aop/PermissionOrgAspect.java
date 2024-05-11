package com.linzen.aop;

import com.linzen.constant.PermissionConstant;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.model.organize.OrganizeCreateForm;
import com.linzen.permission.model.organize.OrganizeDepartCreateForm;
import com.linzen.permission.model.organize.OrganizeDepartUpForm;
import com.linzen.permission.model.organize.OrganizeUpForm;
import com.linzen.permission.service.OrganizeService;
import com.linzen.util.PermissionAspectUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Aspect
@Component
public class PermissionOrgAspect implements PermissionAdminBase {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeService organizeService;

    /**
     * 分级管理切点
     */
    @Pointcut("@annotation(com.linzen.annotation.OrganizePermission)")
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
        switch (methodName) {
            case PermissionConstant.METHOD_CREATE:
                return PermissionAspectUtil.getPermitByOrgIds(
                        // 操作目标对象表单对象
                        ((OrganizeCreateForm) pjp.getArgs()[0]).getParentId(),
                        operatorUserId,
                        PermissionConstant.METHOD_CREATE);
            case PermissionConstant.METHOD_CREATE_DEPARTMENT:
                return PermissionAspectUtil.getPermitByOrgIds(
                        // 操作目标对象表单对象
                        ((OrganizeDepartCreateForm) pjp.getArgs()[0]).getParentId(),
                        operatorUserId,
                        PermissionConstant.METHOD_CREATE);
            case PermissionConstant.METHOD_UPDATE:
                // 当前组织id
                String orgId = (String) pjp.getArgs()[0];
                // 当前组织父级id
                SysOrganizeEntity info = organizeService.getInfo(orgId);
                // 修改后的id
                OrganizeUpForm organizeUpForm = (OrganizeUpForm) pjp.getArgs()[1];
                StringJoiner stringJoiner = new StringJoiner(",");
                stringJoiner.add(orgId);
                if (!organizeUpForm.getParentId().equals(info.getParentId()) && !"-1".equals(info.getParentId())) {
                    stringJoiner.add(info.getParentId());
                }
                if (!organizeUpForm.getParentId().equals(info.getParentId()) && !"-1".equals(organizeUpForm.getParentId())) {
                    stringJoiner.add(organizeUpForm.getParentId());
                }
                return PermissionAspectUtil.getPermitByOrgIds(
                        // 操作目标对象ID
                        stringJoiner.toString(),
                        operatorUserId,
                        PermissionConstant.METHOD_UPDATE);
            case PermissionConstant.METHOD_UPDATE_DEPARTMENT:
                // 当前组织id
                String orgIds = (String) pjp.getArgs()[0];
                // 当前组织父级id
                SysOrganizeEntity infos = organizeService.getInfo(orgIds);
                // 修改后的id
                OrganizeDepartUpForm organizeDepartUpForm = (OrganizeDepartUpForm) pjp.getArgs()[1];
                StringJoiner stringJoiners = new StringJoiner(",");
                stringJoiners.add(orgIds);
                if (!organizeDepartUpForm.getParentId().equals(infos.getParentId())) {
                    stringJoiners.add(infos.getParentId());
                    stringJoiners.add(organizeDepartUpForm.getParentId());
                }
                return PermissionAspectUtil.getPermitByOrgIds(
                        // 操作目标对象ID
                        stringJoiners.toString(),
                        operatorUserId,
                        PermissionConstant.METHOD_UPDATE);
            case PermissionConstant.METHOD_DELETE:
            case PermissionConstant.METHOD_DELETE_DEPARTMENT:
                return PermissionAspectUtil.getPermitByOrgIds(
                        // 操作目标对象ID
                        pjp.getArgs()[0].toString(),
                        operatorUserId,
                        PermissionConstant.METHOD_DELETE);
            default:
                return false;
        }
    }
}
