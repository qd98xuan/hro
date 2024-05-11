package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.entity.ModuleEntity;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.ModuleService;
import com.linzen.base.service.SystemService;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.LinzenConst;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.database.util.TenantDataSourceUtil;
import com.linzen.model.tenant.TenantAuthorizeModel;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.model.organizeadministrator.*;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 机构分级管理员
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Tag(name = "机构分级管理员", description = "organizeAdminIsTrator")
@Slf4j
@RestController
@RequestMapping("/api/permission/organizeAdminIsTrator")
public class OrganizeAdministratorController extends SuperController<OrganizeAdministratorService, SysOrganizeAdministratorEntity> {

    @Autowired
    private OrganizeAdministratorService organizeAdminIsTratorService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 获取分级管理员列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取分级管理员列表")
    @SaCheckPermission(value = {"permission.grade"})
    @GetMapping
    public ServiceResult<PageListVO<OrganizeAdministratorListVo>> list(Pagination pagination) {
        List<OrganizeAdministratorListVo> list = organizeAdminIsTratorService.getList(pagination);
        List<DictionaryDataEntity> dataServiceList4 = dictionaryDataService.getListByTypeDataCode("sex");
        Map<String, String> dataServiceMap4 = dataServiceList4.stream().filter(t -> ObjectUtil.equal(t.getEnabledMark(), 1)).collect(Collectors.toMap(DictionaryDataEntity::getEnCode, DictionaryDataEntity::getFullName));
        list.forEach(t -> {
            t.setGender(dataServiceMap4.get(t.getGender()));
        });
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(list, paginationVO);
    }

    /**
     * 保存分级管理员
     *
     * @param organizeAdminIsTratorCrForm 新建模型
     * @return
     */
    @Operation(summary = "保存分级管理员")
    @Parameters({
            @Parameter(name = "organizeAdminIsTratorCrForm", description = "新建模型", required = true)
    })
    @SaCheckPermission(value = {"permission.grade"})
    @PostMapping
    public ServiceResult save(@RequestBody @Valid OrganizeAdminIsTratorCrForm organizeAdminIsTratorCrForm) {
        String userId = organizeAdminIsTratorCrForm.getUserId();
        if (UserProvider.getLoginUserId().equals(userId)) {
            return ServiceResult.error("无法设置当前用户操作权限");
        }
        List<OrganizeAdministratorCrModel> list = new ArrayList<>(16);
        // 递归得到所有的数组
        getOrganizeAdminIsTratorModel(list, organizeAdminIsTratorCrForm.getOrgAdminModel());
        List<SysOrganizeAdministratorEntity> jsonToList = JsonUtil.createJsonToList(list, SysOrganizeAdministratorEntity.class);
        // 处理应用
        List<String> systemIds = organizeAdminIsTratorCrForm.getSystemIds();
        systemIds.forEach(t -> {
            SysOrganizeAdministratorEntity entity = new SysOrganizeAdministratorEntity();
            entity.setOrganizeType(PermissionConst.SYSTEM);
            entity.setOrganizeId(t);
            entity.setThisLayerSelect(1);
            jsonToList.add(entity);
        });
        // 处理菜单
        List<String> moduleIds = organizeAdminIsTratorCrForm.getModuleIds();
        moduleIds.forEach(t -> {
            SysOrganizeAdministratorEntity entity = new SysOrganizeAdministratorEntity();
            entity.setOrganizeType(PermissionConst.MODULE);
            entity.setOrganizeId(t);
            entity.setThisLayerSelect(1);
            jsonToList.add(entity);
        });
        organizeAdminIsTratorService.createList(jsonToList, userId);
        return ServiceResult.success("保存成功");
    }

    /**
     * 获取
     *
     * @param list
     * @param jsonToList
     */
    private void getOrganizeAdminIsTratorModel(List<OrganizeAdministratorCrModel> list, List<OrganizeAdministratorCrModel> jsonToList) {
        if (jsonToList != null) {
            jsonToList.forEach(t -> {
                OrganizeAdministratorCrModel vo = BeanUtil.toBean(t, OrganizeAdministratorCrModel.class);
                vo.setChildren(null);
                if (vo.getThisLayerSelect() != null && (vo.getThisLayerSelect() == 2 || vo.getThisLayerSelect() == 1)) {
                    vo.setThisLayerSelect(1);
                    if (vo.getThisLayerAdd() != null && (vo.getThisLayerAdd() == 2 || vo.getThisLayerAdd() == 1)) {
                        vo.setThisLayerAdd(1);
                    }
                    if (vo.getThisLayerEdit() != null && (vo.getThisLayerEdit() == 2 || vo.getThisLayerEdit() == 1)) {
                        vo.setThisLayerEdit(1);
                    }
                    if (vo.getThisLayerDelete() != null && (vo.getThisLayerDelete() == 2 || vo.getThisLayerDelete() == 1)) {
                        vo.setThisLayerDelete(1);
                    }
                } else if (vo.getThisLayerSelect() == null || vo.getThisLayerSelect() == 0 || vo.getThisLayerSelect() == 3) {
                    vo.setThisLayerSelect(0);
                    vo.setThisLayerAdd(0);
                    vo.setThisLayerEdit(0);
                    vo.setThisLayerDelete(0);
                }
                if (vo.getSubLayerSelect() != null && (vo.getSubLayerSelect() == 2 || vo.getSubLayerSelect() == 1)) {
                    vo.setSubLayerSelect(1);
                    if (vo.getSubLayerAdd() != null && (vo.getSubLayerAdd() == 2 || vo.getSubLayerAdd() == 1)) {
                        vo.setSubLayerAdd(1);
                    }
                    if (vo.getSubLayerEdit() != null && (vo.getSubLayerEdit() == 2 || vo.getSubLayerEdit() == 1)) {
                        vo.setSubLayerEdit(1);
                    }
                    if (vo.getSubLayerDelete() != null && (vo.getSubLayerDelete() == 2 || vo.getSubLayerDelete() == 1)) {
                        vo.setSubLayerDelete(1);
                    }
                } else if (vo.getSubLayerSelect() == null || vo.getSubLayerSelect() == 0 || vo.getSubLayerSelect() == 3) {
                    vo.setSubLayerSelect(0);
                    vo.setSubLayerAdd(0);
                    vo.setSubLayerEdit(0);
                    vo.setSubLayerDelete(0);
                }
                list.add(vo);
                getOrganizeAdminIsTratorModel(list, t.getChildren());
            });
        }
    }

