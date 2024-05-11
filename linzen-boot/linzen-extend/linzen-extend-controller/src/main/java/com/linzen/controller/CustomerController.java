package com.linzen.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.Pagination;
import com.linzen.base.controller.SuperController;
import com.linzen.base.vo.PageListVO;
import com.linzen.constant.MsgCode;
import com.linzen.entity.CustomerEntity;
import com.linzen.model.customer.CustomerCrForm;
import com.linzen.model.customer.CustomerInfoVO;
import com.linzen.model.customer.CustomerListVO;
import com.linzen.model.customer.CustomerUpForm;
import com.linzen.service.CustomerService;
import com.linzen.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 客户信息
 *
 * @version V0.0.1
 * @copyright 领致信息
 * @author FHNP
 * @date 2023-04-01
 */
@Slf4j
@RestController
@Tag(name = "客户信息", description = "Customer")
@RequestMapping("/api/extend/saleOrder/Customer")
public class CustomerController extends SuperController<CustomerService, CustomerEntity> {

    @Autowired
    private CustomerService customerService;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @GetMapping
    @Operation(summary = "列表")
    public ServiceResult<PageListVO<CustomerListVO>> list(Pagination pagination) {
        pagination.setPageSize(50);
        pagination.setCurrentPage(1);
        List<CustomerEntity> list = customerService.getList(pagination);
        List<CustomerListVO> listVO = JsonUtil.createJsonToList(list, CustomerListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        return ServiceResult.success(vo);
    }

    /**
     * 创建
     *
     * @param customerCrForm 新建模型
     * @return
     */
    @PostMapping
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "customerCrForm", description = "客户模型",required = true),
    })
    public ServiceResult create(@RequestBody @Valid CustomerCrForm customerCrForm) {
        CustomerEntity entity = BeanUtil.toBean(customerCrForm, CustomerEntity.class);
        customerService.create(entity);
        return ServiceResult.success("新建成功");
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult<CustomerInfoVO> info(@PathVariable("id") String id) {
        CustomerEntity entity = customerService.getInfo(id);
        CustomerInfoVO vo = BeanUtil.toBean(entity, CustomerInfoVO.class);
        return ServiceResult.success(vo);
    }

    /**
     * 更新
     *
     * @param id             主键
     * @param customerUpForm 修改模型
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "customerUpForm", description = "客户模型", required = true),
    })
    public ServiceResult update(@PathVariable("id") String id, @RequestBody @Valid CustomerUpForm customerUpForm) {
        CustomerEntity entity = BeanUtil.toBean(customerUpForm, CustomerEntity.class);
        boolean ok = customerService.update(id, entity);
        if (ok) {
            return ServiceResult.success("更新成功");
        }
        return ServiceResult.error("更新失败，数据不存在");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ServiceResult delete(@PathVariable("id") String id) {
        CustomerEntity entity = customerService.getInfo(id);
        if (entity != null) {
            customerService.delete(entity);
        }
        return ServiceResult.success(MsgCode.SU003.get());
    }


}
