package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.*;
import com.linzen.base.model.module.*;
import com.linzen.base.service.*;
import com.linzen.base.vo.DownloadVO;
import com.linzen.base.vo.ListVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.MsgCode;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.DataBaseException;
import com.linzen.model.AppDataInfoModel;
import com.linzen.model.AppObjectDataModel;
import com.linzen.model.FlowWorkModel;
import com.linzen.model.UserMenuModel;
import com.linzen.model.tenant.*;
import com.linzen.permission.entity.SysAuthorizeEntity;
import com.linzen.permission.entity.SysPermissionGroupEntity;
import com.linzen.permission.model.user.UserIdListVo;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.PermissionGroupService;
import com.linzen.permission.service.UserService;
import com.linzen.util.*;
import com.linzen.util.context.SpringContext;
import com.linzen.util.treeutil.ListToTreeUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.TreeViewModel;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import com.linzen.util.type.AuthorizeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统功能
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统菜单", description = "menu")
@RestController
@RequestMapping("/api/system/Menu")
public class ModuleController extends SuperController<ModuleService, ModuleEntity> {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SystemService systemService;
    @Autowired
    private PermissionGroupService permissionGroupApi;
    @Autowired
    private ModuleButtonService buttonService;
    @Autowired
    private ModuleColumnService columnService;
    @Autowired
    private ModuleFormService formService;
    @Autowired
    private ModuleDataAuthorizeSchemeService dataAuthorizeSchemeService;
    @Autowired
    private AuthorizeService authorizeApi;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigValueUtil configValueUtil;


