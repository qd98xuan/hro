package com.linzen.controller;


import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.entity.ContractEntity;
import com.linzen.model.ContractForm;
import com.linzen.model.ContractInfoVO;
import com.linzen.model.ContractListVO;
import com.linzen.service.ContractService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Contract
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息
 * @date 2023-04-01
 */
@RestController
@Tag(name = "示例接口", description = "Contract")
@RequestMapping("/Contract")
public class ContractController extends SuperController<ContractService, ContractEntity> {

    @Autowired
    private ContractService contractService;

    /**
     * 获取列表
     *
     * @param pagination Pagination 分页模型
     * @return ServiceResult
     */
    @Operation(summary = "获取列表")
    @GetMapping("/List")
    public ServiceResult<PageListVO<ContractListVO>> list(Pagination pagination) {
        List<ContractEntity> entity = contractService.getlist(pagination);
        List<ContractListVO> listVo = JsonUtil.createJsonToList(JsonUtil.formatObjectToStringDate(entity, "yyyy-MM-dd HH:mm:ss"), ContractListVO.class);
        PaginationVO vo = BeanUtil.toBean(pagination, PaginationVO.class);
        return ServiceResult.pageList(listVo, vo);
    }

    /**
     * 获取详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<ContractInfoVO> info(@PathVariable("id") String id) {
        ContractEntity entity = contractService.getInfo(id);
        ContractInfoVO vo = BeanUtil.toBean(entity, ContractInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 新建
     *
     * @param contractForm 新建模型
     * @return
     */
    @Operation(summary = "新建")
    @PostMapping
    @Parameters({
            @Parameter(name = "contractForm", description = "示例模型", required = true),
    })
    public ServiceResult create(@RequestBody @Valid ContractForm contractForm) {
        ContractEntity entity = BeanUtil.toBean(contractForm, ContractEntity.class);
        contractService.create(entity);
        return ServiceResult.success("保存成功");
    }

    /**
     * @param id           主键
     * @param contractForm 修改模型
     * @return
     */
    @Operation(summary = "修改")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "contractForm", description = "示例模型", required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid ContractForm contractForm) {
        ContractEntity entity = BeanUtil.toBean(contractForm, ContractEntity.class);
        contractService.update(id, entity);
        return ServiceResult.success("修改成功");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult delete(@PathVariable("id") String id) {
        ContractEntity entity = contractService.getInfo(id);
        contractService.delete(entity);
        return ServiceResult.success("删除成功");
    }

}
