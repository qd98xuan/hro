package com.linzen.form.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.controller.SuperController;
import com.linzen.constant.MsgCode;
import com.linzen.engine.enums.FlowStatusEnum;
import com.linzen.exception.WorkFlowException;
import com.linzen.form.entity.SalesOrderEntity;
import com.linzen.form.entity.SalesOrderEntryEntity;
import com.linzen.form.model.salesorder.SalesOrderEntryEntityInfoModel;
import com.linzen.form.model.salesorder.SalesOrderForm;
import com.linzen.form.model.salesorder.SalesOrderInfoVO;
import com.linzen.form.service.SalesOrderService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售订单
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "销售订单", description = "SalesOrder")
@RestController
@RequestMapping("/api/workflow/Form/SalesOrder")
public class SalesOrderController extends SuperController<SalesOrderService, SalesOrderEntity> {

    @Autowired
    private SalesOrderService salesOrderService;

    /**
     * 获取销售订单信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取销售订单信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult info(@PathVariable("id") String id) {
        SalesOrderEntity entity = salesOrderService.getInfo(id);
        List<SalesOrderEntryEntity> entityList = salesOrderService.getSalesEntryList(id);
        SalesOrderInfoVO vo = BeanUtil.toBean(entity, SalesOrderInfoVO.class);
        if (vo != null) {
            vo.setEntryList(JsonUtil.createJsonToList(entityList, SalesOrderEntryEntityInfoModel.class));
        }
        return ServiceResult.success(vo);
    }

    /**
     * 新建销售订单
     *
     * @param salesOrderForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "新建销售订单")
    @PostMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "salesOrderForm", description = "销售模型", required = true),
    })
    public ServiceResult create(@RequestBody SalesOrderForm salesOrderForm, @PathVariable("id") String id) throws WorkFlowException {
        SalesOrderEntity sales = BeanUtil.toBean(salesOrderForm, SalesOrderEntity.class);
        List<SalesOrderEntryEntity> salesEntryList = JsonUtil.createJsonToList(salesOrderForm.getEntryList(), SalesOrderEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesOrderForm.getStatus())) {
            salesOrderService.save(id, sales, salesEntryList, salesOrderForm);
            return ServiceResult.success(MsgCode.SU002.get());
        }
        salesOrderService.submit(id, sales, salesEntryList, salesOrderForm);
        return ServiceResult.success(MsgCode.SU006.get());
    }

    /**
     * 修改销售订单
     *
     * @param salesOrderForm 表单对象
     * @param id             主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "修改销售订单")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "salesOrderForm", description = "销售模型", required = true),
    })
    public ServiceResult update(@RequestBody SalesOrderForm salesOrderForm, @PathVariable("id") String id) throws WorkFlowException {
        SalesOrderEntity sales = BeanUtil.toBean(salesOrderForm, SalesOrderEntity.class);
        sales.setId(id);
        List<SalesOrderEntryEntity> salesEntryList = JsonUtil.createJsonToList(salesOrderForm.getEntryList(), SalesOrderEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesOrderForm.getStatus())) {
            salesOrderService.save(id, sales, salesEntryList, salesOrderForm);
            return ServiceResult.success(MsgCode.SU002.get());
        }
        salesOrderService.submit(id, sales, salesEntryList, salesOrderForm);
        return ServiceResult.success(MsgCode.SU006.get());
    }
}
