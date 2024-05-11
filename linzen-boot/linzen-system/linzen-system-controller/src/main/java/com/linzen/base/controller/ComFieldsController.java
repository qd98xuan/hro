package com.linzen.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.ComFieldsEntity;
import com.linzen.base.model.comfields.ComFieldsCrForm;
import com.linzen.base.model.comfields.ComFieldsInfoVO;
import com.linzen.base.model.comfields.ComFieldsListVO;
import com.linzen.base.model.comfields.ComFieldsUpForm;
import com.linzen.base.service.ComFieldsService;
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
 * 常用字段
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "常用字段", description = "CommonFields")
@RestController
@RequestMapping("/api/system/CommonFields")
public class ComFieldsController extends SuperController<ComFieldsService, ComFieldsEntity> {

    @Autowired
    private ComFieldsService comFieldsService;

    /**
     * 获取常用字段列表
     *
     * @return ignore
     */
    @Operation(summary = "获取常用字段列表")
    @SaCheckPermission("systemData.dataModel")
    @GetMapping
    public ServiceResult<ListVO<ComFieldsListVO>> list() {
        List<ComFieldsEntity> data = comFieldsService.getList();
        List<ComFieldsListVO> list = JsonUtil.createJsonToList(data, ComFieldsListVO.class);
        ListVO<ComFieldsListVO> vo = new ListVO<>();
        vo.setList(list);
        return ServiceResult.success(vo);
    }

    /**
     * 获取常用字段
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取常用字段")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataModel")
    @GetMapping("/{id}")
    public ServiceResult<ComFieldsInfoVO> info(@PathVariable("id") String id) throws DataBaseException {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        ComFieldsInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ComFieldsInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新增常用字段
     *
     * @param comFieldsCrForm 新增常用字段模型
     * @return ignore
     */
    @Operation(summary = "添加常用字段")
    @Parameter(name = "comFieldsCrForm", description = "新建模型", required = true)
    @SaCheckPermission("systemData.dataModel")
    @PostMapping
    public ServiceResult create(@RequestBody @Valid ComFieldsCrForm comFieldsCrForm) {
        ComFieldsEntity entity = BeanUtil.toBean(comFieldsCrForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(), entity.getId())) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        comFieldsService.create(entity);
        return ServiceResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改常用字段
     *
     * @param id              主键
     * @param comFieldsUpForm 修改常用字段模型
     * @return ignore
     */
    @Operation(summary = "修改常用字段")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "comFieldsUpForm", description = "修改模型", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @PutMapping("/{id}")
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ComFieldsUpForm comFieldsUpForm) {
        ComFieldsEntity entity = BeanUtil.toBean(comFieldsUpForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(), id)) {
            return ServiceResult.error(MsgCode.EXIST001.get());
        }
        boolean flag = comFieldsService.update(id, entity);
        if (!flag) {
            return ServiceResult.error(MsgCode.FA002.get());
        }
        return ServiceResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除常用字段
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除常用字段")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataModel")
    @DeleteMapping("/{id}")
    public ServiceResult delete(@PathVariable("id") String id) {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        if (entity != null) {
            comFieldsService.delete(entity);
            return ServiceResult.success(MsgCode.SU003.get());
        }
        return ServiceResult.error(MsgCode.FA003.get());
    }
}

