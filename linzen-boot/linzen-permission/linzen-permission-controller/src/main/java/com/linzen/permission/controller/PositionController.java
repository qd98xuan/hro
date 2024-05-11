package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linzen.annotation.PositionPermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.exception.DataBaseException;
import com.linzen.permission.entity.*;
import com.linzen.permission.model.permission.PermissionModel;
import com.linzen.permission.model.position.*;
import com.linzen.permission.model.user.mod.UserIdModel;
import com.linzen.permission.service.*;
import com.linzen.util.*;
import com.linzen.util.treeutil.ListToTreeUtil;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位信息
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "岗位管理", description = "Position")
@RestController
@RequestMapping("/api/permission/Position")
public class PositionController extends SuperController<PositionService, SysPositionEntity> {
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private OrganizeRelationService organizeRelationService;

    /**
     * 获取岗位管理信息列表
     *
     * @param paginationPosition 分页模型
     * @return
     */
    @Operation(summary = "获取岗位列表（分页）")
    @SaCheckPermission("permission.position")
    @GetMapping
    public ServiceResult<PageListVO<PositionListVO>> list(PaginationPosition paginationPosition) {
        List<DictionaryDataEntity> dictionaryDataEntities = dictionaryDataService.getListByTypeDataCode("PositionType");
        if (StringUtil.isNotEmpty(paginationPosition.getType())) {
            DictionaryDataEntity dictionaryDataEntity = dictionaryDataEntities.stream().filter(t -> paginationPosition.getType().equals(t.getId())).findFirst().orElse(null);
            if (dictionaryDataEntity != null) {
                paginationPosition.setEnCode(dictionaryDataEntity.getEnCode());
            }
        }
        List<SysPositionEntity> data = positionService.getList(paginationPosition);
        //添加部门信息，部门映射到organizeId
        List<PositionListVO> voList = JsonUtil.createJsonToList(data, PositionListVO.class);
        List<String> collect = data.stream().map(SysPositionEntity::getOrganizeId).collect(Collectors.toList());
        List<SysOrganizeEntity> list = organizeService.getOrgEntityList(collect, true);
        //添加部门信息
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        for (PositionListVO entity1 : voList) {
            SysOrganizeEntity entity = list.stream().filter(t -> t.getId().equals(entity1.getOrganizeId())).findFirst().orElse(new SysOrganizeEntity());
            if (entity1.getOrganizeId().equals(entity.getId())) {
                entity1.setDepartment(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, entity.getOrganizeIdTree(), "/"));
            }
        }
        //将type成中文名
        for (PositionListVO entity1 : voList) {
            dictionaryDataEntities.stream().filter(t -> t.getEnCode().equals(entity1.getType())).findFirst().ifPresent(entity -> entity1.setType(entity.getFullName()));
        }
        PaginationVO paginationVO = BeanUtil.toBean(paginationPosition, PaginationVO.class);
        return ServiceResult.pageList(voList, paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/All")
    public ServiceResult<ListVO<PositionListAllVO>> listAll() {
        List<SysPositionEntity> list = positionService.getList(true);
        List<PositionListAllVO> vos = JsonUtil.createJsonToList(list, PositionListAllVO.class);
        ListVO<PositionListAllVO> vo = new ListVO<>();
        vo.setList(vos);
        return ServiceResult.success(vo);
    }

    /**
     * 树形（机构+岗位）
     *
     * @return
     */
    @Operation(summary = "获取岗位下拉列表（公司+部门+岗位）")
    @GetMapping("/Selector")
    public ServiceResult<ListVO<PositionSelectorVO>> selector() {
        List<SysPositionEntity> list1 = positionService.getList(true);
        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrgMaps(null, false, null);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();
        List<SysOrganizeEntity> list2 = new ArrayList<>(orgMaps.values());;
        List<PosOrgModel> posList = new ArrayList<>();
        for (SysPositionEntity entity : list1) {
            PosOrgModel posOrgModel = BeanUtil.toBean(entity, PosOrgModel.class);
            String organizeId = entity.getOrganizeId();
            posOrgModel.setParentId(organizeId);
            posOrgModel.setType("position");
            posOrgModel.setIcon("icon-linzen icon-linzen-tree-position1");
            SysOrganizeEntity organizeEntity = orgMaps.get(organizeId);
            if (organizeEntity != null) {
                posOrgModel.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/"));
                posOrgModel.setOrganizeIds(organizeService.getOrgIdTree(organizeEntity));
            } else {
                posOrgModel.setOrganizeIds(new ArrayList<>());
            }
            posList.add(posOrgModel);
        }
        List<PosOrgModel> orgList = JsonUtil.createJsonToList(list2, PosOrgModel.class);
        for (PosOrgModel entity1 : orgList) {
            if ("department".equals(entity1.getType())) {
                entity1.setIcon("icon-linzen icon-linzen-tree-department1");
            } else if ("company".equals(entity1.getType())) {
                entity1.setIcon("icon-linzen icon-linzen-tree-organization3");
            }
            SysOrganizeEntity organizeEntity = orgMaps.get(entity1.getId());
            if (organizeEntity != null) {
                entity1.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, organizeEntity.getOrganizeIdTree(), "/"));
                entity1.setOrganizeIds(organizeService.getOrgIdTree(organizeEntity));
            } else {
                entity1.setOrganizeIds(new ArrayList<>());
            }
            entity1.setOrganizeIds(new ArrayList<>());
        }
        JSONArray objects = ListToTreeUtil.treeWhere(posList, orgList);
        List<PosOrgModel> jsonToList = JsonUtil.createJsonToList(objects, PosOrgModel.class);