    /**
     * 删除二级管理员
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除二级管理员")
    @Parameters({
            @Parameter(name = "id", description = "用户id", required = true)
    })
    @SaCheckPermission(value = {"permission.grade"})
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        organizeAdminIsTratorService.deleteByUserId(id);
        return ServiceResult.success(MsgCode.SU003.get());
    }

    /**
     * 获取组织下拉框列表
     *
     * @param userId 用户id
     * @return
     */
    @Operation(summary = "获取组织下拉框列表")
    @SaCheckPermission(value = {"permission.grade"})
    @GetMapping("/Selector")
    public ServiceResult<OrganizeAdministratorSelectedVO> getSelector(String userId) {
        OrganizeAdministratorSelectedVO vo = new OrganizeAdministratorSelectedVO();
        if (StringUtil.isEmpty(userId)) {
            return ServiceResult.success(vo);
        }
        if (StringUtil.isNotEmpty(userId)) {
            if (userId.equals(UserProvider.getLoginUserId())) {
                return ServiceResult.error("无法设置当前用户为分级管理员");
            }
            SysUserEntity userEntity = userService.getInfo(userId);
            if (userEntity != null && Objects.equals(userEntity.getIsAdministrator(), 1)) {
                return ServiceResult.error("无法设置超管为分级管理员");
            }
        }
        //租户权限
        List<String> moduleAuthorize = new ArrayList<>();
        List<String> moduleUrlAddressAuthorize = new ArrayList<>();
        if (configValueUtil.isMultiTenancy()) {
            TenantAuthorizeModel tenantAuthorizeModel = TenantDataSourceUtil.getCacheModuleAuthorize(UserProvider.getUser().getTenantId());
            moduleAuthorize = tenantAuthorizeModel.getModuleIdList();
            moduleUrlAddressAuthorize = tenantAuthorizeModel.getUrlAddressList();
        }
        List<SystemSelectorVO> systemPermissionList = new ArrayList<>();
        List<ModuleSelectorVO> moduleVOPermissionList = new ArrayList<>();
        List<ModuleSelectorModel> modulePermissionList = new ArrayList<>();
        List<String> moduleSelectedList = new ArrayList<>();
        List<String> systemSelectedList = new ArrayList<>();
        // 存储组织集合
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, false, null);
        // 获取所有组织
        List<OrganizeAdministratorSelectorModel> selectorModels = JsonUtil.createJsonToList(orgMaps.values(), OrganizeAdministratorSelectorModel.class);
        // 获取应用列表
        List<SysSystemEntity> list = systemService.getList(null, false, true, true, false, new ArrayList<>());
        List<SystemSelectorVO> systemSelectorVOList = JsonUtil.createJsonToList(list, SystemSelectorVO.class);
        systemSelectorVOList.forEach(t -> t.setIsPermission(0));
        // 菜单
        SysSystemEntity mainSystem = systemService.getInfoByEnCode(LinzenConst.MAIN_SYSTEM_CODE);
        List<ModuleEntity> mainModule = moduleService.getMainModule(moduleAuthorize, moduleUrlAddressAuthorize, false);
        List<ModuleEntity> allModule = new ArrayList<>(mainModule);
        List<ModuleSelectorModel> moduleSelectorVOList = JsonUtil.createJsonToList(mainModule, ModuleSelectorModel.class);
        List<ModuleSelectorModel> allModuleSelectorVOList = JsonUtil.createJsonToList(allModule, ModuleSelectorModel.class);

