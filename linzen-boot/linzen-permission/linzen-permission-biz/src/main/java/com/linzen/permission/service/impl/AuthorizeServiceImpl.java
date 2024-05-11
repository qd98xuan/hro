package com.linzen.permission.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.base.UserInfo;
import com.linzen.base.entity.*;
import com.linzen.base.model.base.SystemBaeModel;
import com.linzen.base.model.button.ButtonModel;
import com.linzen.base.model.column.ColumnModel;
import com.linzen.base.model.form.ModuleFormModel;
import com.linzen.base.model.module.ModuleModel;
import com.linzen.base.model.portalManage.PortalManagePageDO;
import com.linzen.base.model.portalManage.PortalModel;
import com.linzen.base.model.resource.ResourceModel;
import com.linzen.base.service.*;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.PermissionConst;
import com.linzen.database.constant.DbConst;
import com.linzen.database.model.dto.PrepSqlDTO;
import com.linzen.database.model.superQuery.SuperJsonModel;
import com.linzen.database.model.superQuery.SuperQueryJsonModel;
import com.linzen.database.sql.util.SqlFrameFastUtil;
import com.linzen.database.util.DataSourceUtil;
import com.linzen.database.util.DbTypeUtil;
import com.linzen.database.util.JdbcUtil;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.emnus.SearchMethodEnum;
import com.linzen.model.tenant.TenantAuthorizeModel;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.config.ConfigModel;
import com.linzen.permission.constant.AuthorizeConst;
import com.linzen.permission.entity.SysAuthorizeEntity;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.mapper.AuthorizeMapper;
import com.linzen.permission.model.authorize.*;
import com.linzen.permission.model.portalManage.AuthorizePortalManagePrimary;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 操作权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Service
public class AuthorizeServiceImpl extends SuperServiceImpl<AuthorizeMapper, SysAuthorizeEntity> implements AuthorizeService {

    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeAdministratorService organizeAdminIsTratorService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ModuleButtonService moduleButtonService;
    @Autowired
    private ModuleColumnService moduleColumnService;
    @Autowired
    private ModuleFormService moduleFormService;
    @Autowired
    private ModuleDataAuthorizeSchemeService dataAuthorizeSchemeApi;
    @Autowired
    private PortalManageService portalManageApi;

    @Override
    public AuthorizeVO getAuthorize(boolean isCache, boolean singletonOrg) {
        // 是否从缓冲里面获取权限
        if (isCache) {
            return getCacheAuthor(userProvider.get());
        } else {
            return getAuthorize(userProvider.get(), singletonOrg);
        }
    }

    private AuthorizeVO getCacheAuthor(UserInfo userInfo) {
        // 是否从缓冲里面获取权限
        String cacheKey = cacheKeyUtil.getUserAuthorize() + userInfo.getUserId();
        if (!redisUtil.exists(cacheKey)) {
            AuthorizeVO authorizeModel = getAuthorize(userInfo, false);
            if (authorizeModel.getModuleList().size() != 0) {
                redisUtil.insert(cacheKey, authorizeModel, 60);
            }
            return authorizeModel;
        } else {
            return JsonUtil.createJsonToBean(redisUtil.getString(cacheKey).toString(), AuthorizeVO.class);
        }
    }

