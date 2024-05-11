package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.entity.ModuleButtonEntity;
import com.linzen.base.model.button.*;
import com.linzen.base.service.ModuleButtonService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import com.linzen.util.treeutil.SumTree;
import com.linzen.util.treeutil.newtreeutil.TreeDotUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 按钮权限
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "按钮权限", description = "ModuleButton")
@RestController
@RequestMapping("/api/system/ModuleButton")
public class ModuleButtonController extends SuperController<ModuleButtonService, ModuleButtonEntity> {

    @Autowired
    private ModuleButtonService moduleButtonService;

    /**
     * 按钮按钮权限列表
     *
     * @param menuId     功能主键
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取按钮权限列表")
    @Parameters({
            @Parameter(name = "menuId", description = "功能主键", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{menuId}/List")
    public ServiceResult list(@PathVariable("menuId") String menuId, Pagination pagination) {
        List<ModuleButtonEntity> data = moduleButtonService.getListByModuleIds(menuId, pagination);
        List<ButtonTreeListModel> treeList = JsonUtil.createJsonToList(data, ButtonTreeListModel.class);
        List<SumTree<ButtonTreeListModel>> sumTrees = TreeDotUtils.convertListToTreeDot(treeList);
        if (data.size() > sumTrees.size()) {
            List<ButtonTreeListVO> list = JsonUtil.createJsonToList(sumTrees, ButtonTreeListVO.class);
            ListVO<ButtonTreeListVO> treeVo = new ListVO<>();
            treeVo.setList(list);
            return ServiceResult.success(treeVo);
        }
        List<ButtonListVO> list = JsonUtil.createJsonToList(treeList, ButtonListVO.class);
        ListVO<ButtonListVO> treeVo1 = new ListVO<>();
        treeVo1.setList(list);
        return ServiceResult.success(treeVo1);
    }


    /**
     * 按钮按钮权限列表
     *
     * @param menuId 功能主键
     * @return ignore
     */
    @Operation(summary = "获取按钮权限下拉框")
    @Parameters({
            @Parameter(name = "menuId", description = "功能主键", required = true)
    })
    @GetMapping("/{menuId}/Selector")
    public ServiceResult<ListVO<ButtonTreeListVO>> selectList(@PathVariable("menuId") String menuId) {
        List<ModuleButtonEntity> data = moduleButtonService.getListByModuleIds(menuId);
        List<ButtonTreeListModel> treeList = JsonUtil.createJsonToList(data, ButtonTreeListModel.class);
        List<SumTree<ButtonTreeListModel>> sumTrees = TreeDotUtils.convertListToTreeDot(treeList);
        List<ButtonTreeListVO> list = JsonUtil.createJsonToList(sumTrees, ButtonTreeListVO.class);
        ListVO<ButtonTreeListVO> treeVo = new ListVO<>();
        treeVo.setList(list);
        return ServiceResult.success(treeVo);
    }


    /**
     * 获取按钮权限信息
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "获取按钮权限信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}")
    public ServiceResult<ModuleButtonInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        ModuleButtonInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ModuleButtonInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建按钮权限
     *
     * @param moduleButtonCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建按钮权限")
    @Parameters({
            @Parameter(name = "moduleButtonCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody ModuleButtonCrForm moduleButtonCrForm) {
        ModuleButtonEntity entity = BeanUtil.toBean(moduleButtonCrForm, ModuleButtonEntity.class);
        if (moduleButtonService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        moduleButtonService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新按钮权限
     *
     * @param id                 主键值
     * @param moduleButtonUpForm 更新参数
     * @return ignore
     */
    @Operation(summary = "更新按钮权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "moduleButtonUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody ModuleButtonUpForm moduleButtonUpForm) {
        ModuleButtonEntity entity = BeanUtil.toBean(moduleButtonUpForm, ModuleButtonEntity.class);
        if (moduleButtonService.isExistByEnCode(entity.getModuleId(), entity.getEnCode(), id)) {
            return ServiceResult.error(MsgCode.EXIST002.get());
        }
        boolean flag = moduleButtonService.update(id, entity);
        if (flag == false) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除按钮权限
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除按钮权限")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        if (entity != null) {
            moduleButtonService.delete(entity);
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
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        if (entity.getEnabledMark() == null || "1".equals(String.valueOf(entity.getEnabledMark()))) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
        boolean flag = moduleButtonService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

}