        boolean isAdministrator = userProvider.get().getIsAdministrator();
        if (isAdministrator) {
            selectorModels.forEach(t -> {
                // 设置组织id
                t.setOrganizeId(t.getId());
                t.setThisLayerAdd(0);
                t.setThisLayerEdit(0);
                t.setThisLayerDelete(0);
                t.setThisLayerSelect(0);
                t.setSubLayerAdd(0);
                t.setSubLayerEdit(0);
                t.setSubLayerDelete(0);
                t.setSubLayerSelect(0);
            });
            moduleSelectorVOList.forEach(t -> t.setIsPermission(0));
        } else {
            List<SysOrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(UserProvider.getLoginUserId(), PermissionConst.ORGANIZE, false);
            // 当前用户分级管理权限
            Map<String, SysOrganizeAdministratorEntity> thisOrganizeMap = organizeAdministratorEntity.stream().collect(Collectors.toMap(SysOrganizeAdministratorEntity::getOrganizeId, Function.identity()));
            List<SysOrganizeAdministratorEntity> organizeAdministratorEntitys = new ArrayList<>(organizeAdministratorEntity);
            // 处理子组织权限
            organizeAdministratorEntitys.forEach(t -> {
                if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                    // 得到组织id
                    List<String> underOrganizations = orgMaps.values().stream().filter(tt -> tt.getOrganizeIdTree().contains(t.getOrganizeId())).map(SysOrganizeEntity::getId).collect(Collectors.toList());
//                            organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                    underOrganizations.forEach(uo -> {
                        SysOrganizeAdministratorEntity organizeAdministratorEntity1 = thisOrganizeMap.get(uo);
                        if (organizeAdministratorEntity1 != null) {
                            organizeAdministratorEntity1.setThisLayerSelect(1);
                            organizeAdministratorEntity1.setSubLayerSelect(1);
                            if (t.getSubLayerAdd() != null && t.getSubLayerAdd() == 1) {
                                organizeAdministratorEntity1.setThisLayerAdd(1);
                                organizeAdministratorEntity1.setSubLayerAdd(1);
                            }
                            if (t.getSubLayerEdit() != null && t.getSubLayerEdit() == 1) {
                                organizeAdministratorEntity1.setThisLayerEdit(1);
                                organizeAdministratorEntity1.setSubLayerEdit(1);
                            }
                            if (t.getSubLayerDelete() != null && t.getSubLayerDelete() == 1) {
                                organizeAdministratorEntity1.setThisLayerDelete(1);
                                organizeAdministratorEntity1.setSubLayerDelete(1);
                            }
                        }
                    });
                }
            });

            List<OrganizeAdministratorSelectorModel> selectorModelss = new ArrayList<>(16);
            selectorModels.forEach(t -> {
                // 设置组织id
                t.setOrganizeId(t.getId());
                SysOrganizeAdministratorEntity administratorEntity = thisOrganizeMap.get(t.getOrganizeId());
                if (administratorEntity != null) {
                    boolean flag = false;
                    if (administratorEntity.getThisLayerSelect() != null && administratorEntity.getThisLayerSelect() == 1) {
                        t.setThisLayerSelect(0);
                        flag = true;
                        if (administratorEntity.getThisLayerAdd() == 1) {
                            t.setThisLayerAdd(0);
                        }
                        if (administratorEntity.getThisLayerEdit() == 1) {
                            t.setThisLayerEdit(0);
                        }
                        if (administratorEntity.getThisLayerDelete() == 1) {
                            t.setThisLayerDelete(0);
                        }
                    }
                    if (administratorEntity.getSubLayerSelect()!= null && administratorEntity.getSubLayerSelect() == 1) {
                        t.setSubLayerSelect(0);
                        flag = true;
                        if (administratorEntity.getSubLayerAdd()!= null && administratorEntity.getSubLayerAdd() == 1) {
                            t.setSubLayerAdd(0);
                        }
                        if (administratorEntity.getSubLayerEdit()!= null && administratorEntity.getSubLayerEdit() == 1) {
                            t.setSubLayerEdit(0);
                        }
                        if (administratorEntity.getSubLayerDelete()!= null && administratorEntity.getSubLayerDelete() == 1) {
                            t.setSubLayerDelete(0);
                        }
                    }
                    if (flag) {
                        selectorModelss.add(t);
                    }
                }
            });
            selectorModels = selectorModelss;

            List<SysOrganizeAdministratorEntity> moduleOrganizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(UserProvider.getLoginUserId(), PermissionConst.MODULE, false);
            List<String> moduleIds = moduleOrganizeAdministratorEntity.stream().map(SysOrganizeAdministratorEntity::getOrganizeId).collect(Collectors.toList());
            List<ModuleEntity> moduleByIds = moduleService.getModuleByIds(moduleIds, null, null, false);
            moduleSelectorVOList = JsonUtil.createJsonToList(moduleByIds, ModuleSelectorModel.class);

