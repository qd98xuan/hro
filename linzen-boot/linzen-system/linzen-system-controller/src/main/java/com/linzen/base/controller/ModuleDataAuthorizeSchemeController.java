package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.ModuleDataAuthorizeSchemeEntity;
import com.linzen.base.model.moduledataauthorizescheme.DataAuthorizeSchemeCrForm;
import com.linzen.base.model.moduledataauthorizescheme.DataAuthorizeSchemeInfoVO;
import com.linzen.base.model.moduledataauthorizescheme.DataAuthorizeSchemeListVO;
import com.linzen.base.model.moduledataauthorizescheme.DataAuthorizeSchemeUpForm;
import com.linzen.base.service.ModuleDataAuthorizeSchemeService;
import com.linzen.base.vo.ListVO;
import com.linzen.constant.MsgCode;
import com.linzen.exception.DataBaseException;
import com.linzen.util.JsonUtil;
import com.linzen.util.JsonUtilEx;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据权限方案
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "数据权限方案", description = "ModuleDataAuthorizeScheme")
@RestController
@RequestMapping("/api/system/ModuleDataAuthorizeScheme")
public class ModuleDataAuthorizeSchemeController extends SuperController<ModuleDataAuthorizeSchemeService, ModuleDataAuthorizeSchemeEntity> {

    @Autowired
    private ModuleDataAuthorizeSchemeService schemeService;

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return ignore
     */
    @Operation(summary = "方案列表")
    @Parameters({
            @Parameter(name = "moduleId", description = "功能主键", required = true)
    })
    @GetMapping("/{moduleId}/List")
    public ServiceResult<ListVO<DataAuthorizeSchemeListVO>> list(@PathVariable("moduleId") String moduleId) {
        List<ModuleDataAuthorizeSchemeEntity> data = schemeService.getList(moduleId);
        List<DataAuthorizeSchemeListVO> list = JsonUtil.createJsonToList(data, DataAuthorizeSchemeListVO.class);
        ListVO<DataAuthorizeSchemeListVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     * @throws DataBaseException ignore
     */
    @Operation(summary = "获取方案信息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @GetMapping("/{id}")
    public ServiceResult<DataAuthorizeSchemeInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ModuleDataAuthorizeSchemeEntity entity = schemeService.getInfo(id);
        DataAuthorizeSchemeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DataAuthorizeSchemeInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @param dataAuthorizeSchemeCrForm 实体对象
     * @return ignore
     */
    @Operation(summary = "新建方案")
    @Parameters({
            @Parameter(name = "dataAuthorizeSchemeCrForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid DataAuthorizeSchemeCrForm dataAuthorizeSchemeCrForm) {
        ModuleDataAuthorizeSchemeEntity entity = BeanUtil.toBean(dataAuthorizeSchemeCrForm, ModuleDataAuthorizeSchemeEntity.class);
        // 判断fullName是否重复
        if (schemeService.isExistByFullName(entity.getId(), entity.getFullName(), entity.getModuleId())) {
            return ServiceResult.error("已存在相同名称");
        }
        // 判断encode是否重复
        if (schemeService.isExistByEnCode(entity.getId(), entity.getEnCode(), entity.getModuleId())) {
            return ServiceResult.error("已存在相同编码");
        }
        schemeService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 更新
     *
     * @param id                        主键值
     * @param dataAuthorizeSchemeUpForm 实体对象
     * @return ignore
     */
    @Operation(summary = "更新方案")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true),
            @Parameter(name = "dataAuthorizeSchemeUpForm", description = "实体对象", required = true)
    })
    @SaCheckPermission("system.menu")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid DataAuthorizeSchemeUpForm dataAuthorizeSchemeUpForm) {
        ModuleDataAuthorizeSchemeEntity entity = BeanUtil.toBean(dataAuthorizeSchemeUpForm, ModuleDataAuthorizeSchemeEntity.class);
        // 判断encode是否重复
        if ("1".equals(String.valueOf(entity.getAllData()))) {
            return ServiceResult.error("修改失败，该方案不允许编辑");
        }
        // 判断fullName是否重复
        if (schemeService.isExistByFullName(id, entity.getFullName(), entity.getModuleId())) {
            return ServiceResult.error("已存在相同名称");
        }
        // 判断encode是否重复
        if (schemeService.isExistByEnCode(id, entity.getEnCode(), entity.getModuleId())) {
            return ServiceResult.error("已存在相同编码");
        }
        boolean flag = schemeService.update(id, entity);
        if (!flag) {
            return ServiceResult.success(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return ignore
     */
    @Operation(summary = "删除方案")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @SaCheckPermission("system.menu")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ModuleDataAuthorizeSchemeEntity entity = schemeService.getInfo(id);
        if (entity != null) {
            schemeService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }

}