    /**
     * 获取菜单列表
     *
     * @param systemId 系统id
     * @param paginationMenu 分页参数
     * @return ignore
     */
    @Operation(summary = "获取菜单列表")
    @Parameters({
            @Parameter(name = "systemId", description = "系统id", required = true)
    })
    @GetMapping("/ModuleBySystem/{systemId}")
    public ServiceResult<ListVO<MenuListVO>> list(@PathVariable("systemId") String systemId, PaginationMenu paginationMenu) {
        List<ModuleEntity> data = moduleService.getList(systemId, paginationMenu.getCategory(), paginationMenu.getKeyword(), paginationMenu.getType(), paginationMenu.getEnabledMark(), null, false);
        // 递归查上级
        Map<String, ModuleEntity> moduleEntityMap = data.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        if(StringUtil.isNotEmpty(paginationMenu.getKeyword())) {
            moduleService.getParentModule(data, moduleEntityMap);
        }
        List<UserMenuModel> list = JsonUtil.createJsonToList(moduleEntityMap.values(), UserMenuModel.class);
        list = list.stream().sorted(Comparator.comparing(UserMenuModel::getSortCode, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(UserMenuModel::getCreatorTime, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        List<SumTree<UserMenuModel>> menuList = TreeDotUtils.convertListToTreeDot(list);
        List<MenuListVO> menuvo = JsonUtil.createJsonToList(menuList, MenuListVO.class);
        ListVO vo = new ListVO();
        vo.setList(menuvo);
        return ServiceResult.success(vo);
    }

    /**
     * 获取菜单列表(下拉框)
     *
     * @param category 分类
     * @param id       主键
     * @return ignore
     */
    @Operation(summary = "获取菜单列表(下拉框)")
    @Parameters({
            @Parameter(name = "category", description = "分类"),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/Selector/{id}")
    public ServiceResult<ListVO<MenuSelectVO>> treeView(String category, @PathVariable("id") String id) {
        String systemId = "App".equals(category) ? userProvider.get().getAppSystemId() : userProvider.get().getSystemId();
        List<ModuleEntity> data = moduleService.getList(systemId, category, null, 1, 1, null, false);
        if (!"0".equals(id)) {
            data.remove(moduleService.getInfo(id));
        }
        List<UserMenuModel> list = JsonUtil.createJsonToList(data, UserMenuModel.class);
        List<SumTree<UserMenuModel>> menuList = TreeDotUtils.convertListToTreeDotFilter(list);
        List<MenuSelectVO> menuvo = JsonUtil.createJsonToList(menuList, MenuSelectVO.class);
        ListVO vo = new ListVO();
        vo.setList(menuvo);
        return ServiceResult.success(vo);
    }

    /**
     * 获取开发平台菜单
     *
     * @return ignore
     */
    @Operation(summary = "获取开发平台菜单")
    @GetMapping("/SystemSelector")
    public ServiceResult<ListVO<MenuSelectVO>> mainSystemSelector() {
        SysSystemEntity mainSystem = systemService.getInfoByEnCode(LinzenConst.MAIN_SYSTEM_CODE);
        List<ModuleEntity> data = moduleService.getList(mainSystem.getId(), null, null, null, 1, null, false);
        List<UserMenuModel> list = JsonUtil.createJsonToList(data, UserMenuModel.class);
        list.forEach(t -> {
            if ("-1".equals(t.getParentId())) {
                t.setParentId(t.getSystemId());
            }
        });
        UserMenuModel userMenuModel = BeanUtil.toBean(mainSystem, UserMenuModel.class);
        userMenuModel.setType(0);
        userMenuModel.setParentId("-1");
        list.add(userMenuModel);
        List<SumTree<UserMenuModel>> menuList = TreeDotUtils.convertListToTreeDotFilter(list);
        List<MenuSelectVO> menuvo = JsonUtil.createJsonToList(menuList, MenuSelectVO.class);
        ListVO vo = new ListVO();
        vo.setList(menuvo);
        return ServiceResult.success(vo);
    }

    /**
     * 通过系统id获取菜单列表(下拉框)
     *
     * @param category 分类
     * @param id       主键
     * @return ignore
     */
    @Operation(summary = "通过系统id获取菜单列表(下拉框)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "systemId", description = "系统主键", required = true)
    })
    @GetMapping("/Selector/{id}/{systemId}")
    public ServiceResult<ListVO<MenuSelectAllVO>> treeView(String category, @PathVariable("id") String id, @PathVariable("systemId") String systemId) {
        List<ModuleEntity> data = moduleService.getList(systemId, category, null, 1, 1, null, true);
        if (!"0".equals(id)) {
            data.remove(moduleService.getInfo(id));
        }
        List<UserMenuModel> list = JsonUtil.createJsonToList(data, UserMenuModel.class);
        if ("0".equals(systemId)) {
            List<String> moduleAuthorize = new ArrayList<>();
            if (configValueUtil.isMultiTenancy()) {
                TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
                moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
            }
            List<SysSystemEntity> list1 = systemService.getList(null, true, false, true, false, moduleAuthorize);
            list.forEach(t -> {
                if ("-1".equals(t.getParentId())) {
                    t.setParentId(t.getSystemId());
                }
            });
            List<UserMenuModel> jsonToList = JsonUtil.createJsonToList(list1, UserMenuModel.class);
            jsonToList.forEach(t -> {
                t.setType(0);
                t.setParentId("-1");
            });
            list.addAll(jsonToList);
        }
        List<SumTree<UserMenuModel>> menuList = TreeDotUtils.convertListToTreeDotFilter(list);
        List<MenuSelectAllVO> menuvo = JsonUtil.createJsonToList(menuList, MenuSelectAllVO.class);
        ListVO vo = new ListVO();
        vo.setList(menuvo);
        return ServiceResult.success(vo);
    }

    /**
     * 获取菜单权限返回权限组
     *
     * @param id       主键
     * @return ignore
     */
    @Operation(summary = "获取菜单权限返回权限组")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/getPermissionGroup/{id}")
    public ServiceResult<Map<String, Object>> getPermissionGroup(@PathVariable("id") String id) {
        Map<String, Object> map = new HashMap<>(2);
        int type = 0; // 0未开启权限，1有
        List<FlowWorkModel> list = new ArrayList<>();
        // 获取当前菜单开启了哪些权限
        ModuleEntity entity = moduleService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        // 权限是否被绑定，是否有权限
        List<ModuleButtonEntity> buttonList = buttonService.getListByModuleIds(id);
        List<ModuleColumnEntity> columnList = columnService.getList(id);
        List<ModuleFormEntity> formList = formService.getList(id);
        List<ModuleDataAuthorizeSchemeEntity> dataAuthorizeList = dataAuthorizeSchemeService.getList(id);
        if (buttonList.size() > 0 || columnList.size() > 0 || formList.size() >0 || dataAuthorizeList.size() > 0) {
            List<SysAuthorizeEntity> authorizeByItem = authorizeApi.getAuthorizeByItem(AuthorizeType.MODULE, entity.getId());
            List<String> collect = authorizeByItem.stream().map(SysAuthorizeEntity::getObjectId).collect(Collectors.toList());
            List<SysPermissionGroupEntity> permissionGroupByUserId = permissionGroupApi.list(collect);
            list = JsonUtil.createJsonToList(permissionGroupByUserId, FlowWorkModel.class);
            list.forEach(t -> t.setIcon("icon-linzen icon-linzen-authGroup"));
            if (list.size() > 0) {
                type = 1;
            } else {
                type = 2;
            }
        }
        map.put("list", list);
        map.put("type", type);
        return ServiceResult.success(map);
    }

    /**
     * 通过权限组id获取相关权限
     *
     * @param id       主键
     * @return ignore
     */
    @Operation(summary = "通过权限组id获取相关权限")
    @Parameters({
            @Parameter(name = "id", description = "权限组id", required = true)
    })
    @GetMapping("/getPermission/{id}/{permissionId}")
    public ServiceResult<ModulePermissionVO> getPermission(@PathVariable("id") String id, @PathVariable("permissionId") String permissionId) {
        // 获取当前菜单开启了哪些权限
        ModuleEntity entity = moduleService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        SysPermissionGroupEntity permissionGroupEntity = permissionGroupApi.info(permissionId);
        if (permissionGroupEntity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        // 权限组的权限
        List<SysAuthorizeEntity> authList = authorizeApi.getListByObjectId(Collections.singletonList(permissionId));

        ModulePermissionVO modulePermissionVO = new ModulePermissionVO();

        ModulePermissionModel permissionMemberModel = new ModulePermissionModel();
        permissionMemberModel.setFullName("权限成员");
        permissionMemberModel.setType(1);
        List<ModulePermissionModel.ModulePermissionBaseModel> permissionMember = new ArrayList<>();
        if (StringUtil.isNotEmpty(permissionGroupEntity.getPermissionMember())) {
            List<UserIdListVo> userIdListVos = userService.selectedByIds(Arrays.asList(permissionGroupEntity.getPermissionMember().split(",")));
            permissionMember = JsonUtil.createJsonToList(userIdListVos, ModulePermissionModel.ModulePermissionBaseModel.class);
        }
        permissionMemberModel.setList(permissionMember);
        modulePermissionVO.setPermissionMember(permissionMemberModel);


        ModulePermissionModel moduleButtonPermissionModel = new ModulePermissionModel();
        moduleButtonPermissionModel.setFullName("按钮权限");
        List<ModuleButtonEntity> buttonList = buttonService.getListByModuleIds(id);
        if (buttonList.size() > 0) {
            int type = 2;
            List<ModuleButtonEntity> collect = buttonList.stream()
                    .filter(t -> authList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())
                            .contains(t.getId())).collect(Collectors.toList());
            List<ModulePermissionModel.ModulePermissionBaseModel> buttonPermissionList = JsonUtil.createJsonToList(collect, ModulePermissionModel.ModulePermissionBaseModel.class);
            moduleButtonPermissionModel.setList(buttonPermissionList);
            if (buttonPermissionList.size() > 0) {
                type = 1;
            }
            moduleButtonPermissionModel.setType(type);
        }
        modulePermissionVO.setButtonAuthorize(moduleButtonPermissionModel);

        ModulePermissionModel moduleColumnPermissionModel = new ModulePermissionModel();
        moduleColumnPermissionModel.setFullName("列表权限");
        List<ModuleColumnEntity> columnList = columnService.getList(id);
        if (columnList.size() > 0) {
            int type = 2;
            List<ModuleColumnEntity> collect = columnList.stream()
                    .filter(t -> authList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())
                            .contains(t.getId())).collect(Collectors.toList());
            List<ModulePermissionModel.ModulePermissionBaseModel> buttonPermissionList = JsonUtil.createJsonToList(collect, ModulePermissionModel.ModulePermissionBaseModel.class);
            moduleColumnPermissionModel.setList(buttonPermissionList);
            if (buttonPermissionList.size() > 0) {
                type = 1;
            }
            moduleColumnPermissionModel.setType(type);
        }
        modulePermissionVO.setColumnAuthorize(moduleColumnPermissionModel);

        ModulePermissionModel moduleFromPermissionModel = new ModulePermissionModel();
        moduleFromPermissionModel.setFullName("表单权限");
        List<ModuleFormEntity> formList = formService.getList(id);
        if (formList.size() > 0) {
            int type = 2;
            List<ModuleFormEntity> collect = formList.stream()
                    .filter(t -> authList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())
                            .contains(t.getId())).collect(Collectors.toList());
            List<ModulePermissionModel.ModulePermissionBaseModel> buttonPermissionList = JsonUtil.createJsonToList(collect, ModulePermissionModel.ModulePermissionBaseModel.class);
            moduleFromPermissionModel.setList(buttonPermissionList);
            if (buttonPermissionList.size() > 0) {
                type = 1;
            }
            moduleFromPermissionModel.setType(type);
        }
        modulePermissionVO.setFormAuthorize(moduleFromPermissionModel);

        ModulePermissionModel moduleDataPermissionModel = new ModulePermissionModel();
        moduleDataPermissionModel.setFullName("数据权限");
        List<ModuleDataAuthorizeSchemeEntity> dataAuthorizeList = dataAuthorizeSchemeService.getList(id);
        if (dataAuthorizeList.size() > 0) {
            int type = 2;
            List<ModuleDataAuthorizeSchemeEntity> collect = dataAuthorizeList.stream()
                    .filter(t -> authList.stream().map(SysAuthorizeEntity::getItemId).collect(Collectors.toList())
                            .contains(t.getId())).collect(Collectors.toList());
            List<ModulePermissionModel.ModulePermissionBaseModel> buttonPermissionList = JsonUtil.createJsonToList(collect, ModulePermissionModel.ModulePermissionBaseModel.class);
            moduleDataPermissionModel.setList(buttonPermissionList);
            if (buttonPermissionList.size() > 0) {
                type = 1;
            }
            moduleDataPermissionModel.setType(type);
        }
        modulePermissionVO.setDataAuthorize(moduleDataPermissionModel);

        return ServiceResult.success(modulePermissionVO);
    }

    /**
     * 获取菜单列表(下拉框)
     *
     * @param category 分类
     * @return ignore
     */
    @Operation(summary = "获取菜单列表下拉框")
    @GetMapping("/Selector/All")
    public ServiceResult<ListVO<MenuSelectAllVO>> menuSelect(String category) {
        List<ModuleEntity> data = moduleService.getList("0", category, null, null, 1, null, false);
        List<UserMenuModel> list = JsonUtil.createJsonToList(data, UserMenuModel.class);
        List<String> moduleAuthorize = new ArrayList<>();
        if (configValueUtil.isMultiTenancy()) {
            TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
            moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
        }
        List<SysSystemEntity> list1 = systemService.getList(null, true, false, false, false, moduleAuthorize);
        list.forEach(t -> {
            t.setHasModule(!"1".equals(String.valueOf(t.getType())));
            if ("-1".equals(t.getParentId())) {
                t.setParentId(t.getSystemId());
            }
        });
        List<UserMenuModel> jsonToList = JsonUtil.createJsonToList(list1, UserMenuModel.class);
        jsonToList.forEach(t -> {
            t.setType(0);
            t.setHasModule(false);
            t.setParentId("-1");
        });
        list.addAll(jsonToList);
        List<SumTree<UserMenuModel>> menuList = TreeDotUtils.convertListToTreeDotFilter(list);
        List<MenuSelectAllVO> menuvo = JsonUtil.createJsonToList(menuList, MenuSelectAllVO.class);
        ListVO vo = new ListVO();
        vo.setList(menuvo);
        return ServiceResult.success(vo);
    }


    /**
     * 系统功能类别树形
     *
     * @return ignore
     */
    @Operation(summary = "系统功能类别树形")
    @GetMapping("/{systemId}/TreeView")
    public ServiceResult treeView(@PathVariable("systemId") String systemId) {
        List<ModuleEntity> moduleList = moduleService.getList(systemId,null, null, null, null,"0", true);
        List<TreeViewModel> treeList = new ArrayList<>();
        TreeViewModel treeViewModel = new TreeViewModel();
        treeViewModel.setId("apply");
        treeViewModel.setText("软件开发平台");
        treeViewModel.setParentId("0");
        treeViewModel.setImg("fa fa-windows apply");
        treeList.add(treeViewModel);
        for (ModuleEntity entity : moduleList) {
            TreeViewModel treeModel = new TreeViewModel();
            treeModel.setId(entity.getId());
            treeModel.setText(entity.getFullName());
            treeModel.setParentId("apply");
            treeModel.setImg("fa fa-tags");
            treeList.add(treeModel);
        }
        return ServiceResult.success(ListToTreeUtil.toTreeView(treeList));
    }


    /**
     * 获取菜单信息
     *
     * @param id 主键值
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取菜单信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}")
    public ServiceResult<ModuleInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleEntity entity = moduleService.getInfo(id);
        ModuleInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ModuleInfoVO.class);
        return ServiceResult.success(vo);
    }


    /**
     * 新建系统功能
     *
     * @param moduleCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建系统功能")
    @Parameters({
            @Parameter(name = "moduleCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid ModuleCrForm moduleCrForm) {
        ModuleEntity entity = BeanUtil.toBean(moduleCrForm, ModuleEntity.class);
        if (entity.getUrlAddress() != null) {
            entity.setUrlAddress(entity.getUrlAddress().trim());
        }
        if (moduleService.isExistByFullName(entity, moduleCrForm.getCategory(), moduleCrForm.getSystemId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (moduleService.isExistByEnCode(entity, moduleCrForm.getCategory(), moduleCrForm.getSystemId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        moduleService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新系统功能
     *
     * @param id           主键值
     * @param moduleUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "更新系统功能")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "moduleUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ModuleUpForm moduleUpForm) {
        ModuleEntity entity = BeanUtil.toBean(moduleUpForm, ModuleEntity.class);
        //判断如果是目录则不能修改类型
        ModuleEntity moduleEntity = moduleService.getInfo(id);
        if (moduleEntity != null && moduleEntity.getType() == 1 && entity.getType() != 1 && moduleService.getList(moduleEntity.getId()).size() > 0) {
            return ServiceResult.error("当前目录存在数据,不能修改类型");
        }
        entity.setId(id);
        Object bean = SpringContext.getBean("appDataServiceImpl");
        AppDataInfoModel appDataInfoModel = null;
        if (entity.getUrlAddress() != null) {
            entity.setUrlAddress(entity.getUrlAddress().trim());
            String urlAddress = "";
            Object method = ReflectionUtil.invokeMethod(bean, "getInfo", new Class[]{String.class}, new Object[]{id});
            appDataInfoModel = BeanUtil.toBean(method, AppDataInfoModel.class);
            if (appDataInfoModel != null && StringUtil.isNotEmpty(appDataInfoModel.getObjectData())) {
                AppObjectDataModel objectDataModel = BeanUtil.toBean(appDataInfoModel.getObjectData(), AppObjectDataModel.class);
                if (StringUtil.isNotEmpty(objectDataModel.getUrlAddress())) {
                    urlAddress = appDataInfoModel.getObjectData().replaceAll(objectDataModel.getUrlAddress(), entity.getUrlAddress());
                }
                appDataInfoModel.setObjectData(urlAddress);
            }
        }
        if (moduleService.isExistByFullName(entity, moduleUpForm.getCategory(), moduleUpForm.getSystemId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (moduleService.isExistByEnCode(entity, moduleUpForm.getCategory(), moduleUpForm.getSystemId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        boolean flag = moduleService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        // 修改常用应用数据
        if (appDataInfoModel != null) {
            Object method = ReflectionUtil.invokeMethod(bean, "update", new Class[]{AppDataInfoModel.class}, new Object[]{appDataInfoModel});
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除系统功能
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除系统功能")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleEntity entity = moduleService.getInfo(id);
        if (entity != null) {
            List<ModuleEntity> list = moduleService.getList(false, new ArrayList<>(), new ArrayList<>()).stream().filter(t -> t.getParentId().equals(entity.getId())).collect(Collectors.toList());
            if (list.size() > 0) {
                return ServiceResult.error("删除失败，请先删除子菜单。");
            }
            moduleService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 更新菜单状态
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "更新菜单状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult upState(@PathVariable("id") String id) {
        ModuleEntity entity = moduleService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == null || "1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            moduleService.update(id, entity);
            //清除redis权限
            String cacheKey = cacheKeyUtil.getUserAuthorize() + userProvider.get().getUserId();
            if (redisUtil.exists(cacheKey)) {
                redisUtil.remove(cacheKey);
            }
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /**
     * 系统菜单导出功能
     *
     * @param id 接口id
     * @return ignore
     */
    @Operation(summary = "导出系统菜单数据")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}/Actions/Export")
    public ServiceResult exportFile(@PathVariable("id") String id) {
        DownloadVO downloadVO = moduleService.exportData(id);
        return ServiceResult.success(downloadVO);
    }

    /**
     * 系统菜单导入功能
     *
     * @param systemId 系统id
     * @param multipartFile 文件
     * @param parentId 父级id
     * @param category 分类
     * @return ignore
     */
    @Operation(summary = "系统菜单导入功能")
    @Parameters({
            @Parameter(name = "systemId", description = "系统id", required = true),
    })
    @SaCheckPermission("system.menu")
    @PostMapping(value = "/{systemId}/Actions/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult importFile(@PathVariable("systemId") String systemId,
                                   @RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("parentId") String parentId,
                                   @RequestParam("category") String category,
                                   @RequestParam("type") Integer type) throws DataBaseException {
        //判断是否为.bm结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_MODULE.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        try {
            //读取文件内容
            String fileContent = FileUtil.getFileContent(multipartFile);
            //转model后导入
            ModuleExportModel exportModel = JsonUtil.createJsonToBean(fileContent, ModuleExportModel.class);
            ModuleEntity moduleEntity = exportModel.getModuleEntity();
            if (!category.equals(moduleEntity.getCategory())) {
                return ServiceResult.error("当前导入菜单为" + category.toUpperCase() + "端菜单，请在对应模块下导入！");
            }
            if ("App".equals(moduleEntity.getCategory()) && "-1".equals(parentId)) {
                return ServiceResult.error("请在顶级节点下创建目录后再进行菜单导入");
            }
            // 设置系统id然后重新赋值
            moduleEntity.setSystemId(systemId);
            moduleEntity.setParentId(parentId);
            //清空同步菜单记录 避免重复
            moduleEntity.setModuleId(null);
//            String enCode = moduleEntity.getEnCode();
//            moduleEntity.setEnCode(moduleEntity.getEnCode() + "_" + RandomUtil.getRandomCode());
//            if (moduleEntity.getType() == 3) {
//                moduleEntity.setUrlAddress(moduleEntity.getUrlAddress().replace(enCode, moduleEntity.getEnCode()));
//            }
            exportModel.setModuleEntity(moduleEntity);
            return moduleService.importData(exportModel, type);
        } catch (Exception e) {
            throw new DataBaseException(MsgCode.IMP004.get());
        }
    }