//			List<OrganizeAdministratorEntity> systemOrganizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(UserProvider.getLoginUserId(), true, PermissionConst.SYSTEM);

        }
        Map<String, OrganizeAdministratorSelectorModel> modelMap = selectorModels.stream().collect(Collectors.toMap(OrganizeAdministratorSelectorModel::getOrganizeId, Function.identity()));
        // 判断是否为空
        if (StringUtil.isNotEmpty(userId)) {
            List<SysOrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userId, PermissionConst.ORGANIZE, false);
            // 处理子组织字段
            Map<String, SysOrganizeAdministratorEntity> hisOrganizeMap = organizeAdministratorEntity.stream().collect(Collectors.toMap(SysOrganizeAdministratorEntity::getOrganizeId, Function.identity()));
            List<OrganizeAdministratorSelectorModel> finalSelectorModels3 = selectorModels;
            hisOrganizeMap.values().forEach(t -> {
                if (!isAdministrator) {
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        // 得到组织id
                        List<String> underOrganizations = orgMaps.values().stream().filter(tt -> tt.getOrganizeIdTree().contains(t.getOrganizeId())).map(SysOrganizeEntity::getId).collect(Collectors.toList());
//                                organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        // 将同样的组织id的数据先处理好
                        List<OrganizeAdministratorSelectorModel> collect1 = modelMap.values().stream().filter(fsm -> underOrganizations.contains(fsm.getOrganizeId())).collect(Collectors.toList());
                        collect1.forEach(cl -> {
                            OrganizeAdministratorSelectorModel organizeAdministratorSelectorModel = modelMap.get(cl.getOrganizeId());
                            if (organizeAdministratorSelectorModel != null) {
                                if (organizeAdministratorSelectorModel.getThisLayerSelect() != null && organizeAdministratorSelectorModel.getThisLayerSelect() == 0) {
                                    organizeAdministratorSelectorModel.setThisLayerSelect(0);
                                } else {
                                    organizeAdministratorSelectorModel.setThisLayerSelect(3);
                                }
                                if (organizeAdministratorSelectorModel.getSubLayerSelect() != null && organizeAdministratorSelectorModel.getSubLayerSelect() == 0) {
                                    organizeAdministratorSelectorModel.setSubLayerSelect(0);
                                } else {
                                    organizeAdministratorSelectorModel.setSubLayerSelect(3);
                                }
                            }
                        });
                        // 当前模型包含组织id
                        List<String> collect = underOrganizations.stream().filter(uo -> !modelMap.values().stream().map(OrganizeAdministratorSelectorModel::getOrganizeId).collect(Collectors.toList()).contains(uo)).collect(Collectors.toList());
                        collect.forEach(cl -> {
                            SysOrganizeAdministratorEntity organizeAdministratorEntity1 = hisOrganizeMap.get(cl);
                            if (organizeAdministratorEntity1 != null) {
                                SysOrganizeEntity info = orgMaps.get(organizeAdministratorEntity1.getOrganizeId());
                                OrganizeAdministratorSelectorModel organizeAdministratorSelectorModel = BeanUtil.toBean(info, OrganizeAdministratorSelectorModel.class);
                                organizeAdministratorSelectorModel.setOrganizeId(organizeAdministratorEntity1.getOrganizeId());
                                organizeAdministratorSelectorModel.setThisLayerSelect(3);
                                organizeAdministratorSelectorModel.setSubLayerSelect(3);
                                finalSelectorModels3.add(organizeAdministratorSelectorModel);
                            }
                        });
                    }
                }
            });
            Map<String, OrganizeAdministratorSelectorModel> selectorModelMap = selectorModels.stream().collect(Collectors.toMap(OrganizeAdministratorSelectorModel::getOrganizeId, Function.identity()));
            List<OrganizeAdministratorSelectorModel> finalSelectorModels1 = new ArrayList<>(selectorModels);
            hisOrganizeMap.values().forEach(t -> {
                // 我没有他有时，需要判断此组织是否跟我所管理的组织相同，不同则需要放进去
                OrganizeAdministratorSelectorModel organizeAdministratorSelectorModel = selectorModelMap.get(t.getOrganizeId());
                if (organizeAdministratorSelectorModel == null) {
                    boolean flag = false;
                    if (t.getThisLayerSelect() != null) {
                        if (t.getThisLayerSelect() == 1) {
                            t.setThisLayerSelect(2);
                            flag = true;
                            if (flag && t.getThisLayerAdd() != null && t.getThisLayerAdd() == 1) {
                                t.setThisLayerAdd(2);
                            } else if (t.getThisLayerAdd() != null) {
                                t.setThisLayerAdd(null);
                            }
                            if (flag && t.getThisLayerEdit() != null && t.getThisLayerEdit() == 1) {
                                t.setThisLayerEdit(2);
                            } else if (t.getThisLayerEdit() != null) {
                                t.setThisLayerEdit(null);
                            }
                            if (flag && t.getThisLayerDelete() != null && t.getThisLayerDelete() == 1) {
                                t.setThisLayerDelete(2);
                            } else if (t.getThisLayerDelete() != null) {
                                t.setThisLayerDelete(null);
                            }
                        } else {
                            t.setThisLayerSelect(null);
                            t.setThisLayerAdd(null);
                            t.setThisLayerEdit(null);
                            t.setThisLayerDelete(null);
                        }
                    }
                    boolean flag1 = false;
                    if (t.getSubLayerSelect() != null) {
                        if (t.getSubLayerSelect() == 1) {
                            t.setSubLayerSelect(2);
                            flag1 = true;
                            if (flag1 && t.getSubLayerAdd() != null && t.getSubLayerAdd() == 1) {
                                t.setSubLayerAdd(2);
                            } else if (t.getSubLayerAdd() != null) {
                                t.setSubLayerAdd(null);
                            }
                            if (flag1 && t.getSubLayerEdit() != null && t.getSubLayerEdit() == 1) {
                                t.setSubLayerEdit(2);
                            } else if (t.getSubLayerEdit() != null) {
                                t.setSubLayerEdit(null);
                            }
                            if (flag1 && t.getSubLayerDelete() != null && t.getSubLayerDelete() == 1) {
                                t.setSubLayerDelete(2);
                            } else if (t.getSubLayerDelete() != null) {
                                t.setSubLayerDelete(null);
                            }
                        } else {
                            t.setSubLayerSelect(null);
                            t.setSubLayerAdd(null);
                            t.setSubLayerEdit(null);
                            t.setSubLayerDelete(null);
                        }
                    }
                    if (flag || flag1) {
                        OrganizeAdministratorSelectorModel jsonToBean = BeanUtil.toBean(t, OrganizeAdministratorSelectorModel.class);
                        SysOrganizeEntity info = orgMaps.get(t.getOrganizeId());
                        if (info != null) {
                            jsonToBean.setParentId(info.getParentId());
                            jsonToBean.setId(info.getId());
                            jsonToBean.setCategory(info.getCategory());
                            jsonToBean.setIcon(PermissionConst.COMPANY.equals(info.getCategory()) ? "icon-linzen icon-linzen-tree-organization3" : "icon-linzen icon-linzen-tree-department1");
                            jsonToBean.setOrganizeId(t.getOrganizeId());
                            jsonToBean.setOrganizeIdTree(info.getOrganizeIdTree());
                            jsonToBean.setFullName(info.getFullName());
                            finalSelectorModels1.add(jsonToBean);
                        }
                    }
                }
            });
            hisOrganizeMap.values().forEach(t -> {
                finalSelectorModels1.forEach(fs -> {
                    if (t.getOrganizeId().equals(fs.getOrganizeId())) {
                        // 本层添加
                        if (fs.getThisLayerAdd() != null) {
                            if (fs.getThisLayerAdd() == 0) {
                                if (t.getThisLayerAdd() != null) {
                                    if (t.getThisLayerAdd() == 1) {
                                        fs.setThisLayerAdd(1);
                                    } else {
                                        fs.setThisLayerAdd(0);
                                    }
                                }
                            } else if (fs.getThisLayerAdd() == 3) {
                                if (t.getThisLayerAdd() != null) {
                                    if (t.getThisLayerAdd() == 1) {
                                        fs.setThisLayerAdd(1);
                                    } else {
                                        fs.setThisLayerAdd(3);
                                    }
                                }
                            } else {
                                if (t.getThisLayerAdd() != null) {
                                    if (t.getThisLayerAdd() == 1) {
                                        if (isAdministrator) {
                                            fs.setThisLayerAdd(1);
                                        } else {
                                            fs.setThisLayerAdd(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getThisLayerAdd() != null) {
                                if (t.getThisLayerAdd() == 1) {
                                    if (isAdministrator) {
                                        fs.setThisLayerAdd(1);
                                    } else {
                                        fs.setThisLayerAdd(2);
                                    }
                                }
                            }
                        }
                        // 本层编辑
                        if (fs.getThisLayerEdit() != null) {
                            if (fs.getThisLayerEdit() == 0) {
                                if (t.getThisLayerEdit() != null) {
                                    if (t.getThisLayerEdit() == 1) {
                                        fs.setThisLayerEdit(1);
                                    } else {
                                        fs.setThisLayerEdit(0);
                                    }
                                }
                            } else if (fs.getThisLayerEdit() == 3) {
                                if (t.getThisLayerEdit() != null) {
                                    if (t.getThisLayerEdit() == 1) {
                                        fs.setThisLayerEdit(1);
                                    } else {
                                        fs.setThisLayerEdit(3);
                                    }
                                }
                            } else {
                                if (t.getThisLayerEdit() != null) {
                                    if (t.getThisLayerEdit() == 1) {
                                        if (isAdministrator) {
                                            fs.setThisLayerEdit(1);
                                        } else {
                                            fs.setThisLayerEdit(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getThisLayerEdit() != null) {
                                if (t.getThisLayerEdit() == 1) {
                                    if (isAdministrator) {
                                        fs.setThisLayerEdit(1);
                                    } else {
                                        fs.setThisLayerEdit(2);
                                    }
                                }
                            }
                        }
                        // 本层删除
                        if (fs.getThisLayerDelete() != null) {
                            if (fs.getThisLayerDelete() == 0) {
                                if (t.getThisLayerDelete() != null) {
                                    if (t.getThisLayerDelete() == 1) {
                                        fs.setThisLayerDelete(1);
                                    } else {
                                        fs.setThisLayerDelete(0);
                                    }
                                }
                            } else if (fs.getThisLayerDelete() == 3) {
                                if (t.getThisLayerDelete() != null) {
                                    if (t.getThisLayerDelete() == 1) {
                                        fs.setThisLayerDelete(1);
                                    } else {
                                        fs.setThisLayerDelete(3);
                                    }
                                }
                            } else {
                                if (t.getThisLayerDelete() != null) {
                                    if (t.getThisLayerDelete() == 1) {
                                        if (isAdministrator) {
                                            fs.setThisLayerDelete(1);
                                        } else {
                                            fs.setThisLayerDelete(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getThisLayerDelete() != null) {
                                if (t.getThisLayerDelete() == 1) {
                                    if (isAdministrator) {
                                        fs.setThisLayerDelete(1);
                                    } else {
                                        fs.setThisLayerDelete(2);
                                    }
                                }
                            }
                        }
                        // 本层查看
                        if (fs.getThisLayerSelect() != null) {
                            if (fs.getThisLayerSelect() == 0) {
                                if (t.getThisLayerSelect() != null) {
                                    if (t.getThisLayerSelect() == 1) {
                                        fs.setThisLayerSelect(1);
                                    } else {
                                        fs.setThisLayerSelect(0);
                                    }
                                }
                            } else if (fs.getThisLayerSelect() == 3) {
                                if (t.getThisLayerSelect() != null) {
                                    if (t.getThisLayerSelect() == 1) {
                                        fs.setThisLayerSelect(1);
                                    } else {
                                        fs.setThisLayerSelect(3);
                                    }
                                }
                            } else {
                                if (t.getThisLayerSelect() != null) {
                                    if (t.getThisLayerSelect() == 1) {
                                        if (isAdministrator) {
                                            fs.setThisLayerSelect(1);
                                        } else {
                                            fs.setThisLayerSelect(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getThisLayerSelect() != null) {
                                if (t.getThisLayerSelect() == 1) {
                                    if (isAdministrator) {
                                        fs.setThisLayerSelect(1);
                                    } else {
                                        fs.setThisLayerSelect(2);
                                    }
                                }
                            }
                        }
                        // 子层添加
                        if (fs.getSubLayerAdd() != null) {
                            if (fs.getSubLayerAdd() == 0) {
                                if (t.getSubLayerAdd() != null) {
                                    if (t.getSubLayerAdd() == 1) {
                                        fs.setSubLayerAdd(1);
                                    } else {
                                        fs.setSubLayerAdd(0);
                                    }
                                }
                            } else if (fs.getSubLayerAdd() == 3) {
                                if (t.getSubLayerAdd() != null) {
                                    if (t.getSubLayerAdd() == 1) {
                                        fs.setSubLayerAdd(1);
                                    } else {
                                        fs.setSubLayerAdd(3);
                                    }
                                }
                            } else {
                                if (t.getSubLayerAdd() != null) {
                                    if (t.getSubLayerAdd() == 1) {
                                        if (isAdministrator) {
                                            fs.setSubLayerAdd(1);
                                        } else {
                                            fs.setSubLayerAdd(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getSubLayerAdd() != null) {
                                if (t.getSubLayerAdd() == 1) {
                                    if (isAdministrator) {
                                        fs.setSubLayerAdd(1);
                                    } else {
                                        fs.setSubLayerAdd(2);
                                    }
                                }
                            }
                        }

                        if (fs.getSubLayerEdit() != null) {
                            if (fs.getSubLayerEdit() == 0) {
                                if (t.getSubLayerEdit() != null) {
                                    if (t.getSubLayerEdit() == 1) {
                                        fs.setSubLayerEdit(1);
                                    } else {
                                        fs.setSubLayerEdit(0);
                                    }
                                }
                            } else if (fs.getSubLayerEdit() == 3) {
                                if (t.getSubLayerEdit() != null) {
                                    if (t.getSubLayerEdit() == 1) {
                                        fs.setSubLayerEdit(1);
                                    } else {
                                        fs.setSubLayerEdit(3);
                                    }
                                }
                            } else {
                                if (t.getSubLayerEdit() != null) {
                                    if (t.getSubLayerEdit() == 1) {
                                        if (isAdministrator) {
                                            fs.setSubLayerEdit(1);
                                        } else {
                                            fs.setSubLayerEdit(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getSubLayerEdit() != null) {
                                if (t.getSubLayerEdit() == 1) {
                                    if (isAdministrator) {
                                        fs.setSubLayerEdit(1);
                                    } else {
                                        fs.setSubLayerEdit(2);
                                    }
                                }
                            }
                        }

                        if (fs.getSubLayerDelete() != null) {
                            if (fs.getSubLayerDelete() == 0) {
                                if (t.getSubLayerDelete() != null) {
                                    if (t.getSubLayerDelete() == 1) {
                                        fs.setSubLayerDelete(1);
                                    } else {
                                        fs.setSubLayerDelete(0);
                                    }
                                }
                            } else if (fs.getSubLayerDelete() == 3) {
                                if (t.getSubLayerDelete() != null) {
                                    if (t.getSubLayerDelete() == 1) {
                                        fs.setSubLayerDelete(1);
                                    } else {
                                        fs.setSubLayerDelete(3);
                                    }
                                }
                            } else {
                                if (t.getSubLayerDelete() != null) {
                                    if (t.getSubLayerDelete() == 1) {
                                        if (isAdministrator) {
                                            fs.setSubLayerDelete(1);
                                        } else {
                                            fs.setSubLayerDelete(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getSubLayerDelete() != null) {
                                if (t.getSubLayerDelete() == 1) {
                                    if (isAdministrator) {
                                        fs.setSubLayerDelete(1);
                                    } else {
                                        fs.setSubLayerDelete(2);
                                    }
                                }
                            }
                        }

                        if (fs.getSubLayerSelect() != null) {
                            if (fs.getSubLayerSelect() == 0) {
                                if (t.getSubLayerSelect() != null) {
                                    if (t.getSubLayerSelect() == 1) {
                                        fs.setSubLayerSelect(1);
                                    } else {
                                        fs.setSubLayerSelect(0);
                                    }
                                }
                            } else if (fs.getSubLayerSelect() == 3) {
                                if (t.getSubLayerSelect() != null) {
                                    if (t.getSubLayerSelect() == 1) {
                                        fs.setSubLayerSelect(1);
                                    } else {
                                        fs.setSubLayerSelect(3);
                                    }
                                }
                            } else {
                                if (t.getSubLayerSelect() != null) {
                                    if (t.getSubLayerSelect() == 1) {
                                        if (isAdministrator) {
                                            fs.setSubLayerSelect(1);
                                        } else {
                                            fs.setSubLayerSelect(2);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (t.getSubLayerSelect() != null) {
                                if (t.getSubLayerSelect() == 1) {
                                    if (isAdministrator) {
                                        fs.setSubLayerSelect(1);
                                    } else {
                                        fs.setSubLayerSelect(2);
                                    }
                                }
                            }
                        }


                    }
                });
            });
            selectorModels = finalSelectorModels1;

            // 系统
            List<SysOrganizeAdministratorEntity> systemOrganizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userId, PermissionConst.SYSTEM, true);
            List<String> systemPermissionIds = systemOrganizeAdministratorEntity.stream().map(SysOrganizeAdministratorEntity::getOrganizeId).collect(Collectors.toList());
            List<String> collect = systemSelectorVOList.stream().map(SystemSelectorVO::getId).collect(Collectors.toList());
            List<String> systemList = new ArrayList<>();
            systemList.addAll(systemPermissionIds);
            systemList.addAll(collect);
            // 交集  1
            List<String> collect1 = systemPermissionIds.stream().filter(collect::contains).collect(Collectors.toList());
            systemList = systemList.stream().distinct().collect(Collectors.toList());
            // 去掉交集后
            systemList.removeAll(collect1);
            // 我有他没有 0
            List<String> collect2 = collect.stream().filter(systemList::contains).collect(Collectors.toList());
            // 他有我没有 2
            List<String> collect3 = systemPermissionIds.stream().filter(systemList::contains).collect(Collectors.toList());
            List<SysSystemEntity> listByIds1 = systemService.getListByIds(collect1, null);
            List<SystemSelectorVO> systemSelectorVOList1 = JsonUtil.createJsonToList(listByIds1, SystemSelectorVO.class);
            systemSelectorVOList1.forEach(t -> t.setIsPermission(1));
            List<SysSystemEntity> listByIds2 = systemService.getListByIds(collect2, null);
            List<SystemSelectorVO> systemSelectorVOList2 = JsonUtil.createJsonToList(listByIds2, SystemSelectorVO.class);
            systemSelectorVOList2.forEach(t -> t.setIsPermission(0));
            List<SysSystemEntity> listByIds3 = systemService.getListByIds(collect3, null);
            List<SystemSelectorVO> systemSelectorVOList3 = JsonUtil.createJsonToList(listByIds3, SystemSelectorVO.class);
            systemSelectorVOList3.forEach(t -> {
                t.setIsPermission(2);
                t.setDisabled(true);
            });
            systemSelectedList.addAll(systemSelectorVOList1.stream().map(SystemSelectorVO::getId).collect(Collectors.toList()));
            systemSelectedList.addAll(systemSelectorVOList3.stream().map(SystemSelectorVO::getId).collect(Collectors.toList()));

            systemPermissionList.addAll(systemSelectorVOList1);
            systemPermissionList.addAll(systemSelectorVOList2);
            systemPermissionList.addAll(systemSelectorVOList3);
            systemPermissionList = systemPermissionList.stream().sorted(Comparator.comparing(SystemSelectorVO::getSortCode).thenComparing(Comparator.comparing(SystemSelectorVO::getCreatorTime).reversed())).collect(Collectors.toList());


            // 菜单
            List<SysOrganizeAdministratorEntity> moduleOrganizeAdministratorEntity = organizeAdminIsTratorService.getOrganizeAdministratorEntity(userId, PermissionConst.MODULE, false);
            List<String> modulePermissionIds = moduleOrganizeAdministratorEntity.stream().map(SysOrganizeAdministratorEntity::getOrganizeId).collect(Collectors.toList());
            List<String> moduleCollect = moduleSelectorVOList.stream().map(ModuleSelectorModel::getId).collect(Collectors.toList());
            List<String> moduleList = new ArrayList<>();
            moduleList.addAll(modulePermissionIds);
            moduleList.addAll(moduleCollect);
            // 交集  1
            List<String> moduleCollect1 = modulePermissionIds.stream().filter(moduleCollect::contains).collect(Collectors.toList());
            moduleList = moduleList.stream().distinct().collect(Collectors.toList());
            // 去掉交集后
            moduleList.removeAll(moduleCollect1);
            // 我有他没有 0
            List<String> moduleCollect2 = moduleCollect.stream().filter(moduleList::contains).collect(Collectors.toList());
            // 他有我没有 2
            List<String> moduleCollect3 = modulePermissionIds.stream().filter(moduleList::contains).collect(Collectors.toList());
            List<ModuleEntity> moduleListByIds1 = moduleService.getModuleByIds(moduleCollect1, null, null, false);
            List<ModuleSelectorModel> moduleSelectorVOList1 = JsonUtil.createJsonToList(moduleListByIds1, ModuleSelectorModel.class);
            moduleSelectorVOList1.forEach(t -> t.setIsPermission(1));
            List<ModuleEntity> moduleListByIds2 = moduleService.getModuleByIds(moduleCollect2, null, null, false);
            List<ModuleSelectorModel> moduleSelectorVOList2 = JsonUtil.createJsonToList(moduleListByIds2, ModuleSelectorModel.class);
            moduleSelectorVOList2.forEach(t -> t.setIsPermission(0));
            List<ModuleEntity> moduleListByIds3 = moduleService.getModuleByIds(moduleCollect3, null, null, false);
            List<ModuleSelectorModel> moduleSelectorVOList3 = JsonUtil.createJsonToList(moduleListByIds3, ModuleSelectorModel.class);
            moduleSelectorVOList3.forEach(t -> {
                t.setIsPermission(2);
                t.setDisabled(true);
            });
            modulePermissionList.addAll(moduleSelectorVOList1);
            modulePermissionList.addAll(moduleSelectorVOList2);
            modulePermissionList.addAll(moduleSelectorVOList3);
            Set<ModuleSelectorModel> tempModule = new HashSet<>(modulePermissionList);
            // 处理上级
            List<ModuleSelectorModel> finalModulePermissionList = modulePermissionList;
            modulePermissionList.forEach(t -> {
                // 判断上级是否存在，不存在的话取出放入总权限列表
                parentIdInList(finalModulePermissionList, tempModule, allModuleSelectorVOList, t, t);
            });
            modulePermissionList = new ArrayList<>(tempModule);
            modulePermissionList.forEach(t -> {
                if ("-1".equals(t.getParentId())) {
                    t.setParentId(t.getSystemId());
                }
            });
            // 加入主系统
            ModuleSelectorModel jsonToBean = BeanUtil.toBean(mainSystem, ModuleSelectorModel.class);
            if (moduleSelectorVOList1.stream().anyMatch(t -> t.getId().equals(jsonToBean.getId()))) {
                jsonToBean.setIsPermission(1);
            } else if (moduleSelectorVOList2.stream().anyMatch(t -> t.getId().equals(jsonToBean.getId()))) {
                jsonToBean.setIsPermission(0);
            } else if (moduleSelectorVOList3.stream().anyMatch(t -> t.getId().equals(jsonToBean.getId()))) {
                jsonToBean.setIsPermission(2);
                jsonToBean.setDisabled(true);
            }
            moduleSelectedList.addAll(moduleSelectorVOList1.stream().map(ModuleSelectorModel::getId).collect(Collectors.toList()));
            moduleSelectedList.addAll(moduleSelectorVOList3.stream().map(ModuleSelectorModel::getId).collect(Collectors.toList()));

            jsonToBean.setType(0);
            jsonToBean.setParentId("-1");
            modulePermissionList.add(jsonToBean);
            modulePermissionList = modulePermissionList.stream().sorted(Comparator.comparing(ModuleSelectorModel::getSortCode).thenComparing(Comparator.comparing(ModuleSelectorModel::getCreatorTime).reversed())).collect(Collectors.toList());
            List<SumTree<ModuleSelectorModel>> menuList = TreeDotUtils.convertListToTreeDotFilter(modulePermissionList);
            moduleVOPermissionList = JsonUtil.createJsonToList(menuList, ModuleSelectorVO.class);
        }

        // 判断断层有没有上下级关系
        Map<String, OrganizeAdministratorSelectorModel> selectorModelMap = selectorModels.stream().collect(Collectors.toMap(OrganizeAdministratorSelectorModel::getId, Function.identity()));

        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        selectorModels.forEach(t -> {
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                List<String> list1 = new ArrayList<>();
                String[] split = t.getOrganizeIdTree().split(",");
                list1 = Arrays.asList(split);
                Collections.reverse(list1);
                for (String orgId : list1) {
                    OrganizeAdministratorSelectorModel organizeEntity1 = selectorModelMap.get(orgId);
                    if (organizeEntity1 != null && !organizeEntity1.getId().equals(t.getId())) {
                        t.setParentId(organizeEntity1.getId());
                        String[] split1 = t.getOrganizeIdTree().split(organizeEntity1.getId());
                        if (split1.length > 1) {
                            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                        }
                        break;
                    }
                }
            }
        });
        selectorModels.forEach(t -> t.setIcon(StringUtil.isNotEmpty(t.getCategory()) ? "company".equals(t.getCategory()) ? "icon-linzen icon-linzen-tree-organization3" : "icon-linzen icon-linzen-tree-department1" : ""));
        List<SumTree<OrganizeAdministratorSelectorModel>> trees = TreeDotUtils.convertListToTreeDot(selectorModels);
        List<OrganizeAdministratorSelectorVO> listVO = JsonUtil.createJsonToList(trees, OrganizeAdministratorSelectorVO.class);
        listVO.forEach(t -> {
            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
        });
        vo.setOrgAdminList(listVO);
        vo.setSystemPermissionList(systemPermissionList);
        vo.setModulePermissionList(moduleVOPermissionList);
        vo.setSystemIds(systemSelectedList);
        vo.setModuleIds(moduleSelectedList);
        return ServiceResult.success(vo);
    }

    private void parentIdInList(List<ModuleSelectorModel> modulePermissionList, Set<ModuleSelectorModel> tempModule,
                                List<ModuleSelectorModel> allModuleSelectorVOList, ModuleSelectorModel moduleSelectorModel,
                                ModuleSelectorModel moduleSelectorModels) {
        ModuleSelectorModel finalModuleSelectorModel = moduleSelectorModel;
        moduleSelectorModel = modulePermissionList.stream().filter(t -> finalModuleSelectorModel.getParentId().equals(t.getId())).findFirst().orElse(null);
        // !null return  ==1 return
        if (moduleSelectorModel != null && moduleSelectorModel.getType() == 1) {
            tempModule.add(moduleSelectorModel);
            moduleSelectorModels.setParentId(moduleSelectorModel.getId());
            return;
        }
        moduleSelectorModel = allModuleSelectorVOList.stream().filter(t -> finalModuleSelectorModel.getParentId().equals(t.getId())).findFirst().orElse(null);
        if (moduleSelectorModel == null) {
            return;
        }
        parentIdInList(modulePermissionList, tempModule, allModuleSelectorVOList, moduleSelectorModel, moduleSelectorModels);
    }
}