    @Override
    public AuthorizeVO getAuthorize(UserInfo userInfo, boolean singletonOrg) {
        List<ModuleModel> moduleList = new ArrayList<>();
        List<ButtonModel> buttonList = new ArrayList<>();
        List<ColumnModel> columnList = new ArrayList<>();
        List<ResourceModel> resourceList = new ArrayList<>();
        List<ModuleFormModel> formsList = new ArrayList<>();
        List<SystemBaeModel> systemList = new ArrayList<>();
        Boolean isAdmin = userInfo.getIsAdministrator();

        SysSystemEntity entity = systemService.getInfoByEnCode(LinzenConst.MAIN_SYSTEM_CODE);
        List<String> moduleAuthorize = new ArrayList<>();
        List<String> moduleUrlAddressAuthorize = new ArrayList<>();
        if (configValueUtil.isMultiTenancy()) {
            TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(userInfo.getTenantId());
            moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
            moduleUrlAddressAuthorize = tenantAuthorizeModel.getUrlAddressList();
        }
        if (!isAdmin) {
            List<String> roleIds = new ArrayList<>();
//            roleService.getRoleIdsByCurrentUser(userInfo.getOrganizeId()).forEach(role -> {
//                RoleEntity info = roleService.getInfo(role);
//                //判断角色状态是否为有效，显示当前组织内角色的并集
//                if (info != null && info.getEnabledMark() == 1) {
//                    roleIds.add("'" + role + "'");
//                }
//            });
                // 应用 无开发平台
            List<SysOrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getInfoByUserId(userInfo.getUserId());
            if (organizeAdministratorEntity.size() > 0) {
                List<SysOrganizeAdministratorEntity> listByUserId = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userInfo.getUserId(), PermissionConst.SYSTEM, true);
                List<SysSystemEntity> systemEntities = systemService.getListByIds(listByUserId.stream().map(SysOrganizeAdministratorEntity::getOrganizeId).collect(Collectors.toList()), moduleAuthorize);
                List<SystemBaeModel> systemJsonToList = JsonUtil.createJsonToList(systemEntities, SystemBaeModel.class);
                systemList.addAll(systemJsonToList);
                List<String> systemIds = systemJsonToList.stream().map(SystemBaeModel::getId).collect(Collectors.toList());
                List<ModuleEntity> moduleBySystemIds = moduleService.getModuleBySystemIds(systemIds, moduleAuthorize, moduleUrlAddressAuthorize);
                List<ModuleModel> moduleJsonToList = JsonUtil.createJsonToList(moduleBySystemIds, ModuleModel.class);
                moduleList.addAll(moduleJsonToList);
                List<ModuleButtonEntity> buttonByModuleId = moduleButtonService.getListByModuleIds(moduleJsonToList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
                List<ButtonModel> buttonJsonToList = JsonUtil.createJsonToList(buttonByModuleId, ButtonModel.class);
                buttonList.addAll(buttonJsonToList);
                List<ModuleColumnEntity> columnByModuleId = moduleColumnService.getListByModuleId(moduleJsonToList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
                List<ColumnModel> columnJsonToList = JsonUtil.createJsonToList(columnByModuleId, ColumnModel.class);
                columnList.addAll(columnJsonToList);
                List<ModuleDataAuthorizeSchemeEntity> resourceByModuleId = dataAuthorizeSchemeApi.getListByModuleId(moduleJsonToList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
                List<ResourceModel> resourceJsonToList = JsonUtil.createJsonToList(resourceByModuleId, ResourceModel.class);
                resourceList.addAll(resourceJsonToList);
                List<ModuleFormEntity> formByModuleId = moduleFormService.getListByModuleId(moduleJsonToList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
                List<ModuleFormModel> formJsonToList = JsonUtil.createJsonToList(formByModuleId, ModuleFormModel.class);
                formsList.addAll(formJsonToList);
            } else {
                List<String> roleIdList = new ArrayList<>();
                permissionGroupService.getPermissionGroupByUserId(userInfo.getUserId(), userInfo.getOrganizeId(), true, null).forEach(t -> {
                    roleIdList.add(t.getId());
                    roleIds.add("'" + t.getId() + "'");
                });
                if (roleIds.size() > 0) {
                    String roleIdsStr = String.join(",", roleIds);
                    List<SysAuthorizeEntity> listByRoleIdsAndItemType = getListByRoleIdsAndItemType(roleIdList, AuthorizeConst.SYSTEM);
                    List<SysSystemEntity> systemAdmin = systemService.getListByIds(listByRoleIdsAndItemType.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList()), moduleAuthorize);
                    systemList = JsonUtil.createJsonToList(systemAdmin, SystemBaeModel.class);
                    systemList = systemList.stream().distinct().collect(Collectors.toList());
                    // 菜单 无开发平台
                    moduleList = this.baseMapper.findModule(roleIdsStr, entity.getId(), moduleAuthorize, moduleUrlAddressAuthorize, singletonOrg ? 0 : 1);
                    moduleList = moduleList.stream().distinct().collect(Collectors.toList());
                    // 按钮
                    buttonList = this.baseMapper.findButton(roleIdsStr);
                    buttonList = buttonList.stream().distinct().collect(Collectors.toList());
                    // 列表
                    columnList = this.baseMapper.findColumn(roleIdsStr);
                    columnList = columnList.stream().distinct().collect(Collectors.toList());
                    // 数据
                    resourceList = this.baseMapper.findResource(roleIdsStr);
                    resourceList = resourceList.stream().distinct().collect(Collectors.toList());
                    // 表单
                    formsList = this.baseMapper.findForms(roleIdsStr);
                    formsList = formsList.stream().distinct().collect(Collectors.toList());
                }
            }

        } else {
            List<ModuleEntity> moduleAdmin = moduleService.findModuleAdmin(singletonOrg ? 0 : 1, entity.getId(), moduleAuthorize, moduleUrlAddressAuthorize);
            moduleList = JsonUtil.createJsonToList(moduleAdmin, ModuleModel.class);
            buttonList = this.baseMapper.findButtonAdmin(1);
            columnList = this.baseMapper.findColumnAdmin(1);
            resourceList = this.baseMapper.findResourceAdmin(1);
            formsList = this.baseMapper.findFormsAdmin(1);
            List<SysSystemEntity> systemAdmin = systemService.findSystemAdmin(singletonOrg ? 0 : 1, LinzenConst.MAIN_SYSTEM_CODE, moduleAuthorize);
            systemList = JsonUtil.createJsonToList(systemAdmin, SystemBaeModel.class);
        }
        return new AuthorizeVO(moduleList, buttonList, columnList, resourceList, formsList, systemList);
    }

    @Override
    public void savePortalManage(String portalManageId, SaveAuthForm saveAuthForm) {
        final String AUTHORIZE_ROLE = "role";
        String userId = UserProvider.getLoginUserId();
        // 原始授权角色
        List<SysAuthorizeEntity> list = new ArrayList<>();
        for (int i = 0; i < saveAuthForm.getObjectId().length; i++) {
            SysAuthorizeEntity authorizeEntity = new SysAuthorizeEntity();
            authorizeEntity.setId(RandomUtil.uuId());
            authorizeEntity.setItemType(AuthorizeConst.AUTHORIZE_PORTAL_MANAGE);
            authorizeEntity.setItemId(portalManageId);
            authorizeEntity.setObjectType(AUTHORIZE_ROLE);
            authorizeEntity.setObjectId(saveAuthForm.getObjectId()[i]);
            authorizeEntity.setSortCode((long) i);
            authorizeEntity.setCreatorTime(new Date());
            authorizeEntity.setCreatorUserId(userId);
            list.add(authorizeEntity);
        }
        remove(new AuthorizePortalManagePrimary(null, portalManageId).getQuery());
        saveBatch(list);
    }

    @Override
    public void getPortal(List<SysSystemEntity> systemList, List<PortalModel> portalList, Long dateTime, List<String> collect) {
        Map<String, SysSystemEntity> systemBaeModelMap = systemList.stream().collect(Collectors.toMap(SysSystemEntity::getId, Function.identity()));
        List<String> systemIds = systemList.stream().map(SysSystemEntity::getId).collect(Collectors.toList());
        List<PortalManagePageDO> portalManagePageDOS = portalManageApi.selectPortalBySystemIds(systemIds, collect);
        if (portalManagePageDOS.size() == 0) {
            return;
        }
        Map<String, List<PortalManagePageDO>> systemIdAndPortalMap = portalManagePageDOS.stream().collect(Collectors.groupingBy(PortalManagePageDO::getSystemId));
        if (systemIdAndPortalMap != null) {
            systemIdAndPortalMap.keySet().forEach(t -> {
                if (Optional.ofNullable(systemBaeModelMap.get(t)).isPresent()) {
                    PortalModel systemModel = BeanUtil.toBean(systemBaeModelMap.get(t), PortalModel.class);
                    systemModel.setParentId("-1");
                    portalList.add(systemModel);
                    Map<String, String> platFormId = new HashMap<>();
                    List<PortalManagePageDO> portalManagePageDOList = systemIdAndPortalMap.get(t);
                    Map<String, List<PortalManagePageDO>> platFormMap = portalManagePageDOList.stream().collect(Collectors.groupingBy(PortalManagePageDO::getPlatform));
                    List<PortalManagePageDO> web = platFormMap.get("Web");
                    List<PortalManagePageDO> app = platFormMap.get("App");
                    if (web != null && web.size() > 0) {
                        PortalModel platForm = new PortalModel();
                        platForm.setId(systemModel.getId() + "1");
                        platForm.setParentId(systemModel.getId());
                        platForm.setFullName("WEB门户");
                        platForm.setIcon("icon-linzen icon-linzen-pc");
                        platForm.setSortCode(0L);
                        platForm.setCreatorTime(dateTime);
                        platFormId.put("Web", platForm.getId());
                        portalList.add(platForm);
                    }
                    if (app != null && app.size() > 0) {
                        PortalModel platForm = new PortalModel();
                        platForm.setId(systemModel.getId() + "2");
                        platForm.setParentId(systemModel.getId());
                        platForm.setFullName("APP门户");
                        platForm.setIcon("icon-linzen icon-linzen-mobile");
                        platForm.setSortCode(0L);
                        platForm.setCreatorTime(dateTime);
                        platFormId.put("App", platForm.getId());
                        portalList.add(platForm);
                    }
                    portalManagePageDOList.forEach(pageDO -> {
//                        if (!categoryList.contains(pageDO.getCategoryId())) {
//                            categoryList.add(pageDO.getCategoryId());
//                            PortalModel categoryModel = new PortalModel();
//                            categoryModel.setId(pageDO.getCategoryId());
//                            categoryModel.setParentId(platFormId.get(pageDO.getPlatform()));
//                            categoryModel.setFullName(pageDO.getCategoryName());
//                            categoryModel.setOnlyId(RandomUtil.uuId());
//                            list.add(categoryModel);
//                        }
                        PortalModel model = BeanUtil.toBean(pageDO, PortalModel.class);
//                        model.setParentId(pageDO.getCategoryId());
                        model.setParentId(platFormId.get(pageDO.getPlatform()));
                        model.setFullName(pageDO.getPortalName());
                        portalList.add(model);
                    });
                }
            });
        }
    }

    @Override
    @DSTransactional
    public void savePortalAuth(String permissionGroupId, List<String> portalIds) {
        final String AUTHORIZE_ROLE = "role";
        String userId = UserProvider.getLoginUserId();
        // 原始授权角色
        List<SysAuthorizeEntity> list = new ArrayList<>();
        for (int i = 0; i < portalIds.size(); i++) {
            SysAuthorizeEntity authorizeEntity = new SysAuthorizeEntity();
            authorizeEntity.setId(RandomUtil.uuId());
            authorizeEntity.setItemType(AuthorizeConst.AUTHORIZE_PORTAL_MANAGE);
            authorizeEntity.setItemId(portalIds.get(i));
            authorizeEntity.setObjectType(AUTHORIZE_ROLE);
            authorizeEntity.setObjectId(permissionGroupId);
            authorizeEntity.setSortCode((long) i);
            authorizeEntity.setCreatorTime(new Date());
            authorizeEntity.setCreatorUserId(userId);
            list.add(authorizeEntity);
        }
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, AuthorizeConst.AUTHORIZE_PORTAL_MANAGE);
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, permissionGroupId);
        this.remove(queryWrapper);
        list.forEach(this::save);
    }