    // ------------------多租户调用
    /**
     * 通过租户id获取菜单
     *
     * @param tenantMenuModel 模型
     * @return ignore
     */
    @Operation(summary = "通过租户id获取菜单")
    @Parameters({
            @Parameter(name = "tenantMenuModel", description = "模型", required = true)
    })
    @NoDataSourceBind
    @PostMapping("/Tenant/Menu")
    public TenantMenuVO menu(@RequestBody TenantMenuModel tenantMenuModel) throws DataBaseException {
        if (configValueUtil.isMultiTenancy()) {
            TenantDataSourceUtil.switchTenant(tenantMenuModel.getTenantId());
        }
        List<SysSystemEntity> systemEntityList = systemService.getList();
        List<String> ids = new ArrayList<>();
        if (Objects.nonNull(tenantMenuModel.getIds())) {
            ids = tenantMenuModel.getIds();
        }
        List<String> urlAddressList = new ArrayList<>();
        if (Objects.nonNull(tenantMenuModel.getIds())) {
            urlAddressList = tenantMenuModel.getUrlAddressList();
        }
        List<ModuleEntity> moduleEntityList = moduleService.getList(true, new ArrayList<>(), new ArrayList<>());
        TenantMenuVO module = module(systemEntityList, moduleEntityList, ids, urlAddressList);
        return module;
    }