        List<PosOrgModel> list = new ArrayList<>(16);
        // 得到角色的值
        List<PosOrgModel> collect = jsonToList.stream().filter(t -> "position".equals(t.getType())).sorted(Comparator.comparing(PosOrgModel::getSortCode)).collect(Collectors.toList());
        list.addAll(collect);
        jsonToList.removeAll(collect);
        List<PosOrgModel> collect1 = jsonToList.stream().sorted(Comparator.comparing(PosOrgModel::getSortCode).thenComparing(PosOrgModel::getCreatorTime, Comparator.reverseOrder())).collect(Collectors.toList());
        list.addAll(collect1);

        List<SumTree<PosOrgModel>> trees = TreeDotUtils.convertListToTreeDot(list);
        List<PositionSelectorVO> jsonToList1 = JsonUtil.createJsonToList(trees, PositionSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(jsonToList1);
        return ServiceResult.success(vo);
    }

    /**
     * 通过部门、岗位获取岗位下拉框
     *
     * @param idModel 岗位选择模型
     * @return
     */
    @Operation(summary = "通过部门、岗位获取岗位下拉框")
    @Parameters({
            @Parameter(name = "positionConditionModel", description = "岗位选择模型", required = true)
    })
    @PostMapping("/PositionCondition")
    public ServiceResult<ListVO<PositionSelectorVO>> positionCondition(@RequestBody UserIdModel idModel) {
        // 定义返回对象
        List<PositionSelectorVO> modelList = new ArrayList<>();

        List<String> list = organizeRelationService.getOrgIds(idModel.getIds(), null);
        List<String> lists = new ArrayList<>();
        list.forEach(t -> lists.add(t.split("--")[0]));
        list = lists;
        List<String> collect = positionService.getListByOrganizeId(list, false).stream().map(SysPositionEntity::getId).collect(Collectors.toList());
        collect.addAll(list);
        List<SysPositionEntity> positionName = positionService.getPositionName(collect,  null);
        positionName = positionName.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());

        Map<String, SysOrganizeEntity> orgMaps = organizeService.getOrganizeName(positionName.stream().map(SysPositionEntity::getOrganizeId).collect(Collectors.toList()), null, false, null);
        Map<String, String> orgIdNameMaps = organizeService.getInfoList();

