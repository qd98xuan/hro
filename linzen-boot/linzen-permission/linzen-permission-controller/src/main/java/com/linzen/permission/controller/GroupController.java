package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.base.entity.DictionaryDataEntity;
import com.linzen.base.service.DictionaryDataService;
import com.linzen.base.service.DictionaryTypeService;
import com.linzen.base.vo.ListVO;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.GroupEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.model.user.mod.UserIdModel;
import com.linzen.permission.model.usergroup.*;
import com.linzen.permission.service.GroupService;
import com.linzen.permission.service.UserRelationService;
import com.linzen.util.JsonUtil;
import com.linzen.emnus.DictionaryDataEnum;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 分组管理控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@RestController
@Tag(name = "分组管理", description = "UserGroupController")
@RequestMapping("/api/permission/Group")
public class GroupController extends SuperController<GroupService, GroupEntity> {

    @Autowired
    private GroupService userGroupService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeApi;
    @Autowired
    private UserRelationService userRelationService;

    /**
     * 获取分组管理列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取分组管理列表")
    @SaCheckPermission(value = {"permission.group"})
    @GetMapping
    public ServiceResult<PageListVO<GroupPaginationVO>> list(PaginationGroup pagination) {
        List<GroupEntity> list = userGroupService.getList(pagination);
        List<GroupPaginationVO> jsonToList = JsonUtil.createJsonToList(list, GroupPaginationVO.class);
        // 通过数据字典获取类型
        List<DictionaryDataEntity> dictionaryDataEntities = dictionaryDataService.getList(dictionaryTypeApi.getInfoByEnCode(DictionaryDataEnum.PERMISSION_GROUP.getDictionaryTypeId()).getId());
        for (GroupPaginationVO userGroupPaginationVO : jsonToList) {
            DictionaryDataEntity dictionaryDataEntity = dictionaryDataEntities.stream().filter(t -> t.getId().equals(userGroupPaginationVO.getType())).findFirst().orElse(null);
            userGroupPaginationVO.setType(dictionaryDataEntity != null ? dictionaryDataEntity.getFullName() : userGroupPaginationVO.getId());
        }
        PaginationVO paginationVO = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(jsonToList, paginationVO);
    }

    /**
     * 获取分组管理下拉框
     * @return
     */
    @Operation(summary = "获取分组管理下拉框")
    @GetMapping("/Selector")
    public ServiceResult<List<GroupSelectorVO>> selector() {
        List<GroupTreeModel> tree = new ArrayList<>();
        List<GroupEntity> data = userGroupService.list();
        List<DictionaryDataEntity> dataEntityList = dictionaryDataService.getList(dictionaryTypeApi.getInfoByEnCode(DictionaryDataEnum.PERMISSION_GROUP.getDictionaryTypeId()).getId());
        // 获取分组管理外层菜单
        for (DictionaryDataEntity dictionaryDataEntity : dataEntityList) {
            GroupTreeModel firstModel = BeanUtil.toBean(dictionaryDataEntity, GroupTreeModel.class);
            firstModel.setId(dictionaryDataEntity.getId());
            firstModel.setType("0");
            long num = data.stream().filter(t -> t.getType().equals(dictionaryDataEntity.getId())).count();
            firstModel.setNum(num);
            if (num > 0) {
                tree.add(firstModel);
            }
        }
        for (GroupEntity entity : data) {
            GroupTreeModel treeModel = BeanUtil.toBean(entity, GroupTreeModel.class);
            treeModel.setType("group");
            treeModel.setParentId(entity.getType());
            treeModel.setIcon("icon-linzen icon-linzen-generator-group1");
            treeModel.setId(entity.getId());
            DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getType());
            if (dataEntity != null) {
                tree.add(treeModel);
            }
        }
        List<SumTree<GroupTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(tree);
        List<GroupSelectorVO> list = JsonUtil.createJsonToList(sumTrees, GroupSelectorVO.class);
        ListVO<GroupSelectorVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(list);
    }

    /**
     * 自定义范围获取分组下拉框
     *
     * @param idModel 岗位选择模型
     * @return
     */
    @Operation(summary = "自定义范围获取分组下拉框")
    @Parameters({
            @Parameter(name = "positionConditionModel", description = "岗位选择模型", required = true)
    })
    @PostMapping("/GroupCondition")
    public ServiceResult<ListVO<GroupSelectorVO>> positionCondition(@RequestBody UserIdModel idModel) {
        List<GroupEntity> data = userGroupService.getListByIds(idModel.getIds(), true);
        List<GroupTreeModel> tree = new ArrayList<>();
        List<DictionaryDataEntity> dataEntityList = dictionaryDataService.getListByTypeDataCode(DictionaryDataEnum.PERMISSION_GROUP.getDictionaryTypeId());
        // 获取分组管理外层菜单
        for (DictionaryDataEntity dictionaryDataEntity : dataEntityList) {
            GroupTreeModel firstModel = BeanUtil.toBean(dictionaryDataEntity, GroupTreeModel.class);
            firstModel.setId(dictionaryDataEntity.getId());
            firstModel.setType("0");
            long num = data.stream().filter(t -> t.getType().equals(dictionaryDataEntity.getId())).count();
            firstModel.setNum(num);
            if (num > 0) {
                tree.add(firstModel);
            }
        }
        for (GroupEntity entity : data) {
            GroupTreeModel treeModel = BeanUtil.toBean(entity, GroupTreeModel.class);
            treeModel.setType("group");
            treeModel.setParentId(entity.getType());
            treeModel.setIcon("icon-linzen icon-linzen-generator-group1");
            treeModel.setId(entity.getId());
            DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getType());
            if (dataEntity != null) {
                tree.add(treeModel);
            }
        }
        List<SumTree<GroupTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(tree);
        List<GroupSelectorVO> list = JsonUtil.createJsonToList(sumTrees, GroupSelectorVO.class);
        ListVO<GroupSelectorVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission(value = {"permission.group"})
    @GetMapping("/{id}")
    public ServiceResult<GroupInfoVO> info(@PathVariable("id") String id) {
        GroupEntity entity = userGroupService.getInfo(id);
        GroupInfoVO vo = BeanUtil.toBean(entity, GroupInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param userGroupCrForm 新建模型
     * @return
     */
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "userGroupCrForm", description = "新建模型", required = true)
    })
    @SaCheckPermission(value = {"permission.group"})
    @PostMapping
    public ServiceResult create(@RequestBody @Valid GroupCreateForm userGroupCrForm) {
        GroupEntity entity = BeanUtil.toBean(userGroupCrForm, GroupEntity.class);
        // 判断名称是否重复
        if (userGroupService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        // 判断编码是否重复
        if (userGroupService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        userGroupService.crete(entity);
        return ServiceResult.success("创建成功");
    }

    /**
     * 更新
     *
     * @param id 主键
     * @param userGroupUpForm 修改模型
     * @return
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "userGroupUpForm", description = "修改模型", required = true)
    })
    @SaCheckPermission(value = {"permission.group"})
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid GroupUpForm userGroupUpForm) {
        GroupEntity groupEntity = userGroupService.getInfo(id);
        if (groupEntity == null) {
            return ServiceResult.error(MsgCode.FA013.get());
        }
        if ((groupEntity.getEnabledMark() == 1 && userGroupUpForm.getEnabledMark() == 0)
                && userRelationService.getListByObjectId(id, PermissionConst.GROUP).size() > 0) {
            return ServiceResult.error(MsgCode.FA030.get());
        }
        GroupEntity entity = BeanUtil.toBean(userGroupUpForm, GroupEntity.class);
        // 判断名称是否重复
        if (userGroupService.isExistByFullName(entity.getFullName(), id)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        // 判断编码是否重复
        if (userGroupService.isExistByEnCode(entity.getEnCode(), id)) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        userGroupService.update(id, entity);
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission(value = {"permission.group"})
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        GroupEntity entity = userGroupService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        List<SysUserRelationEntity> bingUserByRoleList = userRelationService.getListByObjectId(id, PermissionConst.GROUP);
        if (bingUserByRoleList.size() > 0) {
            return ServiceResult.error(MsgCode.FA024.get());
        }
        userGroupService.delete(entity);
        return ServiceResult.success(MsgCode.SU003.get());
    }

}
