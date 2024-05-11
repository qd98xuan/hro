package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.annotation.OrganizeAdminIsTrator;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.*;
import com.linzen.base.model.base.SystemBaeModel;
import com.linzen.base.model.button.ButtonModel;
import com.linzen.base.model.column.ColumnModel;
import com.linzen.base.model.form.ModuleFormModel;
import com.linzen.base.model.module.ModuleModel;
import com.linzen.base.model.portalManage.PortalListVO;
import com.linzen.base.model.portalManage.PortalModel;
import com.linzen.base.model.portalManage.PortalVO;
import com.linzen.base.model.portalManage.SavePortalAuthModel;
import com.linzen.base.model.resource.ResourceModel;
import com.linzen.base.service.*;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.model.tenant.TenantAuthorizeModel;
import com.linzen.permission.constant.AuthorizeConst;
import com.linzen.permission.entity.SysAuthorizeEntity;
import com.linzen.permission.entity.SysColumnsPurviewEntity;
import com.linzen.permission.model.authorize.*;
import com.linzen.permission.model.columnspurview.ColumnsPurviewUpForm;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.ColumnsPurviewService;
import com.linzen.permission.service.PermissionGroupService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "操作权限", description = "Authorize")
@RestController
@RequestMapping("/api/permission/Authority")
public class AuthorizeController extends SuperController<AuthorizeService, SysAuthorizeEntity> {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ModuleButtonService moduleButtonService;
    @Autowired
    private ModuleColumnService moduleColumnService;
    @Autowired
    private ModuleFormService moduleFormService;
    @Autowired
    private ModuleDataAuthorizeSchemeService moduleDataAuthorizeSchemeService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private ColumnsPurviewService columnsPurviewService;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ConfigValueUtil configValueUtil;


    /**
     * 权限数据
     *
     * @param objectId        对象主键
     * @param dataValuesQuery 权限值
     * @return
     */
    @Operation(summary = "获取岗位/角色/用户权限树形结构")
    @Parameters({@Parameter(name = "objectId", description = "对象主键", required = true), @Parameter(name = "dataValuesQuery", description = "权限值", required = true)})
    @SaCheckPermission(value = {"permission.authorize", "permission.role"}, mode = SaMode.OR)
    @PostMapping("/Data/{objectId}/Values")
    public ServiceResult<AuthorizeDataReturnVO> getValuesData(@PathVariable("objectId") String objectId, @RequestBody DataValuesQuery dataValuesQuery) {
        AuthorizeVO authorizeModel = authorizeService.getAuthorize(false, false);
        List<SysAuthorizeEntity> list = authorizeService.list(new QueryWrapper<SysAuthorizeEntity>().lambda().eq(SysAuthorizeEntity::getObjectId, objectId));
        if (!StringUtil.isEmpty(dataValuesQuery.getType())) {
            switch (dataValuesQuery.getType()) {
                case "system":
                    AuthorizeDataReturnVO authorizeDataReturnVO = this.system(list, authorizeModel);
                    return ServiceResult.success(authorizeDataReturnVO);
                case "module":
                    List<String> systemId = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        systemId = Arrays.asList(dataValuesQuery.getModuleIds().split(","));
                    }
                    List<ModuleEntity> moduleList = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
                    AuthorizeDataReturnVO dataReturnVO = this.module1(moduleList, list, authorizeModel, systemId);
                    return ServiceResult.success(dataReturnVO);
                case "button":
                    List<ModuleEntity> moduleList1 = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList1 = new ArrayList<>();
                    List<SysSystemEntity> selectorSystemList = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        List<String> moduleId1 = Arrays.asList(dataValuesQuery.getModuleIds().split(","));
                        selectList1 = moduleList1.stream().filter(t -> moduleId1.contains(t.getId())).collect(Collectors.toList());
                        selectorSystemList = systemService.getListByIds(moduleId1, null);
                    }
                    AuthorizeDataReturnVO dataReturnVo1 = this.moduleButton(selectList1, selectorSystemList, list, authorizeModel);
                    return ServiceResult.success(dataReturnVo1);

                case "column":
                    List<ModuleEntity> moduleList2 = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList2 = new ArrayList<>();
                    List<SysSystemEntity> selectorSystemList1 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        List<String> moduleId2 = Arrays.asList(dataValuesQuery.getModuleIds().split(","));
                        selectList2 = moduleList2.stream().filter(t -> moduleId2.contains(t.getId())).collect(Collectors.toList());
                        selectorSystemList1 = systemService.getListByIds(moduleId2, null);
                    }
                    AuthorizeDataReturnVO dataReturnVo2 = this.moduleColumn(selectList2, selectorSystemList1, list, authorizeModel);
                    return ServiceResult.success(dataReturnVo2);