    /**
     * 功能权限
     *
     * @param moduleEntityList  所有菜单
     * @param systemEntityList  所有应用
     * @return
     */
    private TenantMenuVO module(List<SysSystemEntity> systemEntityList, List<ModuleEntity> moduleEntityList, List<String> ids, List<String> urlAddressList) {
        TenantMenuVO vo = new TenantMenuVO();
        // 转树前所有数据
        List<TenantMenuTreeModel> moduleAllList = new ArrayList<>();
        List<TenantMenuTreeModel> systemList = JsonUtil.createJsonToList(systemEntityList, TenantMenuTreeModel.class);
        systemList.forEach(t -> t.setParentId("-1"));
        moduleAllList.addAll(systemList);
        Map<String, List<ModuleEntity>> moduleMap = moduleEntityList.stream().collect(Collectors.groupingBy(t -> {
            if ("Web".equals(t.getCategory())) {
                return "Web";
            } else {
                return "App";
            }
        }));
        List<ModuleEntity> webModuleList = moduleMap.get("Web") == null ? new ArrayList<>() : moduleMap.get("Web");
        List<ModuleEntity> appModuleList = moduleMap.get("App") == null ? new ArrayList<>() : moduleMap.get("App");
        Map<String, ModuleEntity> appModuleMap = appModuleList.stream().collect(Collectors.toMap(ModuleEntity::getId, Function.identity()));
        Map<String, String> webIds = new HashMap<>(16);
        List<ModuleEntity> temWebList = webModuleList.stream().filter(t -> "-1".equals(t.getParentId())).collect(Collectors.toList());
        temWebList.stream().filter(t -> "-1".equals(t.getParentId())).forEach(t -> {
            if (!webIds.containsKey(t.getSystemId())) {
                ModuleEntity webData = new ModuleEntity();
                webData.setId(t.getSystemId() + "1");
                t.setParentId(webData.getId());
                webData.setFullName("WEB菜单");
                webData.setIcon("icon-linzen icon-linzen-pc");
                webData.setParentId(t.getSystemId());
                webData.setSystemId(t.getSystemId());
                webModuleList.add(webData);
                webIds.put(t.getSystemId(), webData.getId());
            } else {
                t.setParentId(webIds.get(t.getSystemId()) + "");
            }
        });
        List<TenantMenuTreeModel> webReturnModuleList = JsonUtil.createJsonToList(webModuleList, TenantMenuTreeModel.class);
        moduleAllList.addAll(webReturnModuleList);
        // 处理App菜单
        List<ModuleEntity> temList = appModuleList.stream().filter(t -> "-1".equals(t.getParentId()) && !LinzenConst.MAIN_SYSTEM_CODE.equals(t.getEnCode())).collect(Collectors.toList());
        Map<String, String> appIds = new HashMap<>(16);
        for (ModuleEntity appModuleEntity : temList) {
            if (StringUtil.isEmpty(appIds.get(appModuleEntity.getSystemId()))) {
                ModuleEntity appData = new ModuleEntity();
                appData.setId(appModuleEntity.getSystemId() + appModuleEntity.getSystemId() + "2");
                appModuleEntity.setParentId(appData.getId());
                appData.setFullName("APP菜单");
                appData.setIcon("icon-linzen icon-linzen-mobile");
                appData.setParentId(appModuleEntity.getSystemId());
                appData.setSystemId(appModuleEntity.getSystemId());
                appModuleList.add(appData);
                appIds.put(appModuleEntity.getSystemId(), appData.getId());
            } else {
                appModuleList.remove(appModuleEntity);
                ModuleEntity entity = appModuleMap.get(appModuleEntity.getId());
                entity.setParentId(appIds.get(appModuleEntity.getSystemId()));
                appModuleList.add(entity);
            }
        }
        List<TenantMenuTreeModel> appReturnModuleList = JsonUtil.createJsonToList(appModuleList, TenantMenuTreeModel.class);
        moduleAllList.addAll(appReturnModuleList);
        List<SumTree<TenantMenuTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(moduleAllList);
        List<TenantMenuTreeReturnModel> data = new ArrayList<>();
        TenantMenuTreeReturnModel workFlowEnabled = new TenantMenuTreeReturnModel();
        workFlowEnabled.setId("-999");
        workFlowEnabled.setFullName("协同办公");
        data.add(workFlowEnabled);
        data.addAll(JsonUtil.createJsonToList(sumTrees, TenantMenuTreeReturnModel.class));
        vo.setList(data);
        List<String> allId = moduleAllList.stream().map(TenantMenuTreeModel::getId).collect(Collectors.toList());
        allId.add(workFlowEnabled.getId());
        vo.setAll(allId);
        List<String> ids0 = moduleService.getListByUrlAddress(ids, urlAddressList).stream().map(ModuleEntity::getId).collect(Collectors.toList());
        List<String> selectorIds = allId.stream().filter(t -> !ids0.contains(t)).collect(Collectors.toList());
        if (ids.contains("-999")) {
            selectorIds.remove("-999");
        }
        vo.setIds(selectorIds);
        return vo;
    }

    /**
     * 通过租户id及菜单id获取菜单
     *
     * @param tenantMenuModel 模型
     * @return ignore
     */
    @Operation(summary = "通过租户id及菜单id获取菜单")
    @Parameters({
            @Parameter(name = "tenantMenuModel", description = "模型", required = true)
    })
    @NoDataSourceBind
    @PostMapping("/Tenant/MenuByIds")
    public Map MenuByIds(@RequestBody TenantMenuModel tenantMenuModel) throws DataBaseException {
        if (configValueUtil.isMultiTenancy()) {
            TenantDataSourceUtil.switchTenant(tenantMenuModel.getTenantId());
        }
        List<ModuleEntity> list = moduleService.getList();
        return list.stream().collect(Collectors.toMap(ModuleEntity::getId, ModuleEntity::getUrlAddress));
    }

}