        List<PosOrgConditionModel> posOrgModels = new ArrayList<>(16);
        positionName.forEach(t -> {
            PosOrgConditionModel posOrgModel = BeanUtil.toBean(t, PosOrgConditionModel.class);
            SysOrganizeEntity entity = orgMaps.get(t.getOrganizeId());
            if (entity != null) {
                posOrgModel.setOrganizeId(entity.getId());
                posOrgModel.setParentId(entity.getId());
                if (StringUtil.isNotEmpty(entity.getOrganizeIdTree())) {
                    posOrgModel.setOrganize(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, entity.getOrganizeIdTree(), "/"));
                }
            }
            posOrgModel.setType("position");
            posOrgModel.setIcon("icon-linzen icon-linzen-tree-position1");
            posOrgModels.add(posOrgModel);
        });

        // 处理组织
        orgMaps.values().forEach(org -> {
            PosOrgConditionModel orgVo = BeanUtil.toBean(org, PosOrgConditionModel.class);
            if ("department".equals(orgVo.getType())) {
                orgVo.setIcon("icon-linzen icon-linzen-tree-department1");
            } else if ("company".equals(orgVo.getType())) {
                orgVo.setIcon("icon-linzen icon-linzen-tree-organization3");
            }
            // 处理断层
            if (StringUtil.isNotEmpty(org.getOrganizeIdTree())) {
                List<String> list1 = new ArrayList<>();
                String[] split = org.getOrganizeIdTree().split(",");
                list1 = Arrays.asList(split);
                Collections.reverse(list1);
                for (String orgId : list1) {
                    SysOrganizeEntity organizeEntity1 = orgMaps.get(orgId);
                    if (organizeEntity1 != null && !organizeEntity1.getId().equals(orgVo.getId())) {
                        orgVo.setParentId(organizeEntity1.getId());
                        String[] split1 = org.getOrganizeIdTree().split(organizeEntity1.getId());
                        if (split1.length > 1) {
                            orgVo.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, split1[1], "/"));
                        }
                        break;
                    }
                }
            }
            posOrgModels.add(orgVo);
        });

        List<SumTree<PosOrgConditionModel>> trees = TreeDotUtils.convertListToTreeDot(posOrgModels);
        List<PositionSelectorVO> positionSelectorVO = JsonUtil.createJsonToList(trees, PositionSelectorVO.class);
        // 处理数据
        positionSelectorVO.forEach(t -> {
            if (!"position".equals(t.getType())) {
                t.setFullName(organizeService.getFullNameByOrgIdTree(orgIdNameMaps, t.getOrganizeIdTree(), "/"));
            }
        });
        modelList.addAll(positionSelectorVO);
        ListVO vo = new ListVO();
        vo.setList(modelList);
        return ServiceResult.success(vo);
    }

    /**
     * 通过组织id获取岗位列表
     *
     * @param organizeId 主键值
     * @return
     */
    @Operation(summary = "通过组织id获取岗位列表")
    @Parameters({
            @Parameter(name = "organizeId", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.position")
    @GetMapping("/getList/{organizeId}")
    public ServiceResult<List<PositionVo>> getListByOrganizeId(@PathVariable("organizeId") String organizeId) {
        List<SysPositionEntity> list = positionService.getListByOrganizeId(Collections.singletonList(organizeId), false);
        List<PositionVo> jsonToList = JsonUtil.createJsonToList(list, PositionVo.class);
        return ServiceResult.success(jsonToList);
    }

    /**
     * 获取岗位管理信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取岗位管理信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.position")
    @GetMapping("/{id}")
    public ServiceResult<PositionInfoVO> getInfo(@PathVariable("id") String id) throws DataBaseException {
        SysPositionEntity entity = positionService.getInfo(id);
        PositionInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, PositionInfoVO.class);
        String organizeId = entity.getOrganizeId();
        SysOrganizeEntity organizeEntity = organizeService.getInfo(organizeId);
        vo.setOrganizeIdTree(StringUtil.isNotEmpty(organizeEntity.getOrganizeIdTree()) ? Arrays.asList(organizeEntity.getOrganizeIdTree().split(",")) : new ArrayList<>());
        return ServiceResult.success(vo);
    }


    /**
     * 新建岗位管理
     *
     * @param positionCrForm 实体对象
     * @return
     */
    @PositionPermission
    @Operation(summary = "新建岗位管理")
    @Parameters({
            @Parameter(name = "positionCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("permission.position")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid PositionCrForm positionCrForm) {
        SysPositionEntity entity = BeanUtil.toBean(positionCrForm, SysPositionEntity.class);
        if (positionService.isExistByFullName(entity, false)) {
            return ServiceResult.error("岗位名称不能重复");
        }
        if (positionService.isExistByEnCode(entity, false)) {
            return ServiceResult.error("岗位编码不能重复");
        }
        // 设置岗位id
        entity.setId(RandomUtil.uuId());
//        createOrganizeRoleRelation(entity.getOrganizeId(), entity.getId());
        positionService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新岗位管理
     *
     * @param id             主键值
     * @param positionUpForm 实体对象
     * @return
     */
    @PositionPermission
    @Operation(summary = "更新岗位管理")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "positionUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("permission.position")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid PositionUpForm positionUpForm) {
        SysPositionEntity positionEntity = positionService.getInfo(id);
        if (positionEntity == null) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        // 当岗位绑定用户不让其更改
        if(userRelationService.existByObj(PermissionConst.POSITION, id)){
            if(!positionService.getInfo(id).getOrganizeId().equals(positionUpForm.getOrganizeId())){
                return ServiceResult.error(MsgCode.FA023.get());
            }
            if (positionUpForm.getEnabledMark() == 0 && positionEntity.getEnabledMark() == 1) {
                return ServiceResult.error(MsgCode.FA030.get());
            }
        }
        SysPositionEntity entity = BeanUtil.toBean(positionUpForm, SysPositionEntity.class);
        entity.setId(id);
        if (positionService.isExistByFullName(entity, true)) {
            return ServiceResult.error("岗位名称不能重复");
        }
        if (positionService.isExistByEnCode(entity, true)) {
            return ServiceResult.error("岗位编码不能重复");
        }
//        createOrganizeRoleRelation(entity.getOrganizeId(), id);
        boolean flag = positionService.update(id, entity);
        if (flag == false) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除岗位管理
     *
     * @param id 主键值
     * @return
     */
    @PositionPermission
    @Operation(summary = "删除岗位管理")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.position")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        // 当岗位绑定用户不让其更改
        if(userRelationService.existByObj(PermissionConst.POSITION, id)){
            return ServiceResult.error(MsgCode.FA024.get());
        }
        SysPositionEntity entity = positionService.getInfo(id);
        if (entity != null) {
            List<SysUserRelationEntity> userRelList = userRelationService.getListByObjectId(id);
            if(userRelList.size()>0){
                return ServiceResult.error(MsgCode.FA024.get());
            }
            for (SysUserRelationEntity entity1 : userRelList) {
                SysUserEntity entity2 = userService.getById(entity1.getUserId());
                if (entity2 != null) {
                    String newPositionId = entity2.getPositionId().replace(id, "");
                    if (entity2.getPositionId().contains(id)) {
                        if (newPositionId.length() != 0 && newPositionId.substring(0, 1).equals(",")) {
                            entity2.setPositionId(newPositionId.substring(1));
                        } else if (newPositionId.length() != 0) {
                            entity2.setPositionId(newPositionId.replace(",,", ","));
                        }
                    }
                }
            }
            userRelationService.deleteAllByObjId(id);

            // 删除岗位与组织之间的关联数据
            QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
            query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.POSITION);
            query.lambda().eq(SysOrganizeRelationEntity::getObjectId, id);
            organizeRelationService.remove(query);

            positionService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

    /**
     * 更新菜单状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新菜单状态")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("permission.position")
    @PutMapping("/{id}/Actions/State")
    public ServiceResult upState(@PathVariable("id") String id) {
        SysPositionEntity entity = positionService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == null ||"1".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            positionService.update(id, entity);
            return ServiceResult.success(MsgCode.SU004.get());
        }
        return ServiceResult.error("MsgCode.UPDATE_FAIL_NOT_EXISTS");
    }

    /**
     * 通过组织id获取岗位列表
     *
     * @param organizeIds 组织id数组
     * @return 岗位列表
     */
    @Operation(summary = "获取岗位列表通过组织id数组")
    @Parameters({
            @Parameter(name = "organizeIds", description = "组织id数组", required = true)
    })
    @SaCheckPermission("permission.user")
    @PostMapping("/getListByOrgIds")
    public ServiceResult<ListVO<PermissionModel>> getListByOrganizeIds(@RequestBody @Valid Map<String,List<String>> organizeIds) {
        List<PermissionModel> PositionModelAll = new LinkedList<>();
        for(String organizeId : organizeIds.get("organizeIds")){
            SysOrganizeEntity info = organizeService.getInfo(organizeId);
            if(info != null){
                PermissionModel parentModel = new PermissionModel();
                List<SysPositionEntity> list = positionService.getListByOrganizeId(Collections.singletonList(organizeId), true);
                List<PermissionModel> positionModels = JsonUtil.createJsonToList(list, PermissionModel.class);
                parentModel.setHasChildren(true);
                parentModel.setFullName(info.getFullName());
                parentModel.setId(info.getId());
                parentModel.setChildren(positionModels);
                PositionModelAll.add(parentModel);
            }
        }
        ListVO vo = new ListVO();
        vo.setList(PositionModelAll);
        return ServiceResult.success(vo);


    }

    /**
     * 添加组织角色关联关系
     *
     * @param organizeId    组织id
     * @param positionId    岗位id
     */
    private void createOrganizeRoleRelation(String organizeId, String positionId) {
        // 清除之前的关联关系
        QueryWrapper<SysOrganizeRelationEntity> query = new QueryWrapper<>();
        query.lambda().eq(SysOrganizeRelationEntity::getObjectType, PermissionConst.POSITION);
        query.lambda().eq(SysOrganizeRelationEntity::getObjectId, positionId);
        organizeRelationService.remove(query);
        // 添加与组织的关联关系
        SysOrganizeRelationEntity organizeRelationEntity = new SysOrganizeRelationEntity();
        organizeRelationEntity.setId(RandomUtil.uuId());
        organizeRelationEntity.setOrganizeId(organizeId);
        organizeRelationEntity.setObjectType(PermissionConst.POSITION);
        organizeRelationEntity.setObjectId(positionId);
        organizeRelationService.save(organizeRelationEntity);
    }


}
