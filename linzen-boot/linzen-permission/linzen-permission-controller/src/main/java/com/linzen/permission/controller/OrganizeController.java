package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.linzen.annotation.OrganizePermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.exception.DataBaseException;
import com.linzen.message.service.SynThirdDingTalkService;
import com.linzen.message.service.SynThirdQyService;
import com.linzen.permission.entity.SysOrganizeAdministratorEntity;
import com.linzen.permission.entity.SysOrganizeEntity;
import com.linzen.permission.model.organize.*;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.OrganizeRelationService;
import com.linzen.permission.service.OrganizeService;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import com.linzen.util.treeutil.ListToTreeUtil;
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
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 组织机构
 * 组织架构：公司》部门》岗位》用户
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "组织管理", description = "Organize")
@RestController
@RequestMapping("/api/permission/Organize")
@Slf4j
public class OrganizeController extends SuperController<OrganizeService, SysOrganizeEntity> {

    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SynThirdQyService synThirdQyService;
    @Autowired
    private SynThirdDingTalkService synThirdDingTalkService;
    /**
     * 取出线程池
     */
    @Autowired
    private Executor threadPoolExecutor;
    @Autowired
    private OrganizeAdministratorService organizeAdministratorService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeRelationService organizeRelationService;

    //---------------------------组织管理--------------------------------------------

