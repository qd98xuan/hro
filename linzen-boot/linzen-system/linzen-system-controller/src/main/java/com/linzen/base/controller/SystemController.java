package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.SysSystemEntity;
import com.linzen.base.model.base.*;
import com.linzen.base.service.CommonWordsService;
import com.linzen.base.service.SystemService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.message.util.OnlineUserModel;
import com.linzen.message.util.OnlineUserProvider;
import com.linzen.permission.service.AuthorizeService;
import com.linzen.permission.service.OrganizeAdministratorService;
import com.linzen.permission.service.UserService;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统控制器
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "系统", description = "system")
@RestController
@RequestMapping("/api/system/System")
public class SystemController extends SuperController<SystemService, SysSystemEntity> {

    @Autowired
    private SystemService systemService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private CommonWordsService commonWordsService;
    @Autowired
    private OrganizeAdministratorService organizeAdminTratorApi;
    @Autowired
    private AuthorizeService authorizeApi;

    /**
     * 获取系统列表
     *
     * @param page 关键字
     * @return ignore
     */
    @Operation(summary = "获取系统列表")
    @SaCheckPermission("system.menu")
    @GetMapping
    public ServiceResult<ListVO<SystemListVO>> list(SystemPageVO page) {
        Boolean delFlag = false;
        if (ObjectUtil.equal(page.getEnabledMark(), "0")) {
            delFlag = null;
        }
        if (ObjectUtil.equal(page.getEnabledMark(), "1")) {
            delFlag = true;
        }
        List<SysSystemEntity> list = systemService.getList(page.getKeyword(), delFlag, true, page.getSelector(), true, new ArrayList<>());
        List<SystemListVO> jsonToList = JsonUtil.createJsonToList(list, SystemListVO.class);
        return ServiceResult.success(new ListVO<>(jsonToList));
    }

    /**
     * 获取系统详情
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取系统详情")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}")
    public ServiceResult<SystemVO> info(@PathVariable("id") String id) {
        SysSystemEntity entity = systemService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA001.get());
        }
        SystemVO jsonToBean = BeanUtil.toBean(entity, SystemVO.class);
        return ServiceResult.success(jsonToBean);
    }

    /**
     * 新建系统
     *
     * @param systemCrModel 新建模型
     * @return ignore
     */
    @Operation(summary = "新建系统")
    @Parameters({
            @Parameter(name = "systemCrModel", description = "新建模型", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody SystemCrModel systemCrModel) {
        SysSystemEntity entity = BeanUtil.toBean(systemCrModel, SysSystemEntity.class);
        if (systemService.isExistFullName(entity.getId(), entity.getFullName())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (systemService.isExistEnCode(entity.getId(), entity.getEnCode())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        systemService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改系统
     *
     * @param id 主键
     * @param systemUpModel 修改模型
     * @return ignore
     */
    @Operation(summary = "修改系统")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "systemCrModel", description = "修改模型", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody SystemUpModel systemUpModel) {
        SysSystemEntity systemEntity = systemService.getInfo(id);
        if (systemEntity == null) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        // 主系统不允许禁用
        if (systemEntity.getIsMain() != null && systemEntity.getIsMain() == 1 && systemUpModel.getEnabledMark() == 0) {
            return ServiceResult.error("更新失败，主系统不允许禁用");
        }
        SysSystemEntity entity = BeanUtil.toBean(systemUpModel, SysSystemEntity.class);
        entity.setIsMain(systemEntity.getIsMain());
        if (systemService.isExistFullName(id, entity.getFullName())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        if (systemService.isExistEnCode(id, entity.getEnCode())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        systemService.update(id, entity);
        // 如果禁用了系统，则需要将系统
        if (systemEntity.getEnabledMark() == 1 && entity.getEnabledMark() == 0) {
            sentMessage("应用已被禁用，正为您切换应用", systemEntity);
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除系统
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除系统")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult<String> delete(@PathVariable("id") String id) {
        SysSystemEntity entity = systemService.getInfo(id);
        if (entity == null) {
            return ServiceResult.error(MsgCode.FA003.get());
        }
        if (entity.getIsMain() != null && entity.getIsMain() == 1) {
            return ServiceResult.error("主系统不允许删除");
        }
        // 系统绑定审批常用语时不允许被删除
        if(commonWordsService.existSystem(id)){
            return ServiceResult.error("系统在审批常用语中被使用，不允许删除");
        }else {
            systemService.delete(id);
            sentMessage("应用已被删除，正为您切换应用", entity);
        }
        return ServiceResult.success(MsgCode.SU003.get());
    }

    private void sentMessage(String message, SysSystemEntity entity) {
        // 如果禁用了系统，则需要将系统
        List<OnlineUserModel> onlineUserList = OnlineUserProvider.getOnlineUserList();
        for (OnlineUserModel item : onlineUserList) {
            String systemId = item.getSystemId();
            if (entity.getId().equals(systemId)) {
                if (item.getWebSocket().isOpen()) {
                    Map<String, String> maps = new HashMap<>(1);
                    maps.put("method", "logout");
                    maps.put("msg", "应用已被禁用或删除");
                    if (StringUtil.isNotEmpty(userProvider.get().getTenantId())) {
                        if (userProvider.get().getTenantId().equals(item.getTenantId())) {
                            item.getWebSocket().getAsyncRemote().sendText(JsonUtil.createObjectToString(maps));
                        }
                    } else {
                        item.getWebSocket().getAsyncRemote().sendText(JsonUtil.createObjectToString(maps));
                    }
                }
            }
        }
    }

}