    @Override
    public List<SysAuthorizeEntity> getAuthorizeByItem(String itemType, String itemId) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, itemType);
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemId, itemId);
        return this.list(queryWrapper);
    }

    @Override
    public AuthorizeVO getAuthorizeByUser(boolean singletonOrg) {
        AuthorizeVO authorizeModel = this.getAuthorize(false, singletonOrg);
        AuthorizeVO mainSystemAuthorize = new AuthorizeVO();
        // 分管的话，给菜单及下面的参数赋值进去
        List<String> moduleIds = organizeAdminIsTratorService.getOrganizeAdministratorEntity(UserProvider.getLoginUserId(), PermissionConst.MODULE, false)
                .stream().map(SysOrganizeAdministratorEntity::getOrganizeId).collect(Collectors.toList());
        List<SystemBaeModel> systemList = authorizeModel.getSystemList();
        List<ModuleModel> modelList = authorizeModel.getModuleList();
        List<ButtonModel> buttonList = authorizeModel.getButtonList();
        List<ColumnModel> columnList = authorizeModel.getColumnList();
        List<ModuleFormModel> formsList = authorizeModel.getFormsList();
        List<ResourceModel> resourceList = authorizeModel.getResourceList();
        List<String> moduleAuthorize = new ArrayList<>();
        List<String> moduleUrlAddressAuthorize = new ArrayList<>();
        if (configValueUtil.isMultiTenancy()) {
            TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
            moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
            moduleUrlAddressAuthorize = tenantAuthorizeModel.getUrlAddressList();
        }
        if (UserProvider.getUser().getIsAdministrator()) {
            mainSystemAuthorize = this.getMainSystemAuthorize(new ArrayList<>(), moduleAuthorize, moduleUrlAddressAuthorize, singletonOrg);
        } else if (moduleIds.size() > 0){
            mainSystemAuthorize = this.getMainSystemAuthorize(moduleIds, moduleAuthorize, moduleUrlAddressAuthorize, singletonOrg);
        } else {
            return new AuthorizeVO(modelList, buttonList, columnList, resourceList, formsList, systemList);
        }
        systemList.addAll(mainSystemAuthorize.getSystemList());
        modelList.addAll(mainSystemAuthorize.getModuleList());
        buttonList.addAll(mainSystemAuthorize.getButtonList());
        columnList.addAll(mainSystemAuthorize.getColumnList());
        resourceList.addAll(mainSystemAuthorize.getResourceList());
        formsList.addAll(mainSystemAuthorize.getFormsList());
        return new AuthorizeVO(modelList.stream().distinct().collect(Collectors.toList()),
                buttonList.stream().distinct().collect(Collectors.toList()),
                columnList.stream().distinct().collect(Collectors.toList()),
                resourceList.stream().distinct().collect(Collectors.toList()),
                formsList.stream().distinct().collect(Collectors.toList()),
                systemList.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public AuthorizeVO getMainSystemAuthorize(List<String> moduleIds, List<String> moduleAuthorize, List<String> moduleUrlAddressAuthorize, boolean singletonOrg) {
        SysSystemEntity systemEntity = systemService.getInfoByEnCode(LinzenConst.MAIN_SYSTEM_CODE);
        if (systemEntity == null || moduleAuthorize.contains(systemEntity.getId())) {
            return new AuthorizeVO();
        }
        SystemBaeModel systemBaeModel = BeanUtil.toBean(systemEntity, SystemBaeModel.class);
        List<ModuleEntity> moduleList = new ArrayList<>();
        if (moduleIds.size() > 0) {
            moduleList = moduleService.getModuleByIds(moduleIds, moduleAuthorize, moduleUrlAddressAuthorize, singletonOrg);
        } else {
            moduleList = moduleService.getMainModule(moduleAuthorize, moduleUrlAddressAuthorize, singletonOrg);
        }
        List<ModuleModel> moduleModelList = JsonUtil.createJsonToList(moduleList, ModuleModel.class);
        List<ModuleButtonEntity> buttonByModuleId = moduleButtonService.getListByModuleIds(moduleModelList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
        List<ButtonModel> buttonJsonToList = JsonUtil.createJsonToList(buttonByModuleId, ButtonModel.class);

        List<ModuleColumnEntity> columnByModuleId = moduleColumnService.getListByModuleId(moduleModelList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
        List<ColumnModel> columnJsonToList = JsonUtil.createJsonToList(columnByModuleId, ColumnModel.class);

        List<ModuleDataAuthorizeSchemeEntity> resourceByModuleId = dataAuthorizeSchemeApi.getListByModuleId(moduleModelList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
        List<ResourceModel> resourceJsonToList = JsonUtil.createJsonToList(resourceByModuleId, ResourceModel.class);

        List<ModuleFormEntity> formByModuleId = moduleFormService.getListByModuleId(moduleModelList.stream().map(ModuleModel::getId).collect(Collectors.toList()));
        List<ModuleFormModel> formJsonToList = JsonUtil.createJsonToList(formByModuleId, ModuleFormModel.class);

        return new AuthorizeVO(moduleModelList, buttonJsonToList, columnJsonToList, resourceJsonToList, formJsonToList, Collections.singletonList(systemBaeModel));
    }

    @Override
    public List<SysAuthorizeEntity> getListByRoleIdsAndItemType(List<String> roleIds, String itemType) {
        if (roleIds.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, itemType);
        queryWrapper.lambda().in(SysAuthorizeEntity::getObjectId, roleIds);
        return this.list(queryWrapper);
    }

    @Override
    public void save(String objectId, AuthorizeDataUpForm authorizeList) {
        SaveBatchForm form = BeanUtil.toBean(authorizeList, SaveBatchForm.class);
        form.setRoleIds(new String[]{objectId});
        this.saveBatch(form, false);
    }

    @Override
    public void saveBatch(SaveBatchForm saveBatchForm, boolean isBatch) {
        try {
            UserInfo userInfo = userProvider.get();

            List<SysAuthorizeEntity> objectList = new ArrayList<>();
            List<SysAuthorizeEntity> authorizeList = new ArrayList<>();
            // 设置权限归属对象
            setEntity(saveBatchForm.getUserIds(), AuthorizeConst.USER, objectList, true);
            setEntity(saveBatchForm.getPositionIds(), AuthorizeConst.POSITION, objectList, true);
            setEntity(saveBatchForm.getRoleIds(), AuthorizeConst.ROLE, objectList, true);
//            setEntity(saveBatchForm.getSystemIds(), AuthorizeConst.SYSTEM, objectList, true);
            // 设置权限模块
            setEntity(saveBatchForm.getButton(), AuthorizeConst.BUTTON, authorizeList, false);
            setEntity(saveBatchForm.getModule(), AuthorizeConst.MODULE, authorizeList, false);
            setEntity(saveBatchForm.getColumn(), AuthorizeConst.COLUMN, authorizeList, false);
            setEntity(saveBatchForm.getResource(), AuthorizeConst.RESOURCE, authorizeList, false);
            setEntity(saveBatchForm.getForm(), AuthorizeConst.FROM, authorizeList, false);
            setEntity(saveBatchForm.getSystemIds(), AuthorizeConst.SYSTEM, authorizeList, false);

            // 删除角色相关信息
            List<String> objectIdAll = objectList.stream().map(SysAuthorizeEntity::getObjectId).collect(Collectors.toList());
            userService.delCurRoleUser(objectIdAll);
            if (!isBatch) {
                String ids = String.join(",", objectIdAll);
                JdbcUtil.creUpDe(new PrepSqlDTO(XSSEscape.escapeEmpty(SqlFrameFastUtil.AUTHOR_DEL.replace("{authorizeIds}", ids))).withConn(dataSourceUtils, null));
            }

            // 插入数据
            String sql = DbTypeUtil.checkOracle(dataSourceUtils) || DbTypeUtil.checkPostgre(dataSourceUtils) ?
                    SqlFrameFastUtil.INSERT_AUTHORIZE2 : SqlFrameFastUtil.INSERT_AUTHORIZE;

            String column_key = StringUtil.EMPTY, column_plceholder = StringUtil.EMPTY, column_value = TenantDataSourceUtil.getTenantColumn();
            if (StringUtil.isNotEmpty(column_value)) {
                column_key = StrPool.COMMA + configValueUtil.getMultiTenantColumn();
                column_plceholder = ",?";
            }
            sql = sql.replace("%COLUMN_KEY%", column_key).replace("%COLUMN_PLACEHOLDER%", column_plceholder);
            PrepSqlDTO dto = new PrepSqlDTO(sql).withConn(dataSourceUtils, null);
            for (int i = 0; i < objectList.size(); i++) {
                for (SysAuthorizeEntity entityItem : authorizeList) {
                    List<Object> data = new LinkedList<>();
                    data.add(RandomUtil.uuId());
                    data.add(entityItem.getItemType());
                    data.add(entityItem.getItemId());
                    data.add(objectList.get(i).getObjectType());
                    data.add(objectList.get(i).getObjectId());
                    data.add(i);
                    data.add(DateUtil.getNow());
                    data.add(userInfo.getUserId());
                    if (StringUtil.isNotEmpty(column_value)) {
                        data.add(column_value);
                    }
                    dto.addMultiData(data);
                }
            }
            JdbcUtil.creUpDeBatchOneSql(dto);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("权限报错:" + e.getMessage());
        }
    }

    /**
     * 权限
     */
    private void setEntity(String[] ids, String type, List<SysAuthorizeEntity> entityList, Boolean objectFlag) {
        if (ids != null) {
            for (String id : ids) {
                SysAuthorizeEntity entity = new SysAuthorizeEntity();
                if (objectFlag) {
                    entity.setObjectType(type);
                    entity.setObjectId(id);
                } else {
                    entity.setItemType(type);
                    entity.setItemId(id);
                }
                entityList.add(entity);
            }
        }
    }

    @Override
    public List<SysAuthorizeEntity> getListByUserId(boolean isAdmin, String userId) {
        if (!isAdmin) {
            QueryWrapper<SysUserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysUserRelationEntity::getUserId, userId);
            List<SysUserRelationEntity> list = userRelationService.list(queryWrapper);
            List<String> userRelationList = list.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
            userRelationList.add(userId);
            QueryWrapper<SysAuthorizeEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(SysAuthorizeEntity::getObjectId, userRelationList);
            return this.list(wrapper);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SysAuthorizeEntity> getListByObjectId(List<String> objectId) {
        if (objectId.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SysAuthorizeEntity::getObjectId, objectId);
        return this.list(queryWrapper);
    }

    @Override
    public Boolean existAuthorize(String roleId, String systemId) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, roleId);
        if (StringUtil.isNotEmpty(systemId)) {
            queryWrapper.lambda().eq(SysAuthorizeEntity::getItemId, systemId);
            queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, AuthorizeConst.SYSTEM);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<SysAuthorizeEntity> getListByRoleId(String roleId) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, roleId);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysAuthorizeEntity> getListByObjectId(String objectId, String itemType) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectId, objectId);
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, itemType);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysAuthorizeEntity> getListByObjectAndItem(String itemId, String objectType) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getObjectType, objectType).eq(SysAuthorizeEntity::getItemId, itemId);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysAuthorizeEntity> getListByObjectAndItemIdAndType(String itemId, String itemType) {
        QueryWrapper<SysAuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAuthorizeEntity::getItemType, itemType).eq(SysAuthorizeEntity::getItemId, itemId);
        return this.list(queryWrapper);
    }

    /**
     * 获取条件过滤
     *
     * @param conditionModel 数据权限条件模型
     * @return
     */
    @Override
    @DS("" )
    public <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel) {
        QueryWrapper<T> queryWhere = conditionModel.getObj();
        String moduleId = conditionModel.getModuleId();
        String tableName = conditionModel.getTableName();
        UserInfo userInfo = userProvider.get();
        AuthorizeVO model = this.getAuthorize(true, false);
        List<ResourceModel> resourceList = model.getResourceList().stream().filter(m -> m.getModuleId().equals(moduleId)).collect(Collectors.toList());
        if (resourceList.size() == 0) {
            return null;
        }
        List<ResourceModel> resourceList1 = new ArrayList<>();
        //拼接计数
        int t = 1;
        for (ResourceModel item : resourceList) {
            if (item.getAllData() != null && item.getAllData() == 1) {
                t = 0;
                break;
            }
            List<ConditionModel> conditionModelList = JsonUtil.createJsonToList(item.getConditionJson(), ConditionModel.class);
            for (int i = 0; i < conditionModelList.size(); i++) {
                ConditionModel conditionItem = conditionModelList.get(i);
                for (int k = 0; k < conditionItem.getGroups().size(); k++) {
                    ConditionModel.ConditionItemModel fieldItem = conditionItem.getGroups().get(k);
                    String itemTable = fieldItem.getBindTable();
                    if (StringUtil.isNotEmpty(itemTable) && itemTable.equalsIgnoreCase(tableName)) {
                        resourceList1.add(item);
                    }
                }
            }
        }
        if (t == 1) {
            if (resourceList1.size() > 0) {
                queryWhere.and(tw -> {
                    for (ResourceModel item : resourceList1) {
                        List<ConditionModel> conditionModelList = JsonUtil.createJsonToList(item.getConditionJson(), ConditionModel.class);
                        for (int i = 0; i < conditionModelList.size(); i++) {
                            ConditionModel conditionItem = conditionModelList.get(i);
                            for (int k = 0; k < conditionItem.getGroups().size(); k++) {
                                ConditionModel.ConditionItemModel fieldItem = conditionItem.getGroups().get(k);
                                String itemField = fieldItem.getField();
                                String itemValue = fieldItem.getValue();
                                String itemMethod = fieldItem.getOp();
                                Object value;
                                if (AuthorizeConditionEnum.USER.getCondition().equals(itemValue)) { //当前用户
                                    value = userInfo.getUserId();
                                    //任意文本 当前用户 当前组织 包含为模糊查询
                                    if (itemMethod.equals(SearchMethodEnum.Included.getMessage())) {
                                        itemMethod = SearchMethodEnum.Like.getMessage();
                                    }
                                    if (itemMethod.equals(SearchMethodEnum.NotIncluded.getMessage())) {
                                        itemMethod = SearchMethodEnum.NotLike.getMessage();
                                    }
                                } else if (AuthorizeConditionEnum.ORGANIZE.getCondition().equals(itemValue)) { //当前组织
                                    String orgId = userInfo.getOrganizeId();
                                    if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                                        orgId = userInfo.getDepartmentId();
                                    }
                                    if (itemMethod.equals(SearchMethodEnum.Included.getMessage())) {
                                        itemMethod = SearchMethodEnum.Like.getMessage();
                                    }
                                    if (itemMethod.equals(SearchMethodEnum.NotIncluded.getMessage())) {
                                        itemMethod = SearchMethodEnum.NotLike.getMessage();
                                    }
                                    value = orgId;
                                } else if (AuthorizeConditionEnum.ORGANIZEANDUNDER.getCondition().equals(itemValue)) { //组织及子组织
                                    String orgId = userInfo.getOrganizeId();
                                    if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                                        orgId = userInfo.getDepartmentId();
                                    }
                                    List<String> underOrganizations = organizeService.getUnderOrganizations(orgId, false);
                                    underOrganizations.add(orgId);
                                    value = underOrganizations;
                                } else if (AuthorizeConditionEnum.USERANDUNDER.getCondition().equals(itemValue)) { //用户及用户下属
                                    List<String> idsList = new ArrayList<>();
                                    if (userInfo.getSubordinateIds().size() > 0) {
                                        idsList = userInfo.getSubordinateIds();
                                    }
                                    idsList.add(userInfo.getUserId());
                                    value = idsList;
                                } else if (AuthorizeConditionEnum.BRANCHMANAGEORG.getCondition().equals(itemValue)) { //分管组织
                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userInfo.getUserId());
                                    //子
                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity1 = new ArrayList<>(organizeAdministratorEntity);
                                    //父
                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity2 = new ArrayList<>(organizeAdministratorEntity);
                                    List<String> allIdList = new ArrayList<>();
                                    //子
                                    List<String> childList = organizeAdministratorEntity1.stream().filter(orgAdmin -> orgAdmin.getSubLayerSelect() == 1).map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());
                                    //父
                                    List<String> fathetList = organizeAdministratorEntity2.stream().filter(orgAdmin -> orgAdmin.getThisLayerSelect() == 1).map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());
                                    for (String org : childList) {
                                        List<String> underOrganizations = organizeService.getUnderOrganizations(org, false);
                                        if (underOrganizations.size() > 0) {
                                            allIdList.addAll(underOrganizations);
                                        }
                                    }
                                    if (fathetList.size() > 0) {
                                        allIdList.addAll(fathetList);
                                    }
                                    //空集合处理
                                    if (allIdList.size() == 0) {
                                        allIdList.add("linzenNullList" );
                                    }
                                    value = allIdList;
                                } else if (AuthorizeConditionEnum.BRANCHMANAGEORGANIZEUNDER.getCondition().equals(itemValue)) { //分管组织及子组织
                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userInfo.getUserId());

                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity1 = new ArrayList<>(organizeAdministratorEntity);

                                    List<SysOrganizeAdministratorEntity> organizeAdministratorEntity2 = new ArrayList<>(organizeAdministratorEntity);

                                    List<String> allIdList = new ArrayList<>();
                                    //需要子集
                                    List<String> childList = new ArrayList<>();

                                    List<String> thisList = organizeAdministratorEntity1.stream().filter(orgAdmin -> orgAdmin.getThisLayerSelect() == 1)
                                            .map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());

                                    List<String> subList = organizeAdministratorEntity2.stream().filter(orgAdmin -> orgAdmin.getSubLayerSelect() == 1)
                                            .map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());

                                    if (thisList.size() > 0) {
                                        allIdList.addAll(thisList);
                                        childList.addAll(thisList);
                                    }
                                    if (subList.size() > 0) {
                                        childList.addAll(subList);
                                    }

                                    for (String orgID : childList) {
                                        List<String> underOrganizations = organizeService.getUnderOrganizations(orgID, false);
                                        if (underOrganizations.size() > 0) {
                                            allIdList.addAll(underOrganizations);
                                        }
                                    }
                                    if (allIdList.size() == 0) {
                                        allIdList.add("linzenNullList" );
                                    }
                                    value = allIdList;
                                } else {//任意文本
                                    value = itemValue;
                                    if (itemMethod.equals(SearchMethodEnum.Included.getMessage())) {
                                        itemMethod = SearchMethodEnum.Like.getMessage();
                                    }
                                    if (itemMethod.equals(SearchMethodEnum.NotIncluded.getMessage())) {
                                        itemMethod = SearchMethodEnum.NotLike.getMessage();
                                    }
                                }
                                if ("and".equalsIgnoreCase(conditionItem.getLogic())) {
                                    if (itemMethod.equals(SearchMethodEnum.Equal.getMessage())) {
                                        tw.eq(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())) {
                                        tw.ne(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThan.getMessage())) {
                                        tw.lt(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())) {
                                        tw.le(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())) {
                                        tw.gt(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())) {
                                        tw.ge(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.Included.getMessage())) {
                                        tw.in(itemField, (List) value);
                                    } else if (itemMethod.equals(SearchMethodEnum.NotIncluded.getMessage())) {
                                        tw.notIn(itemField, (List) value);
                                    } else if (itemMethod.equals(SearchMethodEnum.Like.getMessage())) {
                                        tw.like(itemField, value);
                                    } else if (itemMethod.equals(SearchMethodEnum.NotLike.getMessage())) {
                                        tw.notLike(itemField, value);
                                    }
                                } else {
                                    if (itemMethod.equals(SearchMethodEnum.Equal.getMessage())) {
                                        tw.or(
                                                qw -> qw.eq(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.ne(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThan.getMessage())) {
                                        tw.or(
                                                qw -> qw.lt(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.le(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())) {
                                        tw.or(
                                                qw -> qw.gt(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.ge(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.Included.getMessage())) {
                                        tw.or(
                                                qw -> qw.in(itemField, (List) value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.NotIncluded.getMessage())) {
                                        tw.or(
                                                qw -> qw.notIn(itemField, (List) value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.Like.getMessage())) {
                                        tw.or(
                                                qw -> qw.like(itemField, value)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.NotLike.getMessage())) {
                                        tw.or(
                                                qw -> qw.notLike(itemField, value)
                                        );
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        return queryWhere;
    }


    @Override
    @DS("")
    public List<SuperJsonModel> getConditionSql(String moduleId) {
        List<SuperJsonModel> list = new ArrayList<>();
        UserInfo userInfo = userProvider.get();
        String reidsKey = cacheKeyUtil.getUserAuthorize() + moduleId + "_" + userInfo.getUserId();
        long time = 60 * 5;
        if (redisUtil.exists(reidsKey)) {
            return JsonUtil.createJsonToList(redisUtil.getString(reidsKey).toString(), SuperJsonModel.class);
        }

        AuthorizeVO model = this.getAuthorizeByUser(false);
        if(model == null){
            redisUtil.insert(reidsKey, JsonUtil.createObjectToString(new ArrayList<>()), time);
            return new ArrayList<>();
        }
        List<ResourceModel> resourceListAll = model.getResourceList().stream().filter(m -> m.getModuleId().equals(moduleId)).collect(Collectors.toList());
        //先遍历一次 查找其中有没有全部方案
        boolean isAll = resourceListAll.stream().filter(item -> DbConst.LINZEN_ALLDATA.equals(item.getEnCode())).count() > 0;
        //未分配权限方案
        if (isAll || userInfo.getIsAdministrator()) {
            SuperJsonModel superJsonModel = new SuperJsonModel();
            list.add(superJsonModel);
            redisUtil.insert(reidsKey, JsonUtil.createObjectToString(list), time);
            return list;
        }
        Map<String, List<ResourceModel>> authorizeMap = resourceListAll.stream().filter(t->StringUtil.isNotEmpty(t.getObjectId())).collect(Collectors.groupingBy(ma -> ma.getObjectId()));
        int num = 0;
        //方案
        for (String key : authorizeMap.keySet()) {
            List<ResourceModel> resourceList = authorizeMap.get(key);
            boolean authorizeLogic = num == 0;
            for (ResourceModel item : resourceList) {
                String matchLogic = StringUtil.isNotEmpty(item.getMatchLogic()) ? item.getMatchLogic() : SearchMethodEnum.And.getSymbol();
                List<SuperQueryJsonModel> conditionList = new ArrayList<>();
                List<ConditionModel> conditionModelList = JsonUtil.createJsonToList(item.getConditionJson(), ConditionModel.class);
                //分组
                for (ConditionModel conditionModel : conditionModelList) {
                    String logic = conditionModel.getLogic();
                    List<FieLdsModel> groupList = new ArrayList<>();
                    //条件
                    for (ConditionModel.ConditionItemModel fieldItem : conditionModel.getGroups()) {
                        //当前用户
                        String itemValue = fieldItem.getValue();
                        SearchMethodEnum itemMethod = SearchMethodEnum.getSearchMethod(fieldItem.getOp());
                        String itemField = fieldItem.getField();
                        String table = fieldItem.getBindTable();
                        if (itemField.contains(".") && itemField.split("\\.").length == 2) {
                            table = itemField.split("\\.")[0];
                            itemField = itemField.split("\\.")[1];
                        }
                        String bindTable = table;
                        String vModel = itemField;
                        if (AuthorizeConditionEnum.USER.getCondition().equals(itemValue)) {
                            itemValue = userInfo.getUserId();
                        }
                        //当前组织
                        if (AuthorizeConditionEnum.ORGANIZE.getCondition().equals(itemValue)) {
                            List<List<String>> orgAllPathList = getOrgAllPathList(new ArrayList() {{
                                add(userInfo.getOrganizeId());
                            }});
                            if(CollectionUtils.isNotEmpty(orgAllPathList)){
                                itemValue = JsonUtil.createObjectToString(orgAllPathList.get(0));
                            }else{
                                itemValue = "";
                            }
                        }
                        //当前组织及子组织
                        if (AuthorizeConditionEnum.ORGANIZEANDUNDER.getCondition().equals(itemValue)) {
                            String orgId = userInfo.getOrganizeId();
                            if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                                orgId = userInfo.getDepartmentId();
                            }
                            List<String> underOrganizations = organizeService.getUnderOrganizations(orgId, false);
                            underOrganizations.add(orgId);
                            itemValue = getOrgAllPath(underOrganizations);
                        }
                        //当前用户及下属
                        if (AuthorizeConditionEnum.USERANDUNDER.getCondition().equals(itemValue)) {
                            List<String> subOrganizeIds = new ArrayList<>();
                            if (userInfo.getSubordinateIds().size() > 0) {
                                subOrganizeIds = userInfo.getSubordinateIds();
                            }
                            subOrganizeIds.add(userInfo.getUserId());
                            itemValue = JsonUtil.createObjectToString(subOrganizeIds);
                        }
                        //分管组织
                        if (AuthorizeConditionEnum.BRANCHMANAGEORG.getCondition().equals(itemValue)) {
                            List<String> allIdList = organizeAdminIsTratorService.getOrganizeUserList(LinzenConst.CURRENT_ORG_SUB);
                            itemValue = getOrgAllPath(allIdList);
                        }
                        FieLdsModel fieLdsModel = new FieLdsModel();
                        ConfigModel config = new ConfigModel();
                        config.setProjectKey(fieldItem.getConditionText());
                        config.setTableName(bindTable);
                        fieLdsModel.setConfig(config);
                        fieLdsModel.setSymbol(itemMethod.getSymbol());
                        fieLdsModel.setVModel(vModel);
                        fieLdsModel.setId(vModel);
                        fieLdsModel.setFieldValue(itemValue);
                        groupList.add(fieLdsModel);
                    }
                    //搜索条件
                    SuperQueryJsonModel queryJsonModel = new SuperQueryJsonModel();
                    queryJsonModel.setGroups(groupList);
                    queryJsonModel.setLogic(logic);
                    conditionList.add(queryJsonModel);
                }
                if (conditionList.size() > 0) {
                    SuperJsonModel superJsonModel = new SuperJsonModel();
                    superJsonModel.setMatchLogic(matchLogic);
                    superJsonModel.setConditionList(conditionList);
                    superJsonModel.setAuthorizeLogic(authorizeLogic);
                    list.add(superJsonModel);
                }
            }
            num += list.size() > 0 ? 1 : 0;
        }
        redisUtil.insert(reidsKey, JsonUtil.createObjectToString(list), time);
        return list;
    }

    /**
     * 获取组织全路径
     * @param allIdList  组织id列表。
     * @return
     */
    private String getOrgAllPath(List<String> allIdList) {
        List<List<String>> orgAllPathList = getOrgAllPathList(allIdList);
        return JsonUtil.createObjectToString(orgAllPathList);
    }

    /**
     * 获取组织全路径
     * @param allIdList  组织id列表。
     * @return
     */
    private List<List<String>> getOrgAllPathList(List<String> allIdList) {
        List<List<String>> resOrg=new ArrayList<>();
        for(String itemOrg: allIdList){
            SysOrganizeEntity organizeEntity =organizeService.getInfo(itemOrg);
            if (organizeEntity != null) {
                if (StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree())) {
                    String[] split = organizeEntity.getOrganizeIdTree().split(",");
                    if(split.length > 0){
                        resOrg.add(Arrays.asList(split));
                    }
                }
            }
        }
        return resOrg;
    }

}
