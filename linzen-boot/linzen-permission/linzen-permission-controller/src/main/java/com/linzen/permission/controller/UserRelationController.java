package com.linzen.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ArrayUtil;
import com.linzen.annotation.UserPermission;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.constant.PermissionConst;
import com.linzen.permission.entity.SysUserEntity;
import com.linzen.permission.entity.SysUserRelationEntity;
import com.linzen.permission.model.userrelation.UserRelationForm;
import com.linzen.permission.model.userrelation.UserRelationIdsVO;
import com.linzen.permission.service.UserRelationService;
import com.linzen.permission.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户关系
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "用户关系", description = "UserRelation")
@RestController
@RequestMapping("/api/permission/UserRelation")
public class UserRelationController extends SuperController<UserRelationService, SysUserRelationEntity> {

    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @param objectId 对象主键
     * @return
     */
    @Operation(summary = "获取岗位/角色/门户成员列表ids")
    @Parameters({
            @Parameter(name = "objectId", description = "对象主键", required = true)
    })
    @SaCheckPermission(value = {"permission.authorize", "permission.position", "permission.role"}, mode = SaMode.OR)
    @GetMapping("/{objectId}")
    public ServiceResult<UserRelationIdsVO> listTree(@PathVariable("objectId") String objectId) {
        List<SysUserRelationEntity> data = userRelationService.getListByObjectId(objectId);
        List<String> ids = new ArrayList<>();
        for (SysUserRelationEntity entity : data) {
            ids.add(entity.getUserId());
        }
        UserRelationIdsVO vo = new UserRelationIdsVO();
        vo.setIds(ids);
        return ServiceResult.success(vo);
    }

    /**
     * 保存
     *
     * @param objectId 对象主键
     * @param userRelationForm 页面数据
     * @return
     */
    @UserPermission
    @Operation(summary = "添加岗位或角色成员")
    @Parameters({
            @Parameter(name = "objectId", description = "对象主键", required = true),
            @Parameter(name = "userRelationForm", description = "页面数据", required = true)
    })
    @SaCheckPermission(value = {"permission.authorize", "permission.position", "permission.role"}, mode = SaMode.OR)
    @PostMapping("/{objectId}")
    public ServiceResult save(@PathVariable("objectId") String objectId, @RequestBody UserRelationForm userRelationForm) {
        List<String> userIds = new ArrayList<>();
        if(userRelationForm.getObjectType().equals(PermissionConst.ROLE)){
            // 得到禁用的id
            List<SysUserRelationEntity> listByObjectId = userRelationService.getListByObjectId(objectId, PermissionConst.ROLE);
            List<String> collect = listByObjectId.stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
            //删除的用户
            List<String> collect1 = collect.stream().filter(t -> !userRelationForm.getUserIds().contains(t)).collect(Collectors.toList());
            //添加的用户
            List<String> collect2 = userRelationForm.getUserIds().stream().filter(t -> !collect.contains(t)).collect(Collectors.toList());
            userIds.addAll(collect1);
            userIds.addAll(collect2);
            Set<String> set = new HashSet<>(userRelationForm.getUserIds());
            set.addAll(userService.getUserList(collect).stream().map(SysUserEntity::getId).collect(Collectors.toList()));
            List<String> list = new ArrayList<>(set);
            userRelationService.roleSaveByUserIds(objectId, list);
        } else {
            // 得到禁用的id
            List<SysUserRelationEntity> listByObjectId = userRelationService.getListByObjectId(objectId, PermissionConst.POSITION);
            if(userRelationForm.getObjectType().equals(PermissionConst.GROUP)){
                listByObjectId = userRelationService.getListByObjectId(objectId, PermissionConst.GROUP);
            }
            List<String> collect = listByObjectId.stream().map(SysUserRelationEntity::getUserId).collect(Collectors.toList());
            //删除的用户
            List<String> collect1 = collect.stream().filter(t -> !userRelationForm.getUserIds().contains(t)).collect(Collectors.toList());
            //添加的用户
            List<String> collect2 = userRelationForm.getUserIds().stream().filter(t -> !collect.contains(t)).collect(Collectors.toList());
            userIds.addAll(collect1);
            userIds.addAll(collect2);
            Set<String> set = new HashSet<>(userRelationForm.getUserIds());
            set.addAll(userService.getUserList(collect).stream().map(SysUserEntity::getId).collect(Collectors.toList()));
            List<String> list = new ArrayList<>(set);
            userRelationForm.setUserIds(list);
            userRelationService.saveObjectId(objectId,userRelationForm);
        }
        userIds = userService.filterOrgAdministrator(userIds);
        userService.delCurUser(null, ArrayUtil.toArray(userIds, String.class));
        return ServiceResult.success(MsgCode.SU002.get());
    }

}
