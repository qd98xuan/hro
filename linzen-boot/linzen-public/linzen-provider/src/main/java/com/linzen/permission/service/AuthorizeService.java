package com.linzen.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.model.portalManage.PortalModel;
import com.linzen.base.service.SuperService;
import com.linzen.database.model.superQuery.SuperJsonModel;
import com.linzen.permission.entity.SysAuthorizeEntity;
import com.linzen.permission.model.authorize.*;

import java.util.List;

/**
 * 操作权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public interface AuthorizeService extends SuperService<SysAuthorizeEntity> {

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param userInfo 对象
     * @param singletonOrg
     * @return
     */
    AuthorizeVO getAuthorize(UserInfo userInfo, boolean singletonOrg) throws Exception;

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param isCache 是否存在redis
     * @param singletonOrg
     * @return
     */
    AuthorizeVO getAuthorize(boolean isCache, boolean singletonOrg);

    /**
     * 创建
     *
     * @param objectId      对象主键
     * @param authorizeList 实体对象
     */
    void save(String objectId, AuthorizeDataUpForm authorizeList);

    /**
     * 创建
     *
     * @param saveBatchForm    对象主键
     */
    void saveBatch(SaveBatchForm saveBatchForm, boolean isBatch);

    /**
     * 根据用户id获取列表
     *
     * @param isAdmin 是否管理员
     * @param userId  用户主键
     * @return
     */
    List<SysAuthorizeEntity> getListByUserId(boolean isAdmin, String userId);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    List<SysAuthorizeEntity> getListByObjectId(List<String> objectId);

    /**
     * 判断当前角色是否有权限
     *
     * @param roleId
     * @param systemId
     * @return
     */
    Boolean existAuthorize(String roleId, String systemId);

    /**
     * 判断当前角色是否有权限
     *
     * @param roleId
     * @return
     */
    List<SysAuthorizeEntity> getListByRoleId(String roleId);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @param itemType 对象主键
     * @return
     */
    List<SysAuthorizeEntity> getListByObjectId(String objectId, String itemType);

    /**
     * 根据对象Id获取列表
     *
     * @param objectType 对象主键
     * @return
     */
    List<SysAuthorizeEntity> getListByObjectAndItem(String itemId, String objectType);;

    /**
     * 根据对象Id获取列表
     *
     * @param itemId 对象主键
     * @param itemType 对象类型
     * @return
     */
    List<SysAuthorizeEntity> getListByObjectAndItemIdAndType(String itemId, String itemType);

    <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel);

    void savePortalManage(String portalManageId, SaveAuthForm saveAuthForm);

    void getPortal(List<SysSystemEntity> systemList, List<PortalModel> portalList, Long dateTime, List<String> collect);

    void savePortalAuth(String permissionGroupId, List<String> portalIds);

    List<SuperJsonModel> getConditionSql(String moduleId);

    /**
     * 通过Item获取权限列表
     *
     * @param itemType
     * @param itemId
     * @return
     */
    List<SysAuthorizeEntity> getAuthorizeByItem(String itemType, String itemId);

    AuthorizeVO getAuthorizeByUser(boolean singletonOrg);

    AuthorizeVO getMainSystemAuthorize(List<String> moduleIds, List<String> moduleAuthorize, List<String> moduleUrlAddressAuthorize, boolean singletonOrg);

    List<SysAuthorizeEntity> getListByRoleIdsAndItemType(List<String> roleIds, String itemType);
}