    /**
     * 获取组织列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取组织列表")
    @SaCheckPermission(value = {"permission.organize", "permission.position", "permission.user", "permission.role"}, mode = SaMode.OR)
    @GetMapping
    public ServiceResult<ListVO<OrganizeListVO>> getList(PaginationOrganize pagination) {
        // 获取所有组织
        Map<String, SysOrganizeEntity> orgMaps;
        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            Set<String> orgIds = new HashSet<>(16);
            // 判断自己是哪些组织的管理员
            listss.stream().forEach(t-> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgIds.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), Objects.equals(pagination.getEnabledMark(), 1));
                        orgIds.addAll(underOrganizations);
                    }
                }
            });
            List<String> list1 = new ArrayList<>(orgIds);
            // 得到所有有权限的组织
            orgMaps = organizeService.getOrganizeName(list1, pagination.getKeyword(), Objects.equals(pagination.getEnabledMark(), 1), pagination.getType());
        }else{
            orgMaps = organizeService.getOrgMaps(pagination.getKeyword(), Objects.equals(pagination.getEnabledMark(), 1), pagination.getType());
        }
        Map<String, OrganizeModel> orgMapsModel = JSONObject.parseObject(JSONObject.toJSONString(orgMaps), new TypeReference<LinkedHashMap<String, OrganizeModel>>() {}, new Feature[0]);;

        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        orgMapsModel.values().forEach(t -> {
            if (PermissionConst.COMPANY.equals(t.getType())) {
                t.setIcon("icon-linzen icon-linzen-tree-organization3");
            } else {
                t.setIcon("icon-linzen icon-linzen-tree-department1");
            }
            // 处理断层
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                String[] split = t.getOrganizeIdTree().split(",");
                List<String> list1 = Arrays.asList(split);
                Collections.reverse(list1);
                for (String orgId : list1) {
                    if(!orgId.equals(t.getId())) {
                        OrganizeModel organizeEntity1 = orgMapsModel.get(orgId);
                        if (organizeEntity1 != null) {
                            t.setParentId(organizeEntity1.getId());
                            String[] split1 = t.getOrganizeIdTree().split(organizeEntity1.getId());
                            if (split1.length > 1) {
                                t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                            }
                            break;
                        }
                    }
                }
            }
        });
        List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertMapsToTreeDot(orgMapsModel);
        List<OrganizeListVO> listVO = JsonUtil.createJsonToList(trees, OrganizeListVO.class);
        listVO.forEach(t -> {
            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
        });
        ListVO<OrganizeListVO> vo = new ListVO<>();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }


    /**
     * 获取组织下拉框列表
     *
     * @param pagination 分页模型
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取组织下拉框列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/Selector/{id}")
    public ServiceResult<ListVO<OrganizeSelectorVO>> getSelector(Pagination pagination, @PathVariable("id") String id) {
        List<SysOrganizeEntity> allList = new LinkedList<>(organizeService.getOrgMaps(pagination.getKeyword(), true, null).values());
        if (!"0".equals(id)) {
            allList.remove(organizeService.getInfo(id));
        }
        List<SysOrganizeEntity> dataAll = allList;
        List<SysOrganizeEntity> list = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(allList, dataAll), SysOrganizeEntity.class);
        list = list.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        List<OrganizeModel> models = JsonUtil.createJsonToList(list, OrganizeModel.class);
        for (OrganizeModel model : models) {
            model.setIcon("icon-linzen icon-linzen-tree-organization3");
        }
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        models.forEach(t -> {
            t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                String[] split = t.getOrganizeIdTree().split(",");
                if (split.length > 0) {
                    t.setOrganizeIds(Arrays.asList(split));
                } else {
                    t.setOrganizeIds(new ArrayList<>());
                }
            }
        });

        List<OrganizeModel> modelAll = new ArrayList<>();
        modelAll.addAll(models);
        List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDotFilter(modelAll);
        List<OrganizeSelectorVO> listVO = JsonUtil.createJsonToList(trees, OrganizeSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }


    /**
     * 获取组织下拉框列表
     *
     * @param pagination 分页模型
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取组织下拉框列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/SelectorByAuth/{id}")
    public ServiceResult<ListVO<OrganizeSelectorByAuthVO>> getSelectorByAuth(Pagination pagination, @PathVariable("id") String id) {
        List<SysOrganizeEntity> allList = new LinkedList<>(organizeService.getOrgMaps(pagination.getKeyword(), true, null).values());
        allList = allList.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        SysOrganizeEntity entity = organizeService.getInfo(id);
        List<SysOrganizeEntity> dataAll = allList;

        List<SysOrganizeEntity> list = JsonUtil.createJsonToList(ListToTreeUtil.treeWhere(allList, dataAll), SysOrganizeEntity.class);

        List<OrganizeByAuthModel> models = JsonUtil.createJsonToList(list, OrganizeByAuthModel.class);

        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            Set<String> orgIds = new HashSet<>(16);
            // 判断自己是哪些组织的管理员
            listss.stream().forEach(t-> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgIds.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        orgIds.addAll(underOrganizations);
                    }
                }
            });
            List<String> list1 = new ArrayList<>(orgIds);
            // 得到所有有权限的组织
            List<SysOrganizeEntity> organizeName = organizeService.getOrganizeName(list1);
            organizeName = organizeName.stream().filter(t->PermissionConst.COMPANY.equals(t.getCategory())).collect(Collectors.toList());
            models = JsonUtil.createJsonToList(organizeName, OrganizeByAuthModel.class);
        }

        // 判断当前编辑的权限时候是否有上级
        if (entity != null) {
            if (models.stream().filter(t -> t.getId().equals(entity.getParentId())).findFirst().orElse(null) == null) {
                SysOrganizeEntity info = organizeService.getInfo(entity.getParentId());
                if (info != null) {
                    OrganizeByAuthModel jsonToBean = BeanUtil.toBean(info, OrganizeByAuthModel.class);
                    jsonToBean.setDisabled(true);
                    models.add(jsonToBean);
                }
            }
        }
        List<OrganizeByAuthModel> finalModels = models;
        models.forEach(t -> {
            if (PermissionConst.COMPANY.equals(t.getType())) {
                t.setIcon("icon-linzen icon-linzen-tree-organization3");
            } else {
                t.setIcon("icon-linzen icon-linzen-tree-department1");
            }
            // 处理断层
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                String[] split = t.getOrganizeIdTree().split(",");
                List<String> list1 = Arrays.asList(split);
                t.setOrganizeIds(list1);
                t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
                List<String> list2 = new ArrayList<>(list1);
                Collections.reverse(list2);
                for (String orgId : list2) {
                    OrganizeModel organizeEntity1 = finalModels.stream().filter(organizeEntity -> organizeEntity.getId().equals(orgId)).findFirst().orElse(null);
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
        List<SumTree<OrganizeByAuthModel>> trees = TreeDotUtils.convertListToTreeDot(models);
        List<OrganizeSelectorByAuthVO> listVO = JsonUtil.createJsonToList(trees, OrganizeSelectorByAuthVO.class);
        listVO.forEach(t -> {
            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
        });
        ListVO<OrganizeSelectorByAuthVO> vo = new ListVO<>();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 通过部门id获取部门下拉框下拉框
     *
     * @return
     */
    @Operation(summary = "通过部门id获取部门下拉框")
    @Parameters({
            @Parameter(name = "organizeConditionModel", description = "组织id模型", required = true)
    })
    @PostMapping("/OrganizeCondition")
    public ServiceResult<ListVO<OrganizeListVO>> organizeCondition(@RequestBody OrganizeConditionModel organizeConditionModel) {
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        organizeConditionModel.setOrgIdNameMaps(orgIdNameMaps);
        List<OrganizeModel> organizeList = organizeRelationService.getOrgIdsList(organizeConditionModel);
        List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDot(organizeList);
        List<OrganizeListVO> listVO = JsonUtil.createJsonToList(trees, OrganizeListVO.class);
        listVO.forEach(t -> {
            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
        });
        ListVO<OrganizeListVO> vo = new ListVO<>();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 组织树形
     *
     * @return
     */
    @Operation(summary = "获取组织/公司树形")
    @GetMapping("/Tree")
    public ServiceResult<ListVO<OrganizeTreeVO>> tree() {
        List<SysOrganizeEntity> list = new LinkedList<>(organizeService.getOrgMaps(null, true, null).values());
        list = list.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        List<OrganizeModel> models = JsonUtil.createJsonToList(list, OrganizeModel.class);
        for (OrganizeModel model : models) {
            model.setIcon("icon-linzen icon-linzen-tree-organization3");
        }
        List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDot(models);
        List<OrganizeTreeVO> listVO = JsonUtil.createJsonToList(trees, OrganizeTreeVO.class);
        //将子节点全部删除
        Iterator<OrganizeTreeVO> iterator = listVO.iterator();
        while (iterator.hasNext()) {
            OrganizeTreeVO orananizeTreeVO = iterator.next();
            if (!"-1".equals(orananizeTreeVO.getParentId())) {
                iterator.remove();
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 获取组织信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取组织信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @GetMapping("/{id}")
    public ServiceResult<OrganizeInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        SysOrganizeEntity entity = organizeService.getInfo(id);
        OrganizeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, OrganizeInfoVO.class);
        if (StringUtil.isNotEmpty(entity.getOrganizeIdTree())) {
            String replace = entity.getOrganizeIdTree().replace(entity.getId(), "");
            if (StringUtil.isNotEmpty(replace) && !",".equals(replace)) {
                vo.setOrganizeIdTree(Arrays.asList(replace.split(",")));
            } else {
                vo.setOrganizeIdTree(Arrays.asList(new String[]{"-1"}));
            }
        }
        return ServiceResult.success(vo);
    }


    /**
     * 新建组织
     *
     * @param organizeCrForm 新建模型
     * @return
     */
    @OrganizePermission
    @Operation(summary = "新建组织")
    @Parameters({
            @Parameter(name = "organizeCrForm", description = "新建模型", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PostMapping
    public ServiceResult create(@RequestBody @Valid OrganizeCreateForm organizeCrForm) {
        SysOrganizeEntity entity = BeanUtil.toBean(organizeCrForm, SysOrganizeEntity.class);
        entity.setCategory("company");
        if (organizeService.isExistByFullName(entity, false, false)) {
            return ServiceResult.error("公司名称不能重复");
        }
        if (organizeService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("公司编码不能重复");
        }

        // 通过组织id获取父级组织
        String organizeIdTree = getOrganizeIdTree(entity);
        entity.setOrganizeIdTree(organizeIdTree);

        organizeService.create(entity);
        threadPoolExecutor.execute(() -> {
            try{
                //创建组织后判断是否需要同步到企业微信
                synThirdQyService.createDepartmentSysToQy(false, entity, "");
                //创建组织后判断是否需要同步到钉钉
                synThirdDingTalkService.createDepartmentSysToDing(false, entity, "");
            } catch (Exception e) {
                log.error("创建组织后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
            }
        });
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新组织
     *
     * @param id              主键值
     * @param organizeUpForm 实体对象
     * @return
     */
    @OrganizePermission
    @Operation(summary = "更新组织")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "organizeUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid OrganizeUpForm organizeUpForm) {
        List<SysOrganizeEntity> synList = new ArrayList<>();
        SysOrganizeEntity entity = BeanUtil.toBean(organizeUpForm, SysOrganizeEntity.class);
        SysOrganizeEntity info = organizeService.getInfo(organizeUpForm.getParentId());
        if (id.equals(entity.getParentId()) || (info != null && info.getOrganizeIdTree() != null && info.getOrganizeIdTree().contains(id))) {
            return ServiceResult.error("当前机构Id不能与父机构Id相同");
        }
        entity.setId(id);
        entity.setCategory("company");
        if (organizeService.isExistByFullName(entity, false, true)) {
            return ServiceResult.error("公司名称不能重复");
        }
        if (organizeService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("公司编码不能重复");
        }
        // 通过组织id获取父级组织
        String organizeIdTree = getOrganizeIdTree(entity);
        entity.setOrganizeIdTree(organizeIdTree);
        boolean flag = organizeService.update(id, entity);
        synList.add(entity);

        // 得到所有子组织或部门id
        if (info != null && info.getParentId() != null && !entity.getParentId().equals(info.getParentId())) {
            List<String> underOrganizations = organizeService.getUnderOrganizations(id, false);
            underOrganizations.forEach(t -> {
                SysOrganizeEntity info1 = organizeService.getInfo(t);
                if (StringUtil.isNotEmpty(info1.getOrganizeIdTree())) {
                    String organizeIdTrees = getOrganizeIdTree(info1);
                    info1.setOrganizeIdTree(organizeIdTrees);
                    organizeService.update(info1.getId(), info1);
                    synList.add(info1);
                }
            });
        }
        threadPoolExecutor.execute(() -> {
            synList.forEach(t-> {
                try{
                    //修改组织后判断是否需要同步到企业微信
                    synThirdQyService.updateDepartmentSysToQy(false, t, "");
                    //修改组织后判断是否需要同步到钉钉
                    synThirdDingTalkService.updateDepartmentSysToDing(false, t, "");
                } catch (Exception e) {
                    log.error("修改组织后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
                }
            });
        });
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除组织
     *
     * @param orgId 组织主键
     * @return
     */
    @OrganizePermission
    @Operation(summary = "删除组织")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String orgId) {
        return organizeService.delete(orgId);
    }

    /**
     * 删除部门
     *
     * @param orgId 部门主键
     * @return
     */
    @OrganizePermission
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @Operation(summary = "删除部门")
    @DeleteMapping("/Department/{id}")
    public ServiceResult<String> deleteDepartment(@PathVariable("id") String orgId) {
        return organizeService.delete(orgId);
    }

    /**
     * 更新组织状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新组织状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PutMapping("/{id}/Actions/State")
    public ServiceResult update(@PathVariable("id") String id) {
        SysOrganizeEntity organizeEntity = organizeService.getInfo(id);
        if (organizeEntity != null) {
            if ("1".equals(String.valueOf(organizeEntity.getEnabledMark()))) {
                organizeEntity.setEnabledMark(0);
            } else {
                organizeEntity.setEnabledMark(1);
            }
            organizeService.update(organizeEntity.getId(), organizeEntity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.success(MsgCode.FA002.get());
    }


    //---------------------------部门管理--------------------------------------------

//    /**
//     * 获取部门列表
//     *
//     * @param companyId 组织id
//     * @param pagination 分页模型
//     * @return
//    @Operation(summary = "获取部门列表")
//    @Parameters({
//            @Parameter(name = "companyId", description = "组织id", required = true)
//    })
//    @SaCheckPermission(value = {"permission.organize"})
//    @GetMapping("/{companyId}/Department")
//    public ServiceResult<ListVO<OrganizeDepartListVO>> getListDepartment(@PathVariable("companyId") String companyId, Pagination pagination) {
//        List<OrganizeEntity> dataAll = organizeService.getParentIdList(companyId);
//        List<String> childId = dataAll.stream().map(t -> t.getId()).collect(Collectors.toList());
//        List<OrganizeEntity> data = organizeService.getListAll(childId, pagination.getKeyword());
//        //正序显示
//        data = data.stream().sorted(Comparator.comparing(OrganizeEntity::getSortCode)).collect(Collectors.toList());
//        List<OrganizeModel> models = JsonUtil.createJsonToList(data, OrganizeModel.class);
//        if (!userProvider.get().getIsAdministrator()) {
//            // 通过权限转树
//            List<OrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
//            Set<String> orgIds = new HashSet<>(16);
//            // 判断自己是哪些组织的管理员
//            listss.stream().forEach(t-> {
//                if (t != null) {
//                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
//                        orgIds.add(t.getOrganizeId());
//                    }
//                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
//                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId());
//                        orgIds.addAll(underOrganizations);
//                    }
//                }
//            });
//            List<String> list1 = new ArrayList<>(orgIds);
//            List<OrganizeModel> organizeModels = new ArrayList<>(16);
//
//            models.forEach(t -> {
//                list1.forEach(tt -> {
//                    if (t.getId() != null && t.getId().equals(tt)) {
//                        organizeModels.add(t);
//                    }
//                });
//            });
//            models = organizeModels;
//        }
//        // 给部门经理赋值
//        for (OrganizeModel model : models) {
//            if (!StringUtil.isEmpty(model.getManager())) {
//                UserEntity entity = userService.getById(model.getManager());
//                model.setManager(entity != null ? entity.getRealName() + "/" + entity.getAccount() : null);
//            }
//        }
//        List<OrganizeDepartListVO> listvo = JsonUtil.createJsonToList(models, OrganizeDepartListVO.class);
//        ListVO vo = new ListVO();
//        vo.setList(listvo);
//        return ServiceResult.success(vo);
//    }



    /**
     * 获取部门下拉框列表
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取部门下拉框列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/Department/Selector/{id}")
    public ServiceResult<ListVO<OrganizeDepartSelectorListVO>> getListDepartment(@PathVariable("id") String id) {
        List<SysOrganizeEntity> data = new LinkedList<>(organizeService.getOrgMaps(null, true, null).values());
        if (!"0".equals(id)) {
            data.remove(organizeService.getInfo(id));
        }
        List<OrganizeModel> models = JsonUtil.createJsonToList(data, OrganizeModel.class);
        for (OrganizeModel model : models) {
            if ("department".equals(model.getType())) {
                model.setIcon("icon-linzen icon-linzen-tree-department1");
            } else if ("company".equals(model.getType())) {
                model.setIcon("icon-linzen icon-linzen-tree-organization3");
            }
        }

        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        models.forEach(t -> {
            t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                String[] split = t.getOrganizeIdTree().split(",");
                if (split.length > 0) {
                    t.setOrganizeIds(Arrays.asList(split));
                } else {
                    t.setOrganizeIds(new ArrayList<>());
                }
            }
        });

        List<SumTree<OrganizeModel>> trees = TreeDotUtils.convertListToTreeDotFilter(models);
        List<OrganizeDepartSelectorListVO> listVO = JsonUtil.createJsonToList(trees, OrganizeDepartSelectorListVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 获取部门下拉框列表
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取部门下拉框列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/Department/SelectorByAuth/{id}")
    public ServiceResult<ListVO<OrganizeSelectorByAuthVO>> getDepartmentSelectorByAuth(@PathVariable("id") String id) {
        Map<String, SysOrganizeEntity> orgMaps;
        SysOrganizeEntity entity = organizeService.getInfo(id);

        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            Set<String> orgIds = new HashSet<>(16);
            // 判断自己是哪些组织的管理员
            listss.stream().forEach(t-> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgIds.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        orgIds.addAll(underOrganizations);
                    }
                }
            });
            List<String> list1 = new ArrayList<>(orgIds);
            orgMaps = organizeService.getOrganizeName(list1, null, true, null);
        } else {
            orgMaps = organizeService.getOrgMaps(null, true, null);
        }
        Map<String, OrganizeByAuthModel> orgMapsModel = JSONObject.parseObject(JSONObject.toJSONString(orgMaps), new TypeReference<LinkedHashMap<String, OrganizeByAuthModel>>() {}, new Feature[0]);;
        if (!"0".equals(id)) {
            orgMapsModel.remove(id);
        }
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        // 判断当前编辑的权限时候是否有上级
        if (entity != null) {
            if (orgMapsModel.values().stream().filter(t -> t.getId().equals(entity.getParentId())).findFirst().orElse(null) == null) {
                SysOrganizeEntity info = organizeService.getInfo(entity.getParentId());
                if (info != null) {
                    OrganizeByAuthModel jsonToBean = BeanUtil.toBean(info, OrganizeByAuthModel.class);
                    jsonToBean.setDisabled(true);
                    orgMapsModel.put(info.getId(), jsonToBean);
                }
            }
        }
        orgMapsModel.values().forEach(t -> {
            if (PermissionConst.COMPANY.equals(t.getType())) {
                t.setIcon("icon-linzen icon-linzen-tree-organization3");
            } else {
                t.setIcon("icon-linzen icon-linzen-tree-department1");
            }
            // 处理断层
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                List<String> list1 = new ArrayList<>();
                String[] split = t.getOrganizeIdTree().split(",");
                list1 = Arrays.asList(split);
                List<String> list = new ArrayList<>(16);
                list1.forEach(orgId -> {
                    if (StringUtil.isNotEmpty(orgId)) {
                        list.add(orgId);
                    }
                });
                t.setOrganizeIds(list);
                t.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
                Collections.reverse(list1);
                for (String orgId : list1) {
                    OrganizeModel organizeEntity1 = orgMapsModel.get(orgId);
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
        List<SumTree<OrganizeByAuthModel>> trees = TreeDotUtils.convertMapsToTreeDot(orgMapsModel);
        List<OrganizeSelectorByAuthVO> listVO = JsonUtil.createJsonToList(trees, OrganizeSelectorByAuthVO.class);
        listVO.forEach(t -> {
            t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
        });
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }


    /**
     * 新建部门
     *
     * @param organizeDepartCrForm 新建模型
     * @return
     */
    @OrganizePermission
    @Operation(summary = "新建部门")
    @Parameters({
            @Parameter(name = "organizeDepartCrForm", description = "新建模型", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PostMapping("/Department")
    public ServiceResult createDepartment(@RequestBody @Valid OrganizeDepartCreateForm organizeDepartCrForm) {
        SysOrganizeEntity entity = BeanUtil.toBean(organizeDepartCrForm, SysOrganizeEntity.class);
        entity.setCategory("department");
        //判断同一个父级下是否含有同一个名称
        if (organizeService.isExistByFullName(entity, false, false)) {
            return ServiceResult.error("部门名称不能重复");
        }
        //判断同一个父级下是否含有同一个编码
        if (organizeService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("部门编码不能重复");
        }

        // 通过组织id获取父级组织
        String organizeIdTree = getOrganizeIdTree(entity);
        entity.setOrganizeIdTree(organizeIdTree);

        organizeService.create(entity);
        threadPoolExecutor.execute(() -> {
            try{
                //创建部门后判断是否需要同步到企业微信
                synThirdQyService.createDepartmentSysToQy(false, entity, "");
                //创建部门后判断是否需要同步到钉钉
                synThirdDingTalkService.createDepartmentSysToDing(false, entity, "");
            } catch (Exception e) {
                log.error("创建部门后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
            }
        });
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新部门
     *
     * @param id                    主键值
     * @param oraganizeDepartUpForm 修改模型
     * @return
     */
    @OrganizePermission
    @Operation(summary = "更新部门")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "oraganizeDepartUpForm", description = "修改模型", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PutMapping("/Department/{id}")
    public ServiceResult updateDepartment(@PathVariable("id") String id, @RequestBody @Valid OrganizeDepartUpForm oraganizeDepartUpForm) {
        List<SysOrganizeEntity> synList = new ArrayList<>();
        SysOrganizeEntity entity = BeanUtil.toBean(oraganizeDepartUpForm, SysOrganizeEntity.class);
        SysOrganizeEntity info = organizeService.getInfo(oraganizeDepartUpForm.getParentId());
        if (id.equals(entity.getParentId()) || (info != null && info.getOrganizeIdTree() != null && info.getOrganizeIdTree().contains(id))) {
            return ServiceResult.error("当前机构Id不能与父机构Id相同");
        }
        entity.setId(id);
        entity.setCategory("department");
        //判断同一个父级下是否含有同一个名称
        if (organizeService.isExistByFullName(entity, false, true)) {
            return ServiceResult.error("部门名称不能重复");
        }
        //判断同一个父级下是否含有同一个编码
        if (organizeService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error("部门编码不能重复");
        }
        // 通过组织id获取父级组织
        String organizeIdTree = getOrganizeIdTree(entity);
        entity.setOrganizeIdTree(organizeIdTree);
        boolean flag = organizeService.update(id, entity);
        synList.add(entity);

        // 得到所有子组织或部门id
        if (info.getParentId() != null && !entity.getParentId().equals(info.getParentId())) {
            List<String> underOrganizations = organizeService.getUnderOrganizations(id, false);
            underOrganizations.forEach(t -> {
                SysOrganizeEntity info1 = organizeService.getInfo(t);
                if (StringUtil.isNotEmpty(info1.getOrganizeIdTree())) {
                    String organizeIdTrees = getOrganizeIdTree(info1);
                    info1.setOrganizeIdTree(organizeIdTrees);
                    organizeService.update(info1.getId(), info1);
                    synList.add(info1);
                }
            });
        }

        threadPoolExecutor.execute(() -> {
            synList.forEach(t-> {
                try{
                    //修改部门后判断是否需要同步到企业微信
                    synThirdQyService.updateDepartmentSysToQy(false, organizeService.getInfo(id), "");
                    //修改部门后判断是否需要同步到钉钉
                    synThirdDingTalkService.updateDepartmentSysToDing(false, organizeService.getInfo(id), "");
                } catch (Exception e) {
                    log.error("修改部门后同步失败到企业微信或钉钉失败，异常：" + e.getMessage());
                }
            });
        });
        if (flag == false) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }



    /**
     * 更新部门状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新部门状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @PutMapping("/Department/{id}/Actions/State")
    public ServiceResult updateDepartment(@PathVariable("id") String id) {
        SysOrganizeEntity organizeEntity = organizeService.getInfo(id);
        if (organizeEntity != null) {
            if ("1".equals(String.valueOf(organizeEntity.getEnabledMark()))) {
                organizeEntity.setEnabledMark(0);
            } else {
                organizeEntity.setEnabledMark(1);
            }
            organizeService.update(organizeEntity.getId(), organizeEntity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error(MsgCode.FA002.get());
    }

    /**
     * 获取部门信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取部门信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission(value = {"permission.organize"})
    @GetMapping("/Department/{id}")
    public ServiceResult<OrganizeDepartInfoVO> infoDepartment(@PathVariable("id") String id) throws DataBaseException {
        SysOrganizeEntity entity = organizeService.getInfo(id);
        OrganizeDepartInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, OrganizeDepartInfoVO.class);
        List<String> list = new ArrayList<>();
        if (StringUtil.isNotEmpty(entity.getOrganizeIdTree())) {
            String[] split = entity.getOrganizeIdTree().split(",");
            if (split.length > 1) {
                for (int i = 0; i < split.length - 1; i++) {
                    list.add(split[i]);
                }
            }
        }
        vo.setOrganizeIdTree(list);
        return ServiceResult.success(vo);
    }

    /**
     * 获取父级组织id
     *
     * @param entity
     * @return
     */
    private String getOrganizeIdTree(SysOrganizeEntity entity) {
        List<String> list = new ArrayList<>();
        organizeService.getOrganizeIdTree(entity.getParentId(), list);
        // 倒叙排放
        Collections.reverse(list);
        StringBuilder organizeIdTree = new StringBuilder();
        for (String organizeParentId : list) {
            organizeIdTree.append("," + organizeParentId);
        }
        String organizeParentIdTree = organizeIdTree.toString();
        if (StringUtil.isNotEmpty(organizeParentIdTree)) {
            organizeParentIdTree = organizeParentIdTree.replaceFirst(",", "");
        }
        return organizeParentIdTree;
    }

//    /**
//     * 获取父级组织id
//     *
//     * @param entity
//     * @return
//     */
//    private String getOrganizeIdTrees(OrganizeEntity entity) {
//        List<String> list = new ArrayList<>();
//        organizeService.getOrganizeIdTree(entity.getId(), list);
//        // 倒叙排放
//        Collections.reverse(list);
//        StringBuffer organizeIdTree = new StringBuffer();
//        for (String organizeParentId : list) {
//            organizeIdTree.append("," + organizeParentId);
//        }
//        String organizeParentIdTree = organizeIdTree.toString();
//        if (StringUtil.isNotEmpty(organizeParentIdTree)) {
//            organizeParentIdTree = organizeParentIdTree.replaceFirst(",", "");
//        }
//        return organizeParentIdTree;
//    }
//
//    @GetMapping("/aaa")
//    public void aaa() {
//        List<OrganizeEntity> list = organizeService.getList();
//        list.forEach(t->{
//            String organizeIdTree = getOrganizeIdTrees(t);
//            t.setOrganizeIdTree(organizeIdTree);
//            organizeService.updateById(t);
//        });
//    }

    /**
     * 获取默认当前值部门ID
     *
     * @param organizeConditionModel 参数
     * @return 执行结构
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取默认当前值部门ID")
    @Parameters({
            @Parameter(name = "organizeConditionModel", description = "参数", required = true)
    })
    @PostMapping("/getDefaultCurrentValueDepartmentId")
    public ServiceResult<?> getDefaultCurrentValueDepartmentId(@RequestBody OrganizeConditionModel organizeConditionModel) throws DataBaseException {
        String departmentId = organizeService.getDefaultCurrentValueDepartmentId(organizeConditionModel);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("departmentId", departmentId);
        return ServiceResult.success("查询成功", dataMap);
    }

    // -----临时调用
    /**
     * 获取组织列表
     *
     * @param id 主键
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取组织列表")
    @SaCheckPermission(value = {"permission.organize", "permission.position", "permission.user", "permission.role"}, mode = SaMode.OR)
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/AsyncList/{id}")
    public ServiceResult<ListVO<OrganizeListVO>> getList(@PathVariable("id") String id, Pagination pagination) {
        ListVO<OrganizeListVO> vo = new ListVO<>();
        // 获取所有组织
        Map<String, SysOrganizeEntity> orgMaps;
        if (!userProvider.get().getIsAdministrator()) {
            // 通过权限转树
            List<SysOrganizeAdministratorEntity> listss = organizeAdministratorService.getOrganizeAdministratorEntity(userProvider.get().getUserId());
            Set<String> orgIds = new HashSet<>(16);
            // 判断自己是哪些组织的管理员
            listss.stream().forEach(t-> {
                if (t != null) {
                    if (t.getThisLayerSelect() != null && t.getThisLayerSelect() == 1) {
                        orgIds.add(t.getOrganizeId());
                    }
                    if (t.getSubLayerSelect() != null && t.getSubLayerSelect() == 1) {
                        List<String> underOrganizations = organizeService.getUnderOrganizations(t.getOrganizeId(), false);
                        orgIds.addAll(underOrganizations);
                    }
                }
            });
            List<String> list1 = new ArrayList<>(orgIds);
            // 得到所有有权限的组织
            orgMaps = organizeService.getOrganizeName(list1, null, false, null);
        } else {
            orgMaps = organizeService.getOrgMaps(null, false, null);
        }
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        List<SysOrganizeEntity> organizeEntityList = new ArrayList<>();
        SysOrganizeEntity parentEntity = null;
        if ("-1".equals(id)) {
            parentEntity = organizeService.getInfoByParentId(id);
            SysOrganizeEntity organizeEntity = orgMaps.get(parentEntity.getId());
            if (organizeEntity != null) {
                organizeEntityList.add(organizeEntity);
            }
        } else {
            parentEntity = organizeService.getInfo(id);
        }
        // 判断是否有顶级组织权限
        if (organizeEntityList.size() == 0) {
            List<SysOrganizeEntity> temOrganizeEntityList = new ArrayList<>();
            temOrganizeEntityList.add(parentEntity);
            getParentEntity(orgMaps, temOrganizeEntityList, organizeEntityList);
        }
        if (organizeEntityList .size() == 0) {
            vo.setList(new ArrayList<>());
            return ServiceResult.success(vo);
        }
        List<OrganizeListVO> voList = JsonUtil.createJsonToList(organizeEntityList, OrganizeListVO.class);
        voList.forEach(t -> {
            if (PermissionConst.COMPANY.equals(t.getType())) {
                t.setIcon("icon-linzen icon-linzen-tree-organization3");
            } else {
                t.setIcon("icon-linzen icon-linzen-tree-department1");
            }
            t.setHasChildren(true);
            // 处理断层
            if (StringUtil.isNotEmpty(t.getOrganizeIdTree())) {
                String[] split = t.getOrganizeIdTree().split(",");
                List<String> list1 = Arrays.asList(split);
                Collections.reverse(list1);
                for (String orgId : list1) {
                    if(!orgId.equals(t.getId())) {
                        SysOrganizeEntity organizeEntity1 = orgMaps.get(orgId);
                        if (organizeEntity1 != null) {
                            t.setParentId(organizeEntity1.getId());
                            String[] split1 = t.getOrganizeIdTree().split(organizeEntity1.getId());
                            if (split1.length > 1) {
                                t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                            }
                            break;
                        }
                    }
                }
            }
        });
        vo.setList(voList);
        return ServiceResult.success(vo);
    }

    private void getParentEntity(Map<String, SysOrganizeEntity> orgMaps,
                                                 List<SysOrganizeEntity> temOrganizeEntityList,
                                                 List<SysOrganizeEntity> organizeEntityList) {
        List<SysOrganizeEntity> temOrganizeEntityList1 = new ArrayList<>();
        // 判断是否有顶级组织权限
        if (organizeEntityList.size() == 0) {
            temOrganizeEntityList.forEach(t -> {
                List<SysOrganizeEntity> organizeByParentId = organizeService.getOrganizeByParentId(t.getId());
                temOrganizeEntityList1.addAll(organizeByParentId);
                organizeByParentId.forEach(organizeEntity -> {
                    SysOrganizeEntity organizeEntity1 = orgMaps.get(organizeEntity.getId());
                    if (organizeEntity1 != null) {
                        organizeEntityList.add(organizeEntity1);
                    }
                });
            });
        }
        if (organizeEntityList.size() == 0 && temOrganizeEntityList1.size() > 0) {
            getParentEntity(orgMaps, temOrganizeEntityList1, organizeEntityList);
        }
    }
}