                case "resource":
                    List<ModuleEntity> moduleList3 = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList3 = new ArrayList<>();
                    List<SysSystemEntity> selectorSystemList2 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        List<String> moduleId3 = Arrays.asList(dataValuesQuery.getModuleIds().split(","));
                        selectList3 = moduleList3.stream().filter(t -> moduleId3.contains(t.getId())).collect(Collectors.toList());
                        selectorSystemList2 = systemService.getListByIds(moduleId3, null);
                    }
                    AuthorizeDataReturnVO dataReturnVo3 = this.resourceData(selectList3, selectorSystemList2, list, authorizeModel);
                    return ServiceResult.success(dataReturnVo3);

                case "form":
                    List<ModuleEntity> moduleList4 = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList4 = new ArrayList<>();
                    List<SysSystemEntity> selectorSystemList3 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        List<String> moduleId4 = Arrays.asList(dataValuesQuery.getModuleIds().split(","));
                        selectList4 = moduleList4.stream().filter(t -> moduleId4.contains(t.getId())).collect(Collectors.toList());
                        selectorSystemList3 = systemService.getListByIds(moduleId4, null);
                    }
                    AuthorizeDataReturnVO dataReturnVo4 = this.moduleForm(selectList4, selectorSystemList3, list, authorizeModel);
                    return ServiceResult.success(dataReturnVo4);

                default:
            }
        }
        return ServiceResult.error("类型不能为空");
    }

    /**
     * 获取门户权限
     *
     * @return
     */
    @Operation(summary = "获取门户权限")
    @Parameters({@Parameter(name = "id", description = "对象主键", required = true)})
    @SaCheckPermission(value = {"permission.authorize", "permission.role", "onlineDev.visualPortal"}, mode = SaMode.OR)
    @GetMapping("/Portal/{id}")
    public ServiceResult<PortalVO> getPortalAuth(@PathVariable("id") String id) {
        PortalVO vo = new PortalVO();
        List<PortalModel> myPortalList = new ArrayList<>();
        List<PortalModel> permissionGroupPortalList = new ArrayList<>();
        // 权限组权限
        List<SysAuthorizeEntity> permissionGroupAuthorize = authorizeService.getListByRoleId(id);
        List<String> moduleAuthorize = new ArrayList<>();
        if (configValueUtil.isMultiTenancy()) {
            TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
            moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
        }
        List<SysSystemEntity> permissionGroupSystemList = systemService.getListByIds(permissionGroupAuthorize.stream().filter(t -> AuthorizeConst.SYSTEM.equals(t.getItemType())).map(SysAuthorizeEntity::getItemId).collect(Collectors.toList()), moduleAuthorize);
        AuthorizeVO authorize = authorizeService.getAuthorize(false, false);
        List<String> permissionGroupSystemIdList = permissionGroupSystemList.stream().map(SysSystemEntity::getId).collect(Collectors.toList());
        List<SysSystemEntity> mySystemList = JsonUtil.createJsonToList(authorize.getSystemList(), SysSystemEntity.class);
        // 我的数据
        mySystemList = mySystemList.stream().filter(t -> permissionGroupSystemIdList.contains(t.getId())).collect(Collectors.toList());
        long dateTime = System.currentTimeMillis();
        authorizeService.getPortal(mySystemList, myPortalList, dateTime, new ArrayList<>());
        // 权限组数据
        authorizeService.getPortal(permissionGroupSystemList, permissionGroupPortalList, dateTime, new ArrayList<>());

        // 验证他有我没有的
        List<String> noContainsList = permissionGroupPortalList.stream().filter(t -> !myPortalList.contains(t)).map(PortalModel::getId).collect(Collectors.toList());

        myPortalList.addAll(permissionGroupPortalList);
        myPortalList.forEach(t -> {
            if (noContainsList.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        List<String> allId = myPortalList.stream().distinct().map(PortalModel::getId).collect(Collectors.toList());
        List<SumTree<PortalModel>> trees = TreeDotUtils.convertListToTreeDot(myPortalList.stream().sorted(Comparator.comparing(PortalModel::getSortCode).thenComparing(PortalModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList()));
        vo.setList(JsonUtil.createJsonToList(trees, PortalListVO.class));
        vo.setAll(allId);
        List<String> collect = permissionGroupAuthorize.stream().filter(t -> AuthorizeConst.AUTHORIZE_PORTAL_MANAGE.equals(t.getItemType())).map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        vo.setIds(permissionGroupPortalList.stream().filter(t -> collect.contains(t.getId())).map(PortalModel::getId).collect(Collectors.toList()));
        return ServiceResult.success(vo);
    }

    /**
     * 保存门户权限
     *
     * @return
     */
    @Operation(summary = "保存门户权限")
    @Parameters({@Parameter(name = "itemId", description = "对象主键", required = true)})
    @SaCheckPermission(value = {"permission.authorize", "permission.role", "onlineDev.visualPortal"}, mode = SaMode.OR)
    @PostMapping("/Portal/{id}")
    public ServiceResult<String> savePortalAuth(@PathVariable("id") String id, @RequestBody SavePortalAuthModel model) {
        authorizeService.savePortalAuth(id, model.getIds());
        return ServiceResult.success(MsgCode.SU005.get());
    }


    /**
     * 对象数据
     *
     * @return
     */
    @Operation(summary = "获取功能权限数据")
    @Parameters({@Parameter(name = "itemId", description = "对象主键", required = true), @Parameter(name = "objectType", description = "对象类型", required = true)})
    @SaCheckPermission(value = {"permission.authorize", "permission.role", "onlineDev.visualPortal"}, mode = SaMode.OR)
    @GetMapping("/Model/{itemId}/{objectType}")
    public ServiceResult<AuthorizeItemObjIdsVO> getObjectAuth(@PathVariable("itemId") String itemId, @PathVariable("objectType") String objectType) {
        List<SysAuthorizeEntity> authorizeList = authorizeService.getListByObjectAndItem(itemId, objectType);
        List<String> ids = authorizeList.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
        AuthorizeItemObjIdsVO vo = new AuthorizeItemObjIdsVO();
        vo.setIds(ids);
        return ServiceResult.success(vo);
    }

    @Operation(summary = "门户管理授权")
    @Parameters({@Parameter(name = "itemId", description = "对象主键", required = true), @Parameter(name = "saveAuthForm", description = "保存权限模型", required = true)})
    @PutMapping("/Model/{portalManageId}")
    @SaCheckPermission(value = {"permission.authorize", "permission.role"}, mode = SaMode.OR)
    public ServiceResult<String> savePortalManage(@PathVariable("portalManageId") String portalManageId, @RequestBody SaveAuthForm saveAuthForm) {
        authorizeService.savePortalManage(portalManageId, saveAuthForm);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 保存
     *
     * @param objectId            对象主键
     * @param authorizeDataUpForm 修改权限模型
     * @return
     */
    @OrganizeAdminIsTrator
    @Operation(summary = "保存权限")
    @Parameters({@Parameter(name = "objectId", description = "对象主键", required = true), @Parameter(name = "authorizeDataUpForm", description = "修改权限模型", required = true)})
    @SaCheckPermission(value = {"permission.authorize", "permission.role"}, mode = SaMode.OR)
    @PutMapping("/Data/{objectId}")
    public ServiceResult save(@PathVariable("objectId") String objectId, @RequestBody AuthorizeDataUpForm authorizeDataUpForm) {
        authorizeService.save(objectId, authorizeDataUpForm);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 保存批量
     *
     * @param saveBatchForm 批量保存模型
     * @return
     */
    @OrganizeAdminIsTrator
    @Operation(summary = "批量保存权限")
    @Parameters({@Parameter(name = "saveBatchForm", description = "批量保存模型", required = true)})
    @SaCheckPermission(value = {"permission.authorize"}, mode = SaMode.OR)
    @PostMapping("/Data/Batch")
    public ServiceResult saveBatch(@RequestBody SaveBatchForm saveBatchForm) {
        authorizeService.saveBatch(saveBatchForm, true);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 获取模块列表展示字段
     *
     * @param moduleId 菜单Id
     * @return
     */
    @Operation(summary = "获取模块列表展示字段")
    @Parameters({@Parameter(name = "moduleId", description = "菜单id", required = true)})
    @GetMapping("/GetColumnsByModuleId/{moduleId}")
    public ServiceResult getColumnsByModuleId(@PathVariable("moduleId") String moduleId) {
        SysColumnsPurviewEntity entity = columnsPurviewService.getInfo(moduleId);
        List<Map<String, Object>> jsonToListMap = null;
        if (entity != null) {
            jsonToListMap = JsonUtil.createJsonToListMap(entity.getFieldList());
        }
        return ServiceResult.success(jsonToListMap != null ? jsonToListMap : new ArrayList<>(16));
    }

    /**
     * 配置模块列表展示字段
     *
     * @param columnsPurviewUpForm 修改模型
     * @return
     */
    @Operation(summary = "配置模块列表展示字段")
    @Parameters({@Parameter(name = "columnsPurviewUpForm", description = "修改模型", required = true)})
    @PutMapping("/SetColumnsByModuleId")
    public ServiceResult setColumnsByModuleId(@RequestBody ColumnsPurviewUpForm columnsPurviewUpForm) {
        SysColumnsPurviewEntity entity = BeanUtil.toBean(columnsPurviewUpForm, SysColumnsPurviewEntity.class);
        columnsPurviewService.update(columnsPurviewUpForm.getModuleId(), entity);
        return ServiceResult.success(MsgCode.SU005.get());
    }

    /**
     * 功能权限
     *
     * @param authorizeList 已有权限
     * @return
     */
    private AuthorizeDataReturnVO system(List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<SystemBaeModel> systemList = authorizeModel.getSystemList();
        // 哪些是系统的
        List<SysAuthorizeEntity> collect = authorizeList.stream().filter(t -> AuthorizeConst.SYSTEM.equals(t.getItemType())).collect(Collectors.toList());
        List<SysSystemEntity> systemEntityList = systemService.getListByIds(collect.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList()), null);
        List<AuthorizeDataReturnModel> authorizeModelList = JsonUtil.createJsonToList(systemEntityList, AuthorizeDataReturnModel.class);
        List<AuthorizeDataReturnModel> jsonToList = JsonUtil.createJsonToList(systemList, AuthorizeDataReturnModel.class);
        // 取交集并集处理
        List<AuthorizeDataReturnModel> containsList = authorizeModelList.stream().filter(t -> !jsonToList.contains(t)).collect(Collectors.toList());
        List<String> collect1 = containsList.stream().map(AuthorizeDataReturnModel::getId).collect(Collectors.toList());
        collect1.addAll(systemEntityList.stream().map(SysSystemEntity::getId).collect(Collectors.toList()));
        containsList.forEach(t -> t.setDisabled(true));
        jsonToList.addAll(containsList);
        vo.setList(jsonToList.stream().sorted(Comparator.comparing(AuthorizeDataReturnModel::getSortCode).thenComparing(AuthorizeDataReturnModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList()));
        vo.setAll(jsonToList.stream().map(AuthorizeDataReturnModel::getId).collect(Collectors.toList()));
        vo.setIds(collect1.stream().distinct().collect(Collectors.toList()));
        return vo;
    }


    /**
     * 功能权限
     *
     * @param moduleListAll  所有功能
     * @param authorizeList  已有权限
     * @param authorizeModel 权限集合
     * @param systemId       系统id
     * @return
     */
    private AuthorizeDataReturnVO module1(List<ModuleEntity> moduleListAll, List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel, List<String> systemId) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<ModuleModel> moduleList = new ArrayList<>();
        // 权限组本身拥有的菜单
        List<SysAuthorizeEntity> authorizeLists = authorizeList.stream().filter(t -> AuthorizeConst.MODULE.equals(t.getItemType())).collect(Collectors.toList());
        List<ModuleEntity> moduleByIds = moduleService.getModuleByIds(authorizeLists.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList()), null, null, false).stream().filter(t -> systemId.contains(t.getSystemId())).collect(Collectors.toList());
        List<ModuleModel> jsonToList = JsonUtil.createJsonToList(moduleByIds, ModuleModel.class);
        // 我的菜单
        List<ModuleModel> moduleList1 = authorizeModel.getModuleList();
        moduleList1 = moduleList1.stream().filter(t -> systemId.contains(t.getSystemId())).collect(Collectors.toList());
        List<String> collect = moduleList1.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<ModuleModel> collect1 = jsonToList.stream().filter(t -> collect.contains(t.getId())).collect(Collectors.toList());

        List<String> containsList = collect1.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<String> collect3 = authorizeList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        containsList.addAll(collect3);
        containsList.addAll(systemId);
        moduleList.addAll(jsonToList);
        moduleList.addAll(moduleList1);
        moduleList = moduleList.stream().filter(t -> systemId.contains(t.getSystemId())).distinct().collect(Collectors.toList());

        List<ModuleModel> list = new ArrayList<>(moduleList);
        // 存放上级菜单id及上级 systemId,id
        Map<String, String> appIds = new HashMap<>(16);
        Map<String, String> webIds = new HashMap<>(16);
        long datetime = System.currentTimeMillis();
        moduleList.stream().sorted(Comparator.comparing(ModuleModel::getCategory).reversed()).forEach(t -> {
            if ("App".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!appIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "1");
                    ModuleModel appData = new ModuleModel();
                    appData.setId(t.getSystemId() + "1");
                    appData.setSortCode(0L);
                    appData.setCreatorTime(datetime);
                    appData.setFullName("APP菜单");
                    appData.setIcon("icon-linzen icon-linzen-mobile");
                    appData.setParentId(t.getSystemId());
                    appData.setSystemId(t.getSystemId());
                    list.add(appData);
                    appIds.put(t.getSystemId(), appData.getId());
                } else {
                    t.setParentId(appIds.get(t.getSystemId()) + "");
                }
            } else if ("Web".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!webIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "2");
                    ModuleModel webData = new ModuleModel();
                    webData.setId(t.getSystemId() + "2");
                    webData.setSortCode(-1L);
                    webData.setCreatorTime(datetime);
                    webData.setFullName("WEB菜单");
                    webData.setIcon("icon-linzen icon-linzen-pc");
                    webData.setParentId(t.getSystemId());
                    webData.setSystemId(t.getSystemId());
                    list.add(webData);
                    webIds.put(t.getSystemId(), webData.getId());
                } else {
                    t.setParentId(webIds.get(t.getSystemId()) + "");
                }
            }
            ModuleModel model = BeanUtil.toBean(t, ModuleModel.class);
            list.add(model);
        });
        list.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> t.setParentId(t.getSystemId()));
        List<SysSystemEntity> systemList = systemService.getListByIds(systemId, null);
        List<ModuleModel> jsonToList1 = JsonUtil.createJsonToList(systemList, ModuleModel.class);
        jsonToList1.forEach(t -> {
            t.setParentId("-1");
            t.setSystemId(t.getId());
        });
        list.addAll(jsonToList1);

        List<String> mySystemIdList = authorizeModel.getSystemList().stream().map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> collect2 = list.stream().filter(t -> !mySystemIdList.contains(t.getSystemId())).map(ModuleModel::getId).collect(Collectors.toList());
        List<AuthorizeDataModel> treeList = JsonUtil.createJsonToList(list, AuthorizeDataModel.class);
        treeList.forEach(t -> {
            if (collect2.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode).thenComparing(AuthorizeDataModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<AuthorizeDataReturnModel> data = JsonUtil.createJsonToList(trees, AuthorizeDataReturnModel.class);
        vo.setAll(list.stream().map(ModuleModel::getId).distinct().collect(Collectors.toList()));
        vo.setList(data);
        containsList.addAll(collect2);
        vo.setIds(containsList.stream().distinct().collect(Collectors.toList()));
        return vo;
    }

    /**
     * 按钮权限
     *
     * @param moduleList         功能
     * @param selectorSystemList 应用
     * @param authorizeList      已有权限
     * @param authorizeModel     权限集合
     * @return
     */
    AuthorizeDataReturnVO moduleButton(List<ModuleEntity> moduleList, List<SysSystemEntity> selectorSystemList, List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        // 树
        List<ButtonModel> allButtonList = new ArrayList<>();
        // id
        List<String> ids = new ArrayList<>();
        List<String> noContainsIds = new ArrayList<>();
        // 转map
        Map<String, ModuleEntity> moduleMap = moduleList.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        Set<String> moduleIdIds = moduleMap.keySet();
        Map<String, SysSystemEntity> systemEntityMap = selectorSystemList.stream().collect(Collectors.toMap(SysSystemEntity::getId, Function.identity()));
        Set<String> systemIdIds = systemEntityMap.keySet();
        // 我的菜单权限
        List<ButtonModel> myButtonList = authorizeModel.getButtonList();
        myButtonList = myButtonList.stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        // 权限组的权限
        List<SysAuthorizeEntity> authorizeLists = authorizeList.stream().filter(t -> AuthorizeConst.BUTTON.equals(t.getItemType())).collect(Collectors.toList());
        List<ModuleButtonEntity> buttonByIds = moduleButtonService.getListByIds(authorizeLists.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())).stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        List<ButtonModel> permissionGroupButtonList = JsonUtil.createJsonToList(buttonByIds, ButtonModel.class);
        // 将菜单id设置给按钮的上级
        myButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        permissionGroupButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        // 所有的按钮权限
        allButtonList.addAll(myButtonList);
        allButtonList.addAll(permissionGroupButtonList);
        // 我的按钮id
        List<String> myButtonId = myButtonList.stream().map(ButtonModel::getId).collect(Collectors.toList());
        // 交集 1:1
        List<String> containsButtonList = permissionGroupButtonList.stream().filter(t -> myButtonId.contains(t.getId())).map(ButtonModel::getId).distinct().collect(Collectors.toList());
        // 我没有的
        List<String> noContainsButtonList = permissionGroupButtonList.stream().filter(t -> !containsButtonList.contains(t.getId())).map(ButtonModel::getId).collect(Collectors.toList());
        List<String> collect3 = authorizeList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        ids.addAll(collect3);
        ids.addAll(containsButtonList);
        noContainsIds.addAll(noContainsButtonList);
        // 我的菜单
        List<ModuleModel> myModuleList = authorizeModel.getModuleList();
        // 共有菜单
        List<String> containsModuleList = myModuleList.stream().filter(t -> moduleIdIds.contains(t.getId())).map(ModuleModel::getId).collect(Collectors.toList());
        List<String> myModuleIds = myModuleList.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<String> noContainsModuleList = moduleIdIds.stream().filter(t -> !myModuleIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsModuleList);
        noContainsIds.addAll(noContainsModuleList);
        // 我的应用
        List<SystemBaeModel> mySystemList = authorizeModel.getSystemList();
        // 共有应用
        List<String> containsSystemList = mySystemList.stream().filter(t -> systemIdIds.contains(t.getId())).map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> mySystemIds = mySystemList.stream().map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> noContainsSystemList = systemIdIds.stream().filter(t -> !mySystemIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsSystemList);
        noContainsIds.addAll(noContainsSystemList);
        // 所有按钮权限的上级
        List<String> allModuleIds = allButtonList.stream().map(ButtonModel::getParentId).distinct().collect(Collectors.toList());
        Map<String, ModuleEntity> allModuleListMap = new HashMap<>();
        // 新建APP菜单上级
        allModuleIds.forEach(t -> {
            ModuleEntity entity = moduleMap.get(t);
            while (entity != null) {
                allModuleListMap.put(entity.getId(), entity);
                entity = moduleMap.get(entity.getParentId());
            }
        });
        // 存放上级菜单id及上级 systemId,id
        Map<String, String> appIds = new HashMap<>(16);
        Map<String, String> webIds = new HashMap<>(16);
        long datetime = System.currentTimeMillis();
        allModuleListMap.values().stream().sorted(Comparator.comparing(ModuleEntity::getCategory).reversed()).forEach(t -> {
            if ("App".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!appIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "1");
                    ButtonModel appData = new ButtonModel();
                    appData.setId(t.getSystemId() + "1");
                    appData.setSortCode(0L);
                    appData.setCreatorTime(datetime);
                    appData.setFullName("APP菜单");
                    appData.setIcon("icon-linzen icon-linzen-mobile");
                    appData.setParentId(t.getSystemId());
                    allButtonList.add(appData);
                    appIds.put(t.getSystemId(), appData.getId());
                } else {
                    t.setParentId(appIds.get(t.getSystemId()) + "");
                }
            } else if ("Web".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!webIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "2");
                    ButtonModel webData = new ButtonModel();
                    webData.setId(t.getSystemId() + "2");
                    webData.setSortCode(-1L);
                    webData.setCreatorTime(datetime);
                    webData.setFullName("WEB菜单");
                    webData.setIcon("icon-linzen icon-linzen-pc");
                    webData.setParentId(t.getSystemId());
                    allButtonList.add(webData);
                    webIds.put(t.getSystemId(), webData.getId());
                } else {
                    t.setParentId(webIds.get(t.getSystemId()) + "");
                }
            }
            ButtonModel model = BeanUtil.toBean(t, ButtonModel.class);
            allButtonList.add(model);
        });
        allButtonList.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> t.setParentId(t.getSystemId()));
        List<String> systemId = allButtonList.stream().filter(t -> StringUtil.isNotEmpty(t.getParentId())).map(ButtonModel::getParentId).collect(Collectors.toList());
        systemId.forEach(t -> {
            if (Optional.ofNullable(systemEntityMap.get(t)).isPresent()) {
                ButtonModel jsonToBean = BeanUtil.toBean(systemEntityMap.get(t), ButtonModel.class);
                jsonToBean.setParentId("-1");
                allButtonList.add(jsonToBean);
            }
        });

        List<AuthorizeDataModel> treeList = JsonUtil.createJsonToList(allButtonList, AuthorizeDataModel.class);
        // 处理不可选的
        treeList.forEach(t -> {
            if (noContainsIds.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode).thenComparing(AuthorizeDataModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<AuthorizeDataReturnModel> data = JsonUtil.createJsonToList(trees, AuthorizeDataReturnModel.class);
        vo.setAll(allButtonList.stream().map(ButtonModel::getId).distinct().collect(Collectors.toList()));
        vo.setList(data);
        ids.addAll(noContainsIds);
        vo.setIds(ids.stream().distinct().collect(Collectors.toList()));
        return vo;
    }

    /**
     * 列表权限
     *
     * @param moduleList         功能
     * @param selectorSystemList 列表
     * @param authorizeList      已有权限
     * @param authorizeModel     权限集合
     * @return
     */
    AuthorizeDataReturnVO moduleColumn(List<ModuleEntity> moduleList, List<SysSystemEntity> selectorSystemList, List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        // 树
        List<ColumnModel> allButtonList = new ArrayList<>();
        // id
        List<String> ids = new ArrayList<>();
        List<String> noContainsIds = new ArrayList<>();
        // 转map
        Map<String, ModuleEntity> moduleMap = moduleList.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        Set<String> moduleIdIds = moduleMap.keySet();
        Map<String, SysSystemEntity> systemEntityMap = selectorSystemList.stream().collect(Collectors.toMap(SysSystemEntity::getId, Function.identity()));
        Set<String> systemIdIds = systemEntityMap.keySet();
        // 我的菜单权限
        List<ColumnModel> myButtonList = authorizeModel.getColumnList();
        myButtonList = myButtonList.stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        // 权限组的权限
        List<SysAuthorizeEntity> authorizeLists = authorizeList.stream().filter(t -> AuthorizeConst.COLUMN.equals(t.getItemType())).collect(Collectors.toList());
        List<ModuleColumnEntity> buttonByIds = moduleColumnService.getListByIds(authorizeLists.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())).stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        List<ColumnModel> permissionGroupButtonList = JsonUtil.createJsonToList(buttonByIds, ColumnModel.class);
        // 将菜单id设置给按钮的上级
        myButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        permissionGroupButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        // 所有的按钮权限
        allButtonList.addAll(myButtonList);
        allButtonList.addAll(permissionGroupButtonList);
        // 我的按钮id
        List<String> myButtonId = myButtonList.stream().map(ColumnModel::getId).collect(Collectors.toList());
        // 交集 1:1
        List<String> containsButtonList = permissionGroupButtonList.stream().filter(t -> myButtonId.contains(t.getId())).map(ColumnModel::getId).distinct().collect(Collectors.toList());
        // 我没有的
        List<String> noContainsButtonList = permissionGroupButtonList.stream().filter(t -> !containsButtonList.contains(t.getId())).map(ColumnModel::getId).collect(Collectors.toList());
        List<String> collect3 = authorizeList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        ids.addAll(collect3);
        ids.addAll(containsButtonList);
        noContainsIds.addAll(noContainsButtonList);
        // 我的菜单
        List<ModuleModel> myModuleList = authorizeModel.getModuleList();
        // 共有菜单
        List<String> containsModuleList = myModuleList.stream().filter(t -> moduleIdIds.contains(t.getId())).map(ModuleModel::getId).collect(Collectors.toList());
        List<String> myModuleIds = myModuleList.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<String> noContainsModuleList = moduleIdIds.stream().filter(t -> !myModuleIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsModuleList);
        noContainsIds.addAll(noContainsModuleList);
        // 我的应用
        List<SystemBaeModel> mySystemList = authorizeModel.getSystemList();
        // 共有应用
        List<String> containsSystemList = mySystemList.stream().filter(t -> systemIdIds.contains(t.getId())).map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> mySystemIds = mySystemList.stream().map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> noContainsSystemList = systemIdIds.stream().filter(t -> !mySystemIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsSystemList);
        noContainsIds.addAll(noContainsSystemList);
        // 所有按钮权限的上级
        List<String> allModuleIds = allButtonList.stream().map(ColumnModel::getParentId).distinct().collect(Collectors.toList());
        Map<String, ModuleEntity> allModuleListMap = new HashMap<>();
        // 新建APP菜单上级
        allModuleIds.forEach(t -> {
            ModuleEntity entity = moduleMap.get(t);
            while (entity != null) {
                allModuleListMap.put(entity.getId(), entity);
                entity = moduleMap.get(entity.getParentId());
            }
        });
        // 存放上级菜单id及上级 systemId,id
        Map<String, String> appIds = new HashMap<>(16);
        Map<String, String> webIds = new HashMap<>(16);
        long datetime = System.currentTimeMillis();
        allModuleListMap.values().stream().sorted(Comparator.comparing(ModuleEntity::getCategory).reversed()).forEach(t -> {
            if ("App".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!appIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "1");
                    ColumnModel appData = new ColumnModel();
                    appData.setId(t.getSystemId() + "1");
                    appData.setSortCode(0L);
                    appData.setCreatorTime(datetime);
                    appData.setFullName("APP菜单");
                    appData.setIcon("icon-linzen icon-linzen-mobile");
                    appData.setParentId(t.getSystemId());
                    allButtonList.add(appData);
                    appIds.put(t.getSystemId(), appData.getId());
                } else {
                    t.setParentId(appIds.get(t.getSystemId()) + "");
                }
            } else if ("Web".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!webIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "2");
                    ColumnModel webData = new ColumnModel();
                    webData.setId(t.getSystemId() + "2");
                    webData.setSortCode(-1L);
                    webData.setCreatorTime(datetime);
                    webData.setFullName("WEB菜单");
                    webData.setIcon("icon-linzen icon-linzen-pc");
                    webData.setParentId(t.getSystemId());
                    allButtonList.add(webData);
                    webIds.put(t.getSystemId(), webData.getId());
                } else {
                    t.setParentId(webIds.get(t.getSystemId()) + "");
                }
            }
            ColumnModel model = BeanUtil.toBean(t, ColumnModel.class);
            allButtonList.add(model);
        });
        allButtonList.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> t.setParentId(t.getSystemId()));
        List<String> systemId = allButtonList.stream().filter(t -> StringUtil.isNotEmpty(t.getParentId())).map(ColumnModel::getParentId).collect(Collectors.toList());
        systemId.forEach(t -> {
            if (Optional.ofNullable(systemEntityMap.get(t)).isPresent()) {
                ColumnModel jsonToBean = BeanUtil.toBean(systemEntityMap.get(t), ColumnModel.class);
                jsonToBean.setParentId("-1");
                allButtonList.add(jsonToBean);
            }
        });
        List<AuthorizeDataModel> treeList = JsonUtil.createJsonToList(allButtonList, AuthorizeDataModel.class);
        // 处理不可选的
        treeList.forEach(t -> {
            if (noContainsIds.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode).thenComparing(AuthorizeDataModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<AuthorizeDataReturnModel> data = JsonUtil.createJsonToList(trees, AuthorizeDataReturnModel.class);
        vo.setAll(allButtonList.stream().map(ColumnModel::getId).distinct().collect(Collectors.toList()));
        vo.setList(data);
        ids.addAll(noContainsIds);
        vo.setIds(ids.stream().distinct().collect(Collectors.toList()));
        return vo;
    }

    /**
     * 表单权限
     *
     * @param moduleList         功能
     * @param selectorSystemList 应用列表
     * @param authorizeList      已有权限
     * @param authorizeModel     权限集合
     * @return
     */
    AuthorizeDataReturnVO moduleForm(List<ModuleEntity> moduleList, List<SysSystemEntity> selectorSystemList, List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        // 树
        List<ModuleFormModel> allButtonList = new ArrayList<>();
        // id
        List<String> ids = new ArrayList<>();
        List<String> noContainsIds = new ArrayList<>();
        // 转map
        Map<String, ModuleEntity> moduleMap = moduleList.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        Set<String> moduleIdIds = moduleMap.keySet();
        Map<String, SysSystemEntity> systemEntityMap = selectorSystemList.stream().collect(Collectors.toMap(SysSystemEntity::getId, Function.identity()));
        Set<String> systemIdIds = systemEntityMap.keySet();
        // 我的菜单权限
        List<ModuleFormModel> myButtonList = authorizeModel.getFormsList();
        myButtonList = myButtonList.stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        // 权限组的权限
        List<SysAuthorizeEntity> authorizeLists = authorizeList.stream().filter(t -> AuthorizeConst.FROM.equals(t.getItemType())).collect(Collectors.toList());
        List<ModuleFormEntity> buttonByIds = moduleFormService.getListByIds(authorizeLists.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())).stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        List<ModuleFormModel> permissionGroupButtonList = JsonUtil.createJsonToList(buttonByIds, ModuleFormModel.class);
        // 将菜单id设置给按钮的上级
        myButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        permissionGroupButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        // 所有的按钮权限
        allButtonList.addAll(myButtonList);
        allButtonList.addAll(permissionGroupButtonList);
        // 我的按钮id
        List<String> myButtonId = myButtonList.stream().map(ModuleFormModel::getId).collect(Collectors.toList());
        // 交集 1:1
        List<String> containsButtonList = permissionGroupButtonList.stream().filter(t -> myButtonId.contains(t.getId())).map(ModuleFormModel::getId).distinct().collect(Collectors.toList());
        // 我没有的
        List<String> noContainsButtonList = permissionGroupButtonList.stream().filter(t -> !containsButtonList.contains(t.getId())).map(ModuleFormModel::getId).collect(Collectors.toList());
        List<String> collect3 = authorizeList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        ids.addAll(collect3);
        ids.addAll(containsButtonList);
        noContainsIds.addAll(noContainsButtonList);
        // 我的菜单
        List<ModuleModel> myModuleList = authorizeModel.getModuleList();
        // 共有菜单
        List<String> containsModuleList = myModuleList.stream().filter(t -> moduleIdIds.contains(t.getId())).map(ModuleModel::getId).collect(Collectors.toList());
        List<String> myModuleIds = myModuleList.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<String> noContainsModuleList = moduleIdIds.stream().filter(t -> !myModuleIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsModuleList);
        noContainsIds.addAll(noContainsModuleList);
        // 我的应用
        List<SystemBaeModel> mySystemList = authorizeModel.getSystemList();
        // 共有应用
        List<String> containsSystemList = mySystemList.stream().filter(t -> systemIdIds.contains(t.getId())).map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> mySystemIds = mySystemList.stream().map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> noContainsSystemList = systemIdIds.stream().filter(t -> !mySystemIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsSystemList);
        noContainsIds.addAll(noContainsSystemList);
        // 所有按钮权限的上级
        List<String> allModuleIds = allButtonList.stream().map(ModuleFormModel::getParentId).distinct().collect(Collectors.toList());
        Map<String, ModuleEntity> allModuleListMap = new HashMap<>();
        // 新建APP菜单上级
        allModuleIds.forEach(t -> {
            ModuleEntity entity = moduleMap.get(t);
            while (entity != null) {
                allModuleListMap.put(entity.getId(), entity);
                entity = moduleMap.get(entity.getParentId());
            }
        });
        // 存放上级菜单id及上级 systemId,id
        Map<String, String> appIds = new HashMap<>(16);
        Map<String, String> webIds = new HashMap<>(16);
        long datetime = System.currentTimeMillis();
        allModuleListMap.values().stream().sorted(Comparator.comparing(ModuleEntity::getCategory).reversed()).forEach(t -> {
            if ("App".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!appIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "1");
                    ModuleFormModel appData = new ModuleFormModel();
                    appData.setId(t.getSystemId() + "1");
                    appData.setSortCode(0L);
                    appData.setCreatorTime(datetime);
                    appData.setFullName("APP菜单");
                    appData.setIcon("icon-linzen icon-linzen-mobile");
                    appData.setParentId(t.getSystemId());
                    allButtonList.add(appData);
                    appIds.put(t.getSystemId(), appData.getId());
                } else {
                    t.setParentId(appIds.get(t.getSystemId()) + "");
                }
            } else if ("Web".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!webIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "2");
                    ModuleFormModel webData = new ModuleFormModel();
                    webData.setId(t.getSystemId() + "2");
                    webData.setSortCode(-1L);
                    webData.setCreatorTime(datetime);
                    webData.setFullName("WEB菜单");
                    webData.setIcon("icon-linzen icon-linzen-pc");
                    webData.setParentId(t.getSystemId());
                    allButtonList.add(webData);
                    webIds.put(t.getSystemId(), webData.getId());
                } else {
                    t.setParentId(webIds.get(t.getSystemId()) + "");
                }
            }
            ModuleFormModel model = BeanUtil.toBean(t, ModuleFormModel.class);
            allButtonList.add(model);
        });
        allButtonList.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> t.setParentId(t.getSystemId()));
        List<String> systemId = allButtonList.stream().filter(t -> StringUtil.isNotEmpty(t.getParentId())).map(ModuleFormModel::getParentId).collect(Collectors.toList());
        systemId.forEach(t -> {
            if (Optional.ofNullable(systemEntityMap.get(t)).isPresent()) {
                ModuleFormModel jsonToBean = BeanUtil.toBean(systemEntityMap.get(t), ModuleFormModel.class);
                jsonToBean.setParentId("-1");
                allButtonList.add(jsonToBean);
            }
        });
        List<AuthorizeDataModel> treeList = JsonUtil.createJsonToList(allButtonList, AuthorizeDataModel.class);
        // 处理不可选的
        treeList.forEach(t -> {
            if (noContainsIds.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode).thenComparing(AuthorizeDataModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<AuthorizeDataReturnModel> data = JsonUtil.createJsonToList(trees, AuthorizeDataReturnModel.class);
        vo.setAll(allButtonList.stream().map(ModuleFormModel::getId).distinct().collect(Collectors.toList()));
        vo.setList(data);
        ids.addAll(noContainsIds);
        vo.setIds(ids.stream().distinct().collect(Collectors.toList()));
        return vo;
    }

    /**
     * 数据权限
     *
     * @param moduleList         功能
     * @param selectorSystemList 应用方案
     * @param authorizeList      已有权限
     * @param authorizeModel     权限集合
     * @return
     */
    AuthorizeDataReturnVO resourceData(List<ModuleEntity> moduleList, List<SysSystemEntity> selectorSystemList, List<SysAuthorizeEntity> authorizeList, AuthorizeVO authorizeModel) {
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        // 树
        List<ResourceModel> allButtonList = new ArrayList<>();
        // id
        List<String> ids = new ArrayList<>();
        List<String> noContainsIds = new ArrayList<>();
        // 转map
        Map<String, ModuleEntity> moduleMap = moduleList.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        Set<String> moduleIdIds = moduleMap.keySet();
        Map<String, SysSystemEntity> systemEntityMap = selectorSystemList.stream().collect(Collectors.toMap(SysSystemEntity::getId, Function.identity()));
        Set<String> systemIdIds = systemEntityMap.keySet();
        // 我的菜单权限
        List<ResourceModel> myButtonList = authorizeModel.getResourceList();
        myButtonList = myButtonList.stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        // 权限组的权限
        List<SysAuthorizeEntity> authorizeLists = authorizeList.stream().filter(t -> AuthorizeConst.RESOURCE.equals(t.getItemType())).collect(Collectors.toList());
        List<ModuleDataAuthorizeSchemeEntity> buttonByIds = moduleDataAuthorizeSchemeService.getListByIds(authorizeLists.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())).stream().filter(t -> moduleIdIds.contains(t.getModuleId())).collect(Collectors.toList());
        List<ResourceModel> permissionGroupButtonList = JsonUtil.createJsonToList(buttonByIds, ResourceModel.class);
        // 将菜单id设置给按钮的上级
        myButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        permissionGroupButtonList.forEach(t -> t.setParentId(t.getModuleId()));
        // 所有的按钮权限
        allButtonList.addAll(myButtonList);
        allButtonList.addAll(permissionGroupButtonList);
        // 我的按钮id
        List<String> myButtonId = myButtonList.stream().map(ResourceModel::getId).collect(Collectors.toList());
        // 交集 1:1
        List<String> containsButtonList = permissionGroupButtonList.stream().filter(t -> myButtonId.contains(t.getId())).map(ResourceModel::getId).distinct().collect(Collectors.toList());
        // 我没有的
        List<String> noContainsButtonList = permissionGroupButtonList.stream().filter(t -> !containsButtonList.contains(t.getId())).map(ResourceModel::getId).collect(Collectors.toList());
        List<String> collect3 = authorizeList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList());
        ids.addAll(collect3);
        ids.addAll(containsButtonList);
        noContainsIds.addAll(noContainsButtonList);
        // 我的菜单
        List<ModuleModel> myModuleList = authorizeModel.getModuleList();
        // 共有菜单
        List<String> containsModuleList = myModuleList.stream().filter(t -> moduleIdIds.contains(t.getId())).map(ModuleModel::getId).collect(Collectors.toList());
        List<String> myModuleIds = myModuleList.stream().map(ModuleModel::getId).collect(Collectors.toList());
        List<String> noContainsModuleList = moduleIdIds.stream().filter(t -> !myModuleIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsModuleList);
        noContainsIds.addAll(noContainsModuleList);
        // 我的应用
        List<SystemBaeModel> mySystemList = authorizeModel.getSystemList();
        // 共有应用
        List<String> containsSystemList = mySystemList.stream().filter(t -> systemIdIds.contains(t.getId())).map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> mySystemIds = mySystemList.stream().map(SystemBaeModel::getId).collect(Collectors.toList());
        List<String> noContainsSystemList = systemIdIds.stream().filter(t -> !mySystemIds.contains(t)).collect(Collectors.toList());
        ids.addAll(containsSystemList);
        noContainsIds.addAll(noContainsSystemList);
        // 所有按钮权限的上级
        List<String> allModuleIds = allButtonList.stream().map(ResourceModel::getParentId).distinct().collect(Collectors.toList());
        Map<String, ModuleEntity> allModuleListMap = new HashMap<>();
        // 新建APP菜单上级
        allModuleIds.forEach(t -> {
            ModuleEntity entity = moduleMap.get(t);
            while (entity != null) {
                allModuleListMap.put(entity.getId(), entity);
                entity = moduleMap.get(entity.getParentId());
            }
        });
        // 存放上级菜单id及上级 systemId,id
        Map<String, String> appIds = new HashMap<>(16);
        Map<String, String> webIds = new HashMap<>(16);
        long datetime = System.currentTimeMillis();
        allModuleListMap.values().stream().sorted(Comparator.comparing(ModuleEntity::getCategory).reversed()).forEach(t -> {
            if ("App".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!appIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "1");
                    ResourceModel appData = new ResourceModel();
                    appData.setId(t.getSystemId() + "1");
                    appData.setSortCode(0L);
                    appData.setCreatorTime(datetime);
                    appData.setFullName("APP菜单");
                    appData.setIcon("icon-linzen icon-linzen-mobile");
                    appData.setParentId(t.getSystemId());
                    allButtonList.add(appData);
                    appIds.put(t.getSystemId(), appData.getId());
                } else {
                    t.setParentId(appIds.get(t.getSystemId()) + "");
                }
            } else if ("Web".equals(t.getCategory()) && "-1".equals(t.getParentId())) {
                if (!webIds.containsKey(t.getSystemId())) {
                    t.setParentId(t.getSystemId() + "2");
                    ResourceModel webData = new ResourceModel();
                    webData.setId(t.getSystemId() + "2");
                    webData.setSortCode(-1L);
                    webData.setCreatorTime(datetime);
                    webData.setFullName("WEB菜单");
                    webData.setIcon("icon-linzen icon-linzen-pc");
                    webData.setParentId(t.getSystemId());
                    allButtonList.add(webData);
                    webIds.put(t.getSystemId(), webData.getId());
                } else {
                    t.setParentId(webIds.get(t.getSystemId()) + "");
                }
            }
            ResourceModel model = BeanUtil.toBean(t, ResourceModel.class);
            allButtonList.add(model);
        });
        allButtonList.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> t.setParentId(t.getSystemId()));
        List<String> systemId = allButtonList.stream().filter(t -> StringUtil.isNotEmpty(t.getParentId())).map(ResourceModel::getParentId).collect(Collectors.toList());
        systemId.forEach(t -> {
            if (Optional.ofNullable(systemEntityMap.get(t)).isPresent()) {
                ResourceModel jsonToBean = BeanUtil.toBean(systemEntityMap.get(t), ResourceModel.class);
                jsonToBean.setParentId("-1");
                allButtonList.add(jsonToBean);
            }
        });
        List<AuthorizeDataModel> treeList = JsonUtil.createJsonToList(allButtonList, AuthorizeDataModel.class);
        // 处理不可选的
        treeList.forEach(t -> {
            if (noContainsIds.contains(t.getId())) {
                t.setDisabled(true);
            }
        });
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode).thenComparing(AuthorizeDataModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<AuthorizeDataReturnModel> data = JsonUtil.createJsonToList(trees, AuthorizeDataReturnModel.class);
        vo.setAll(allButtonList.stream().map(ResourceModel::getId).distinct().collect(Collectors.toList()));
        vo.setList(data);
        ids.addAll(noContainsIds);
        vo.setIds(ids.stream().distinct().collect(Collectors.toList()));
        return vo;
    }

}