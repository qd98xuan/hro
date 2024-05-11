package com.linzen.util;

import com.linzen.constant.PermissionConstant;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.util.context.SpringContext;

/**
 * 分级管理工具类
 *
 * @author FHNP SAME
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class PermissionAspectUtil {

    private static final OrganizeService organizeService;
    private static final OrganizeAdministratorService organizeAdministratorService;

    static {
        organizeService = SpringContext.getBean(OrganizeService.class);
        organizeAdministratorService = SpringContext.getBean(OrganizeAdministratorService.class);
    }

    /**
     * 判断是否存在修改前所在的组织的操作
     *
     * @param targetUserId 被操作目标对象ID
     * @param operatorUsrId 操作者ID
     * @param methodName 操作方法
     */
    public static Boolean getPermitByUserId(String targetUserId, String operatorUsrId, String methodName) {
        for(SysOrganizeEntity organizeEntity : organizeService.getAllOrgByUserId(targetUserId)){
            if (PermissionAspectUtil.containPermission(organizeEntity.getId(), operatorUsrId, methodName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 操作者是否含有该组织的操作权限
     *
     * @param organizeId 被操作者所在组织ID
     * @param operatorUserId 当前操作者用户对象ID
     * @param methodName 操作类型：创建、编辑、删除
     */
    public static boolean containPermission(String organizeId, String operatorUserId, String methodName) {
        SysOrganizeEntity organizeEntity = organizeService.getInfo(organizeId);
        if (organizeEntity != null) {
            // 当前用户的所有分级权限
            SysOrganizeAdministratorEntity adminEntity = organizeAdministratorService.getOne(operatorUserId, organizeId);
            if(permissionFlag(adminEntity, methodName, true)){
                return true;
            }
            // 查看父级的组织权限是否含有子集权限
            return parentPermission(organizeEntity.getParentId(), methodName, operatorUserId);
        }
        return false;
    }

    /**
     * 判断是否存在修改前所在的组织的操作(拥有所有的组织权限才能操作)
     *
     * @param organizeIds 组织ID集合字符串
     * @param operatorUsrId 操作者ID
     * @param methodName 操作方法
     */
    public static Boolean getPermitByOrgIds(String organizeIds, String operatorUsrId, String methodName) {
        boolean flag = true;
        for (String organizeId : organizeIds.split(",")) {
            flag = true;
            flag = PermissionAspectUtil.containPermission(organizeId, operatorUsrId, methodName);
            if (!flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 判断是否可修改所在的组织的操作(只要有一个权限即可操作)
     *
     * @param organizeIds 组织ID集合字符串
     * @param operatorUsrId 操作者ID
     * @param methodName 操作方法
     */
    public static Boolean getPermitByOrgId(String organizeIds, String operatorUsrId, String methodName) {
        for (String organizeId : organizeIds.split(",")) {
            if (PermissionAspectUtil.containPermission(organizeId,operatorUsrId, methodName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断组织新建权限(从父级给的子集新建操作权限)
     *
     * @param organizeParentId
     * @param methodName
     * @param userId
     * @return
     */
    private static boolean parentPermission(String organizeParentId, String methodName, String userId) {
        // 得到父级组织
        SysOrganizeEntity parentOrganizeEntity = organizeService.getInfo(organizeParentId);

        if (parentOrganizeEntity != null) {
            // 得到父级的权限
            if(permissionFlag(organizeAdministratorService.getOne(userId, parentOrganizeEntity.getId()), methodName, false)){
                return true;
            }
            // 当前正在判断的组织已经是顶级则无需递归
            if (!"-1".equals(parentOrganizeEntity.getParentId())) {
                return parentPermission(parentOrganizeEntity.getParentId(), methodName, userId);
            }
        }
        return false;
    }

    /**
     * 判断是否具有权限
     * @param adminEntity 分级管理对象
     * @param methodName 操作类型：创建、编辑、删除
     * @param thisFlag true:当前组织 false:子组织
     */
    private static boolean permissionFlag(SysOrganizeAdministratorEntity adminEntity, String methodName, Boolean thisFlag) {
        if (adminEntity != null) {
            String methodType = "";
            // 存在则验证是否有当前组织分级管理
            try {
                switch (methodName) {
                    case PermissionConstant.METHOD_CREATE:
                        // 创建权限
                        methodType = PermissionConstant.GET_METHOD_CREATE;
                        break;
                    case PermissionConstant.METHOD_UPDATE:
                        // 编辑权限
                        methodType = PermissionConstant.GET_METHOD_UPDATE;
                        break;
                    case PermissionConstant.METHOD_DELETE:
                        // 删除权限
                        methodType = PermissionConstant.GET_METHOD_DELETE;
                        break;
                    default:
                        break;
                }
                // 拼接方法名
                String method = (thisFlag ? PermissionConstant.GET_METHOD_THIS : PermissionConstant.GET_METHOD_SUB) + methodType;
                String selectMethod = (thisFlag ? PermissionConstant.GET_METHOD_THIS : PermissionConstant.GET_METHOD_SUB) + PermissionConstant.GET_METHOD_SELECT;
                if ((int) SysOrganizeAdministratorEntity.class.getMethod(method).invoke(adminEntity) == 1 && (int) SysOrganizeAdministratorEntity.class.getMethod(selectMethod).invoke(adminEntity) == 1) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
